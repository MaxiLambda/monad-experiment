package lincks.maximilian.monads.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.monads.MonadConstructor;

import java.util.function.Function;

public class  Maybe<T> implements Monad<Maybe<?>,T>  {

    private final T value;
    private static final Maybe<?> nothing = new Maybe<>(null);

    @MonadConstructor
    public Maybe(T value) {
        this.value = value;
    }

    public static <R> Maybe<R> nothing() {
        return (Maybe<R>) nothing;
    }

    public T get() {
        return value;
    }

    @Override
    public <R> Monad<Maybe<?>, R> bind(Function<T, Monad<Maybe<?>, R>> f) {
        return nothing().equals(this) ? nothing() : f.apply(value);
    }

    public static <T> Maybe<T> unwrap(Monad<Maybe<?>, T> m) {
        return (Maybe<T>) m;
    }
}
