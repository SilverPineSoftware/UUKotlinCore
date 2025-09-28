package com.silverpine.uu.core

/**
 * A simple result type that represents either a successful value [Success] or a failure [Failure]
 * containing a [UUError]. This is similar in spirit to [kotlin.Result], but uses a fixed error type
 * instead of [Throwable].
 *
 * @param T the type of the success value.
 */
sealed class UUResult<out T>
{
    /**
     * Represents a successful result containing [value].
     *
     * @param T the type of the success value.
     * @property value the wrapped successful value.
     */
    data class Success<T>(val value: T) : UUResult<T>()

    /**
     * Represents a failed result containing a [UUError].
     *
     * @property error the error associated with the failure.
     */
    data class Failure(val error: UUError) : UUResult<Nothing>()

    /**
     * Returns the success value if this is a [Success], or `null` if it is a [Failure].
     */
    fun getOrNull(): T? = (this as? Success)?.value

    /**
     * Returns the [UUError] if this is a [Failure], or `null` if it is a [Success].
     */
    fun errorOrNull(): UUError? = (this as? Failure)?.error

    /**
     * Executes the given [block] if this is a [Success], passing the success value.
     *
     * @param block the lambda to execute on success.
     * @return this [UUResult] instance for call chaining.
     */
    inline fun onSuccess(block: (T) -> Unit): UUResult<T> = apply {
        if (this is Success) block(value)
    }

    /**
     * Executes the given [block] if this is a [Failure], passing the [UUError].
     *
     * @param block the lambda to execute on failure.
     * @return this [UUResult] instance for call chaining.
     */
    inline fun onFailure(block: (UUError) -> Unit): UUResult<T> = apply {
        if (this is Failure) block(error)
    }

    /**
     * Maps the success value to a new [UUResult] of type [R] using [transform].
     * If this is a [Failure], the error is propagated unchanged.
     *
     * @param R the new result type.
     * @param transform the mapping function applied to the success value.
     * @return a new [UUResult] containing the transformed value or the original error.
     */
    inline fun <R> map(transform: (T) -> R): UUResult<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    /**
     * Flat-maps the success value to another [UUResult] of type [R] using [transform].
     * This allows chaining operations that may themselves return [UUResult].
     *
     * @param R the new result type.
     * @param transform the transformation function returning another [UUResult].
     * @return the result of applying [transform], or the original failure.
     */
    inline fun <R> flatMap(transform: (T) -> UUResult<R>): UUResult<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    /**
     * Recovers from a failure by providing a fallback value via [recover].
     * If this is a [Success], the value is returned unchanged.
     *
     * @param recover the recovery function producing a fallback value from [UUError].
     * @return a [Success] containing either the original or fallback value.
     */
    inline fun recover(recover: (UUError) -> @UnsafeVariance T): UUResult<T> = when (this) {
        is Success -> this
        is Failure -> Success(recover(error))
    }

    /**
     * Folds the result into a single value by applying [onSuccess] if this is a success,
     * or [onFailure] if this is a failure.
     *
     * @param R the type of the folded result.
     * @param onSuccess the function applied to the success value.
     * @param onFailure the function applied to the error.
     * @return the folded result.
     */
    inline fun <R> fold(onSuccess: (T) -> R, onFailure: (UUError) -> R): R =
        when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(error)
        }

    companion object
    {
        /**
         * Creates a [Success] result containing the given [value].
         *
         * @param value the success value.
         */
        fun <T> success(value: T): UUResult<T> = Success(value)

        /**
         * Creates a [Failure] result containing the given [error].
         *
         * @param error the failure error.
         */
        fun <T> failure(error: UUError): UUResult<T> = Failure(error)
    }
}