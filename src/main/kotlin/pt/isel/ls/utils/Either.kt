package pt.isel.ls.utils

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()
}

// Functions for when using Either to represent success or failure
fun <R> success(value: R) = Either.Right(value)
fun <L> failure(error: L) = Either.Left(error)

typealias Success<S> = Either.Right<S>
typealias Failure<F> = Either.Left<F>

fun <L, R, T> handleEither(either: Either<L, R>, onSuccess: (R) -> Either<L, T>): Either<L, T> =
    when (either) {
        is Success -> onSuccess(either.value)
        is Failure -> either
    }

data class Condition<F>(val predicate: Boolean, val failure: F)

fun <F> handleConditions(conditions: List<Condition<F>>, onSuccess: () -> Unit): Either<F, Unit> =
    conditions.find { it.predicate }?.let { failure(it.failure) }
        ?: success(onSuccess())