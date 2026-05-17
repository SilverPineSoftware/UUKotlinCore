package com.silverpine.uu.core

/**
 * A simple result type that represents either a successful value [Success] or a failure [Failure]
 * containing an [ErrorType]. This is similar in spirit to [kotlin.Result], but uses a caller-defined
 * error type instead of [Throwable].
 *
 * @since 1.0.0
 * @param SuccessType the type of the success value.
 * @param ErrorType the type of the failure value.
 */
sealed class UUResult<out SuccessType, out ErrorType>
{
    /**
     * Represents a successful result containing [value].
     *
     * @since 1.0.0
     * @param T the type of the success value.
     * @property value the wrapped successful value.
     */
    data class Success<out T>(val value: T) : UUResult<T, Nothing>()

    /**
     * Represents a failed result containing [error].
     *
     * @since 1.0.0
     * @param E the type of the error value.
     * @property error the error associated with the failure.
     */
    data class Failure<out E>(val error: E) : UUResult<Nothing, E>()

    /**
     * Returns the success value if this is a [Success], or `null` if it is a [Failure].
     *
     * @since 1.0.0
     */
    fun getOrNull(): SuccessType? = (this as? Success)?.value

    /**
     * Returns the error if this is a [Failure], or `null` if it is a [Success].
     *
     * @since 1.0.0
     */
    fun errorOrNull(): ErrorType? = (this as? Failure)?.error

    /**
     * Executes the given [block] if this is a [Success], passing the success value.
     *
     * @since 1.0.0
     * @param block the lambda to execute on success.
     * @return this [UUResult] instance for call chaining.
     */
    inline fun onSuccess(block: (SuccessType) -> Unit): UUResult<SuccessType, ErrorType> = apply {
        if (this is Success) block(value)
    }

    /**
     * Executes the given [block] if this is a [Failure], passing the error.
     *
     * @since 1.0.0
     * @param block the lambda to execute on failure.
     * @return this [UUResult] instance for call chaining.
     */
    inline fun onFailure(block: (ErrorType) -> Unit): UUResult<SuccessType, ErrorType> = apply {
        if (this is Failure) block(error)
    }

    /**
     * Maps the success value to a new [UUResult] of type [R] using [transform].
     * If this is a [Failure], the error is propagated unchanged.
     *
     * @since 1.0.0
     * @param R the new result type.
     * @param transform the mapping function applied to the success value.
     * @return a new [UUResult] containing the transformed value or the original error.
     */
    inline fun <R> map(transform: (SuccessType) -> R): UUResult<R, ErrorType> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    /**
     * Flat-maps the success value to another [UUResult] of type [R] using [transform].
     * This allows chaining operations that may themselves return [UUResult].
     *
     * @since 1.0.0
     * @param R the new result type.
     * @param transform the transformation function returning another [UUResult].
     * @return the result of applying [transform], or the original failure.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <R> flatMap(transform: (SuccessType) -> UUResult<R, *>): UUResult<R, ErrorType> = when (this) {
        is Success -> transform(value) as UUResult<R, ErrorType>
        is Failure -> this
    }

    /**
     * Recovers from a failure by providing a fallback value via [recover].
     * If this is a [Success], the value is returned unchanged.
     *
     * @since 1.0.0
     * @param recover the recovery function producing a fallback value from the error.
     * @return a [Success] containing either the original or fallback value.
     */
    inline fun recover(recover: (ErrorType) -> @UnsafeVariance SuccessType): UUResult<SuccessType, ErrorType> = when (this) {
        is Success -> this
        is Failure -> Success(recover(error))
    }

    /**
     * Folds the result into a single value by applying [onSuccess] if this is a success,
     * or [onFailure] if this is a failure.
     *
     * @since 1.0.0
     * @param R the type of the folded result.
     * @param onSuccess the function applied to the success value.
     * @param onFailure the function applied to the error.
     * @return the folded result.
     */
    inline fun <R> fold(onSuccess: (SuccessType) -> R, onFailure: (ErrorType) -> R): R =
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(error)
        }

    /**
     * Returns the success value if this is [Success], or the result of [onFailure] if this is [Failure].
     *
     * @since 1.0.0
     * @param onFailure called when this is [Failure] to produce a fallback value.
     * @return the success value or the fallback.
     */
    inline fun getOrElse(onFailure: (ErrorType) -> @UnsafeVariance SuccessType): SuccessType = when (this)
    {
        is Success -> value
        is Failure -> onFailure(error)
    }

    companion object
    {
        /**
         * Creates a [Success] result containing the given [value].
         *
         * @since 1.0.0
         * @param value the success value.
         */
        fun <T> success(value: T): UUResult<T, Nothing> = Success(value)

        /**
         * Creates a [Failure] result containing the given [error].
         *
         * @since 1.0.0
         * @param error the failure error.
         */
        fun <T, E> failure(error: E): UUResult<T, E> = Failure(error)
    }
}
