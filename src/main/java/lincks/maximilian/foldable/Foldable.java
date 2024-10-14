package lincks.maximilian.foldable;

import lincks.maximilian.monadpluszero.MonadPlusZero;
import lincks.maximilian.util.Bottom;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lincks.maximilian.monadzero.MonadZero.zero;

public interface Foldable<F extends Foldable<F, ?>, T> extends Bottom<F,T> {
    //with Monoid instead of MonadPlus, this would be more powerful
    //monoids need to be implemented with classes providing a mempty and mconcat operation
    //maybe use instances instead of generic classes

    default <M extends MonadPlusZero<M, R>, R> MonadPlusZero<M, R> foldMap(Function<T, MonadPlusZero<M, R>> f, Class<M> clazz) {
        return foldr((T t, MonadPlusZero<M, R> acc) -> (MonadPlusZero<M, R>) acc.mplus(f.apply(t)), zero(clazz));
    }

    //without Monoids, this probably can't be expressed as foldMap
    <R> R foldr(BiFunction<T, R, R> acc, R identity);
}
