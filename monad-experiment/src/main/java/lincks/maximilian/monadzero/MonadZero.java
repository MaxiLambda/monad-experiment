package lincks.maximilian.monadzero;

import lincks.maximilian.monads.Monad;

import java.util.function.Predicate;

public interface MonadZero<M extends MonadZero<M,?>,T> extends Monad<M,T>, Zero<M,T> {

    default MonadZero<M, T>  filter(Predicate<T> p) {
        return (MonadZero<M, T>) bind(val -> p.test(val) ? (Monad<M, Object>) this : Zero.zero(this.getClass()));
    }
}
