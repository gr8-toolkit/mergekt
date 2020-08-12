package tech.pm.mergekt.intergation

import tech.pm.mergekt.compileSchemaCompiler
import tech.pm.mergekt.create
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotlintest.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tech.pm.mergekt.api.Optional
import tech.pm.mergekt.compiler.MergeMethodGenerator

internal class OneOptionalFieldClass {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    private val packageName by lazy { "com.pm.test" }
    private val className by lazy { "SomeLine" }

    @Test
    fun `Merge NoData with data`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val lineClass = getEntityClass(result)

        val data = Optional.Data(27L)
        val initState = lineClass.create(Optional.NoData)
        val newState = lineClass.create(data)

        val mergeResult = getMergeResult(result, lineClass, initState, newState)

        val getCountMethod = lineClass.getDeclaredMethod("getCount")
        getCountMethod.invoke(mergeResult) shouldBe data
    }

    @Test
    fun `Merge data with data`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val lineClass = getEntityClass(result)

        val data = Optional.Data(27L)
        val initState = lineClass.create(Optional.Data(15L))
        val newState = lineClass.create(data)

        val mergeResult = getMergeResult(result, lineClass, initState, newState)

        val getCountMethod = lineClass.getDeclaredMethod("getCount")
        getCountMethod.invoke(mergeResult) shouldBe data
    }

    @Test
    fun `Merge data with no data`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val lineClass = getEntityClass(result)

        val data = Optional.Data(15L)
        val initState = lineClass.create(data)
        val newState = lineClass.create(Optional.NoData)

        val mergeResult = getMergeResult(result, lineClass, initState, newState)

        val getCountMethod = lineClass.getDeclaredMethod("getCount")
        getCountMethod.invoke(mergeResult) shouldBe data
    }

    private fun getMergeResult(
        result: KotlinCompilation.Result,
        lineClass: Class<*>,
        initState: Any,
        newState: Any
    ): Any? {
        val extensionFileName = buildString {
            append(packageName)
            append(".")
            append(className)
            append(MergeMethodGenerator.EXTENSION_SUFFIX)
            append("Kt")
        }

        val extensionsFile = result.classLoader.loadClass(extensionFileName)

        val mergeMethod = extensionsFile.getDeclaredMethod(MergeMethodGenerator.MERGE_METHOD_NAME, lineClass, lineClass)
        return mergeMethod.invoke(null, initState, newState)
    }

    private fun getEntityClass(result: KotlinCompilation.Result) =
        result.classLoader.loadClass("$packageName.$className")

    private fun prepareData(): KotlinCompilation.Result {
        return compileSchemaCompiler(
            temporaryFolder,
            SourceFile.kotlin("source.kt", createTemplate())
        )
    }

    private fun createTemplate(): String {
        return """
            package $packageName
            
            import com.parimatch.mergekt.api.NeedMergeMethod
            import com.parimatch.mergekt.api.Optional

            @NeedMergeMethod
            data class $className(
                val count: Optional<Long>
            )
        """
    }
}
