package lincks.maximilian.monads;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

import static lincks.maximilian.monads.MonadPure.pure;


public interface Monad<M extends Monad<M, ?>,T> {

    <R> Monad<M,R> bind(Function<T,Monad<M,R>> f);

    default <R> Monad<M,R> map(Function<T,R> f) {
        return this.bind(f.andThen(pure(this.getClass())));
    }

    default M getM() {
        return (M) this;
    }
}
