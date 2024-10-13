package lincks.maximilian.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativeConstructorDelegate;

import java.util.function.Function;
import java.util.function.Supplier;

@ApplicativeConstructorDelegate(clazz = Either.Right.class)
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

    @Override
    default <R> Either<F, R> then(Supplier<Monad<Either<F, ?>, R>> f) {
        return unwrap(Monad.super.then(f));
    }

    record Right<F, T>(T value) implements Either<F, T> {
        @ApplicativeConstructor
        public Right {
        }
    }

    record Left<F, T>(F value) implements Either<F, T> {
    }
}
