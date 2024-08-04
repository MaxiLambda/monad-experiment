package lincks.maximilian.monads.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.monads.MonadConstructor;
import lincks.maximilian.monads.MonadConstructorDelegate;

import java.util.function.Function;

@MonadConstructorDelegate(clazz = Either.Right.class)
public sealed interface Either<F, T> extends Monad<Either<F, ?>, T> {

    static <F, T> Either<F, T> unwrap(Monad<Either<F, ?>, T> m) {
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

    record Right<F, T>(T value) implements Either<F, T> {
        @MonadConstructor
        public Right {
        }
    }

    record Left<F, T>(F value) implements Either<F, T> {
    }
}
