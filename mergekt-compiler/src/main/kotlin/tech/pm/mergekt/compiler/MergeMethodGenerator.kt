package tech.pm.mergekt.compiler

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName
import tech.pm.mergekt.api.Optional
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

internal class MergeMethodGenerator(private val elementsUtils: Elements) {

    fun generate(element: Element): FileSpec {
        val fileName = element.simpleName.toString() + EXTENSION_SUFFIX

        val allFields = elementsUtils.getAllMembers(element as TypeElement)
            .filter { it.kind == ElementKind.FIELD }

        val typeOptionalFields = allFields
            .filter { it.asType().parameterizedRawType == Optional::class.asTypeName() }

        val listFields = allFields
            //todo find a better solution
            .filter { it.asType().parameterizedRawType.toString() == "java.util.List" }

        val funSpec = FunSpec.builder(MERGE_METHOD_NAME)
            .addModifiers(KModifier.INTERNAL)
            .receiver(element.asType().asTypeName())
            .addParameter("new", element.asType().asTypeName())
            .addComment(typeOptionalFields.toString())

        typeOptionalFields.forEach { field ->
            funSpec.beginControlFlow("val $INTERNAL_FIELD_PREFIX$field = if (new.$field != %T)", Optional.NoData::class)
                .addStatement("new.$field")
                .nextControlFlow("else")
                .addStatement("this.$field")
                .endControlFlow()
        }

        listFields.forEach { field ->
            funSpec.beginControlFlow("val $INTERNAL_FIELD_PREFIX$field = if (new.$field.isNotEmpty())")
                .addStatement("new.$field")
                .nextControlFlow("else")
                .addStatement("this.$field")
                .endControlFlow()
        }

        val arguments = typeOptionalFields.plus(listFields).joinToString(separator = ",") { field ->
            buildString {
                append(field)
                append(" = ")
                append(INTERNAL_FIELD_PREFIX)
                append(field)
            }
        }

        funSpec.returns(element.asType().asTypeName())
            .addStatement("return this.copy($arguments)", element)

        return FileSpec.builder(elementsUtils.getPackageOf(element).toString(), fileName)
            .addFunction(funSpec.build())
            .build()
    }

    companion object {
        private const val INTERNAL_FIELD_PREFIX = "merged"
        internal const val EXTENSION_SUFFIX = "Extensions"

        //Don't change (part of API)
        internal const val MERGE_METHOD_NAME = "merge"
    }
}
