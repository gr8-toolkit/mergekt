package tech.pm.mergekt.compiler

import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import tech.pm.mergekt.api.NeedMergeMethod
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Suppress("RedundantVisibilityModifier", "unused")
@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
internal class MergeCompiler : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(NeedMergeMethod::class.java.canonicalName)
    }

    private val messager: Messager
        get() = processingEnv.messager

    private val generator: MergeMethodGenerator by lazy {
        MergeMethodGenerator(processingEnv.elementUtils)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) {
            // An error was raised in the previous round. Don't try anything for now to avoid adding
            // possible more noise.
            return false
        }
        if (annotations.isEmpty()) {
            return false
        }

        val elements = roundEnv.getElementsAnnotatedWith(NeedMergeMethod::class.java)

        try {
            elements.forEach {
                generator.generate(it).writeTo(processingEnv.filer)
            }

        } catch (e: RuntimeException) {
            e.printStackTrace()
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Compilation failed; see the compiler error output for details ($e)"
            )
        }

        return true
    }
}
