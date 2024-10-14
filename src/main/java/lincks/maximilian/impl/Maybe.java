package lincks.maximilian.impl;

import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.Function;
import java.util.function.Supplier;

@ToString
@EqualsAndHashCode
public class Maybe<T> implements MonadPlus<Maybe<?>, T> {

    private static final Maybe<Void> nothing = new Maybe<>(null);
    private final T value;

    @ApplicativeConstructor
    public Maybe(T value) {
        this.value = value;
    }

    public static <R> Maybe<R> nothing() {
        return (Maybe<R>) nothing;
    }

    public static <T> Maybe<T> unwrap(Monad<Maybe<?>, T> m) {
        return (Maybe<T>) m;
    }

    public T get() {
        return value;
    }

    @Override
    public <R> Maybe<R> bind(Function<T, Monad<Maybe<?>, R>> f) {
        return nothing().equals(this) ? nothing() : f.andThen(Maybe::unwrap).apply(value);
    }

    @Override
    public <R> Maybe<R> map(Function<T, R> f) {
        return unwrap(MonadPlus.super.map(f));
    }

    @Override
    public <R> Maybe<R> then(Supplier<Monad<Maybe<?>, R>> f) {
        return unwrap(MonadPlus.super.then(f));
    }

    @Override
    public Maybe<T> mplus(MonadPlus<Maybe<?>, T> other) {
        return nothing().equals(this) ? nothing() : unwrap(other);
    }
}
