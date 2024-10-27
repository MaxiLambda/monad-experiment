package lincks.maximilian.monadpluszero;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.monadplus.MonadPlus;

import java.util.function.Supplier;

/**
 * TODO remove. alternative and mplus have different semantics. The default should not be that they can be used interchangeably
 * Unifies {@link Alternative} with {@link MonadPlus}.
 * MUST overwrite either {@link #alternative(Supplier)} or {@link #mplus}.
 * Overwriting both might be useful as they have different semantics.
 */
public interface MonadPlusZero<M extends MonadPlusZero<M, ?>, T> extends MonadPlus<M, T>, Alternative<M, T> {

    @Override
    default MonadPlusZero<M, T> alternative(Supplier<? extends Alternative<M, T>> other) {
        return mplus((MonadPlusZero<M, T>) other.get());
    }

    @Override
    default MonadPlusZero<M, T> mplus(MonadPlus<M, T> other) {
        return alternative(() -> (Alternative<M, T>) other);
    }
}
