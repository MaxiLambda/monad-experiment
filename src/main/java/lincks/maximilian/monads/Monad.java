package lincks.maximilian.monads;

import java.util.function.Function;

import static lincks.maximilian.monads.MonadPure.pure;


public interface Monad<M extends Monad<M, T>,T> {

    <M2 extends Monad<M2,R>, R> M2  bind(Function<T,M2> f);

    default <M2 extends Monad<M2,R>, R> M2 map(Function<T,R> f) {
        return (M2) this.bind(f.andThen(pure(this.getClass())));
    }
}
