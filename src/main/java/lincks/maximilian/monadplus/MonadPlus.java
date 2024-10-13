package lincks.maximilian.monadplus;

import lincks.maximilian.monads.Monad;

public interface MonadPlus<M extends MonadPlus<M, ?>, T> extends Monad<M, T> {
    MonadPlus<M,T> mplus(MonadPlus<M,T> other);
}
