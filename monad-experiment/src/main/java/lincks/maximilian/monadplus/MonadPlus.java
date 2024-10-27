package lincks.maximilian.monadplus;

import lincks.maximilian.monads.Monad;

/**
 * Describes how two instances of a Monad of the same type can be merged.
 * Extends the functionality of regular Monads.
 */
public interface MonadPlus<M extends MonadPlus<M, ?>, T> extends Monad<M, T> {
    MonadPlus<M, T> mplus(MonadPlus<M, T> other);
}
