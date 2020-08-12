package tech.pm.mergekt

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.rules.TemporaryFolder
import tech.pm.mergekt.compiler.MergeCompiler
import javax.annotation.processing.AbstractProcessor

internal fun compileSchemaCompiler(
    temporaryFolder: TemporaryFolder,
    vararg sourceFiles: SourceFile
): KotlinCompilation.Result {
    return prepareCompilation(temporaryFolder, MergeCompiler(), *sourceFiles).compile()
}

internal fun prepareCompilation(
    temporaryFolder: TemporaryFolder,
    compiler: AbstractProcessor,
    vararg sourceFiles: SourceFile
): KotlinCompilation {
    return KotlinCompilation()
        .apply {
            workingDir = temporaryFolder.root
            annotationProcessors = listOf(compiler)
            inheritClassPath = true
            sources = sourceFiles.asList()
            verbose = false
        }
}

internal fun Class<*>.create(vararg args: Any): Any {
    return this.constructors.first().newInstance(*args)
}
