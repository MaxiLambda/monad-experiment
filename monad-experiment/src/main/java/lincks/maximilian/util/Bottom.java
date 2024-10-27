package lincks.maximilian.util;

import lincks.maximilian.foldable.Foldable;
import lincks.maximilian.monadzero.MonadZero;

/**
 * Helper Interface, used so "unwrap" functions can be implemented on all Implementations of {@link lincks.maximilian.functor.Functor}
 * {@link MonadZero} ,and {@link Foldable} and all their descendents.
 *
 * @param <B> the Type wrapping T.
 * @param <T> the Type which is wrapped.
 */
public interface Bottom<B extends Bottom<B, ?>, T> {
}

