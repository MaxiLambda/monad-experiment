package lincks.maximilian.foldable;

import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monadplus.MonadPlusZero;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

import static lincks.maximilian.monadplus.MonadPlusZero.zero;

public interface Foldable<F extends Foldable<F, ?>, T> {
    //with Monoid instead of MonadPlus, this would be more powerful
    //monoids need to be implemented with classes providing a mempty and mconcat operation
    //maybe use instances instead of generic classes

    default <M extends MonadPlus<M, R>, R> MonadPlus<M, R> foldMap(Function<T, MonadPlus<M, R>> f, Class<M> clazz) {
        return foldr((T t, MonadPlus<M, R> acc) -> acc.mplus(f.apply(t)), zero(clazz));
    }

    //without Monoids, this probably can't be expressed as foldMap
    <R> R foldr(BiFunction<T,R,R> acc, R identity);
}
