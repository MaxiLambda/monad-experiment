package lincks.maximilian.functor;

import lincks.maximilian.util.Bottom;

import java.util.function.Function;

public interface Functor<F extends Functor<F, ?>, T> extends Bottom<F,T> {
    <R> Functor<F, R> map(Function<T, R> f);
}
