package lincks.maximilian.functor;

import java.util.function.Function;

public interface Functor<F extends Functor<F,?>,T> {
    <R> Functor<F,R> map(Function<T,R> f);
}
