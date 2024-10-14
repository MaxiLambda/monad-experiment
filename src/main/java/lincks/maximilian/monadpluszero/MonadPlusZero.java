package lincks.maximilian.monadpluszero;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.monadplus.MonadPlus;

import java.util.function.Supplier;

public interface MonadPlusZero<M extends MonadPlusZero<M, ?>, T> extends MonadPlus<M, T>, Alternative<M, T> {

    @Override
    default MonadPlusZero<M, T> alternative(Supplier<Alternative<M, T>> other) {
        return (MonadPlusZero<M, T>) mplus((MonadPlusZero<M, T>) other.get());
    }

    @Override
    default MonadPlus<M, T> mplus(MonadPlus<M, T> other) {
        return alternative(() -> (Alternative<M, T>) other);
    }
}
