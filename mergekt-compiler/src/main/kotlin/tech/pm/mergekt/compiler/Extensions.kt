package tech.pm.mergekt.compiler

import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.type.TypeMirror

val TypeName.parameterizedRawType
    get() = (this as? ParameterizedTypeName)?.rawType

val TypeMirror.parameterizedRawType
    get() = this.asTypeName().parameterizedRawType
