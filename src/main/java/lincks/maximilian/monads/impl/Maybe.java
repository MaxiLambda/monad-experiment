package lincks.maximilian.monads.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.monads.MonadConstructor;

import java.util.function.Function;

public class Maybe<T> implements Monad<Maybe<T>,T>  {

    private final T value;
    public static final Maybe<?> nothing = new Maybe<>(null);

    @MonadConstructor
    public Maybe(T value) {
        this.value = value;
    }


    private <R> Maybe<R> nothing() {
        return (Maybe<R>) nothing;
    }

    public T get() {
        return value;
    }

    @Override
    public <M2 extends Monad<M2, R>, R> M2 bind(Function<T, M2> f) {
        return this.equals(nothing()) ? (M2) nothing() : f.apply(value);
    }
}
