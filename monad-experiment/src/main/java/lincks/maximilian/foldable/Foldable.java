package lincks.maximilian.foldable;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monadpluszero.MonadPlusZero;
import lincks.maximilian.monadzero.Zero;
import lincks.maximilian.util.TBF;
import lincks.maximilian.util.TF;
import lincks.maximilian.util.Top;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static lincks.maximilian.monadzero.Zero.zero;

/**
 * Represents a collection of values that can be reduced to a single value.
 *
 * @param <F> the type of the Foldable
 * @param <T> the type the foldable wraps over
 */
public interface Foldable<F extends Foldable<F, ?>, T> extends Top<F, T> {

    /**
     * Reduce a foldable to a single value by applying a function repeatedly to its values and an accumulator.
     *
     * @param acc      the function used to accumulate results.
     * @param identity the identity or staring value of the reduction.
     * @param <R>      the type of the result
     * @return the result of successively applying acc to all values in this
     */
    //without Monoids, this probably can't be expressed as foldMap/ or at lest not in a useful way
    <R> R foldr(BiFunction<T, R, R> acc, R identity);

    /**
     * Same as {@link #foldr(BiFunction, Object)} but we don't need to provide an identity, if our folding function
     * returns instances of {@link Zero}. {@link Zero#zero(Class)} is called with the class implementing MonadZero.
     *
     * @param acc  the function used to accumulate results.
     * @param <MZ> the type of the MonadZero implementation we are mapping towards
     * @param <R>  the type of the result
     * @return the result of successively applying acc to all values in this
     */
    default <MZ extends Zero<MZ, ?>, R> Zero<MZ, R> foldr(TBF<T, ? extends Zero<MZ, R>, R, MZ> acc) {
        return foldr((BiFunction<T, Zero<MZ, R>, Zero<MZ, R>>) acc.getFunction(), (Zero<MZ, R>) zero(acc.getType()));
    }

    //with Monoid instead of MonadPlusZero/Alternative, foldMap would be more powerful
    //monoids need to be implemented with classes providing a mempty and mconcat operation
    //maybe use instances instead of generic classes

    /**
     * {@link #foldr} implementation based on {@link Alternative#alternative(Supplier)}
     * foldMap implementation based on {@link Alternative}.
     */
    default <A extends Alternative<A, R>, R> Alternative<A, R> foldMapA(Function<T, Alternative<A, R>> f, Class<A> clazz) {
        return foldr((T t, Alternative<A, R> acc) -> acc.alternative(() -> f.apply(t)), zero(clazz));
    }

    /**
     * {@link #foldr} implementation based on {@link Alternative#alternative(Supplier)}.
     * If you provide a {@link TF} the class argument{@link #foldMapA(Function, Class)} can be omitted.
     * foldMap implementation based on {@link Alternative}.
     */
    default <A extends Alternative<A, ?>, R> Alternative<A, R> foldMapA(TF<T, R, A> f) {
        return foldr((T t, Alternative<A, R> acc) -> acc.alternative(() -> f.applyTyped(t)), (Alternative<A, R>) zero(f.getType()));
    }

    /**
     * {@link #foldr} implementation based on {@link MonadPlusZero#mplus(MonadPlus)}
     * foldMap implementation based on {@link MonadPlusZero}
     */
    default <M extends MonadPlusZero<M, R>, R> MonadPlusZero<M, R> foldMapM(Function<T, MonadPlusZero<M, R>> f, Class<M> clazz) {
        return foldr((T t, MonadPlusZero<M, R> acc) -> acc.alternative(() -> f.apply(t)), zero(clazz));
    }

    /**
     * {@link #foldr} implementation based on {@link MonadPlusZero#mplus(MonadPlus)}
     * If you provide a {@link TF} the class argument from {@link #foldMapM(Function, Class)} can be omitted.
     * foldMap implementation based on {@link MonadPlusZero}
     */
    default <M extends MonadPlusZero<M, ?>, R> MonadPlusZero<M, R> foldMapM(TF<T, R, M> f) {
        return foldr((T t, MonadPlusZero<M, R> acc) -> acc.mplus(f.applyTyped(t)), (MonadPlusZero<M, R>) zero(f.getType()));
    }


    /**
     * Checks if all Elements satisfy the predicate. NOT LAZY
     * @param predicate the predicate to check the values against
     * @return weather or not all Elements satisfy predicate
     */
    default boolean all(Predicate<T> predicate) {
        return foldr((val, acc) -> acc && predicate.test(val),true);
    }

    /**
     * Checks if any Element satisfies the predicate. NOT LAZY
     * @param predicate the predicate to check the values against
     * @return weather or not any Element satisfies predicate
     */
    default boolean any(Predicate<T> predicate) {
        return foldr((val, acc) -> acc || predicate.test(val),false);
    }
}
