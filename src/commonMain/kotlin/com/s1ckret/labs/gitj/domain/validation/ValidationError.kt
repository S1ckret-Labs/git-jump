package com.s1ckret.labs.gitj.domain.validation

import arrow.core.Validated
import arrow.core.ValidatedNel

abstract class ValidationError(
    open val message: String,
    open val map: Map<String, Any> = emptyMap()
)

typealias Validation<A> = ValidatedNel<ValidationError, A>

inline fun <A> A.ok(): ValidatedNel<Nothing, A> =
    Validated.validNel(this)

inline fun <E> E.err(): ValidatedNel<E, Nothing> =
    Validated.invalidNel(this)
