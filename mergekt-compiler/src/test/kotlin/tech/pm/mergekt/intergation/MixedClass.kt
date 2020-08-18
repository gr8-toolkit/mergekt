package tech.pm.mergekt.intergation

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotlintest.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import tech.pm.mergekt.api.Optional
import tech.pm.mergekt.compileSchemaCompiler
import tech.pm.mergekt.compiler.MergeMethodGenerator
import tech.pm.mergekt.create

internal class MixedClass {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    private val packageName by lazy { "com.pm.test" }
    private val className by lazy { "SomeLine" }

    @Test
    fun `Merge no data with data`() {
        val result = prepareData()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val lineClass = getEntityClass(result)

        val id = "id"
        val data = Optional.Data(27L)
        val initState = lineClass.create(id, Optional.NoData)
        val newState = lineClass.create(id, data)

        val mergeResult = getMergeResult(result, lineClass, initState, newState)

        val getIdMethod = lineClass.getDeclaredMethod("getId")
        getIdMethod.invoke(mergeResult) shouldBe id

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
        @Suppress("UnnecessaryVariable")
        @Language("kotlin") val result = """
            package $packageName
            
            import tech.pm.mergekt.api.NeedMergeMethod
            import tech.pm.mergekt.api.Optional

            @NeedMergeMethod
            data class $className(
                val id : String,
                val count: Optional<Long>
            )
        """

        return result
    }
}
