package tech.pm.mergekt.intergation

import tech.pm.mergekt.compileSchemaCompiler
import tech.pm.mergekt.create
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotlintest.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tech.pm.mergekt.api.Optional
import tech.pm.mergekt.compiler.MergeMethodGenerator

internal class TwoOptionalFieldClass {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    private val packageName by lazy { "com.pm.test" }
    private val className by lazy { "SomeLine" }

    @Test
    fun `Update two field by one update`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val lineClass = getEntityClass(result)

        val finalCount = Optional.Data(27L)
        val finalName = Optional.Data("test")

        val initState = lineClass.create(Optional.NoData, Optional.NoData)
        val newState = lineClass.create(finalCount, finalName)

        val mergeResult = getMergeResult(result, lineClass, initState, newState)

        checkData(lineClass, mergeResult, finalCount, finalName)
    }

    @Test
    fun `Update two field by one by one`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val testClass = getEntityClass(result)

        val finalCount = Optional.Data(27L)
        val finalName = Optional.Data("new name")

        val initName = Optional.Data("old string")

        val initState = testClass.create(Optional.Data(12L), initName)
        val countUpdate = testClass.create(finalCount, Optional.NoData)
        val stringUpdate = testClass.create(Optional.NoData, finalName)

        val mergeNewCountResult = getMergeResult(result, testClass, initState, countUpdate)

        checkData(testClass, mergeNewCountResult, finalCount, initName)

        val mergeNewStringResult = getMergeResult(result, testClass, mergeNewCountResult, stringUpdate)

        checkData(testClass, mergeNewStringResult, finalCount, finalName)
    }

    private fun checkData(entityClass: Class<*>, state: Any, count: Optional<Long>, name: Optional<String>) {
        val getCountMethod = entityClass.getDeclaredMethod("getCount")
        getCountMethod.invoke(state) shouldBe count

        val getNameMethod = entityClass.getDeclaredMethod("getName")
        getNameMethod.invoke(state) shouldBe name
    }

    private fun getMergeResult(
        result: KotlinCompilation.Result,
        entityClass: Class<*>,
        initState: Any,
        newState: Any
    ): Any {
        val extensionFileName = buildString {
            append(packageName)
            append(".")
            append(className)
            append(MergeMethodGenerator.EXTENSION_SUFFIX)
            append("Kt")
        }

        val extensionsFile = result.classLoader.loadClass(extensionFileName)

        val mergeMethod =
            extensionsFile.getDeclaredMethod(MergeMethodGenerator.MERGE_METHOD_NAME, entityClass, entityClass)

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
        @Suppress("UnnecessaryVariable")
        @Language("kotlin") val result = """
            package $packageName
            
            import tech.pm.mergekt.api.NeedMergeMethod
            import tech.pm.mergekt.api.Optional

            @NeedMergeMethod
            data class $className(
                val count: Optional<Long>,
                val name: Optional<String>
            )
        """

        return result
    }
}
