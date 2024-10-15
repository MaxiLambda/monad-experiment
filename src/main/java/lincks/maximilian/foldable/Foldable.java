package lincks.maximilian.foldable;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.monadpluszero.MonadPlusZero;
import lincks.maximilian.util.Bottom;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lincks.maximilian.monadzero.MonadZero.zero;

public interface Foldable<F extends Foldable<F, ?>, T> extends Bottom<F,T> {
    //with Monoid instead of MonadPlusZero/Alternative, foldMap would be more powerful
    //monoids need to be implemented with classes providing a mempty and mconcat operation
    //maybe use instances instead of generic classes

    /** foldMap implementation based on {@link Alternative}. */
    default <A extends Alternative<A, R>, R> Alternative<A, R> foldMapA(Function<T, Alternative<A, R>> f, Class<A> clazz) {
        return foldr((T t, Alternative<A, R> acc) -> acc.alternative(() -> f.apply(t)), zero(clazz));
    }

    /** foldMap implementation based on {@link MonadPlusZero} */
    default <M extends MonadPlusZero<M, R>, R> MonadPlusZero<M, R> foldMapM(Function<T, MonadPlusZero<M, R>> f, Class<M> clazz) {
        return foldr((T t, MonadPlusZero<M, R> acc) -> acc.alternative(() -> f.apply(t)), zero(clazz));
    }

    //without Monoids, this probably can't be expressed as foldMap/ or at lest not in a useful way
    <R> R foldr(BiFunction<T, R, R> acc, R identity);
}
