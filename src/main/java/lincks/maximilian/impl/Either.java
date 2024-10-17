package lincks.maximilian.impl;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativeConstructorDelegate;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.util.Bottom;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Monad describing values which might be missing, but a default or a reason is provided.
 */
@ApplicativeConstructorDelegate(clazz = Either.Right.class)
public sealed interface Either<F, T> extends Monad<Either<F, ?>, T>, Alternative<Either<F, ?>, T> {

    /**
     * Unwraps an Either if it is wrapped in some other type.
     *
     * @param m the wrapped up Either
     * @return the cast Either
     */
    static <F, T> Either<F, T> unwrap(Bottom<Either<F, ?>, T> m) {
        return (Either<F, T>) m;
    }

    @Override
    default <R> Either<F, R> bind(Function<T, Monad<Either<F, ?>, R>> f) {
        return switch (this) {
            case Left(F value) -> new Left<>(value);
            case Right(T value) -> f.andThen(Either::unwrap).apply(value);
        };
    }

    @Override
    default <R> Either<F, R> map(Function<T, R> f) {
        return unwrap(Monad.super.map(f));
    }

    @Override
    default <R> Either<F, R> then(Supplier<Monad<Either<F, ?>, R>> f) {
        return unwrap(Monad.super.then(f));
    }

    @Override
    default Either<F, T> alternative(Supplier<? extends Alternative<Either<F, ?>, T>> other) {
        return switch (this) {
            case Left(F ignored) -> unwrap(other.get());
            case Right(T ignored) -> this;
        };
    }

    /**
     * Either Implementation with the "success" value
     *
     * @param value the value
     */
    record Right<F, T>(T value) implements Either<F, T> {
        @ApplicativeConstructor
        public Right {
        }
    }

    /**
     * Either Implementation with the "failure" value
     *
     * @param value the value
     */
    record Left<F, T>(F value) implements Either<F, T> {
    }
}
