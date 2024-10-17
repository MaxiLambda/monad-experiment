package lincks.maximilian.impl;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.monadzero.MZero;
import lincks.maximilian.monadzero.MonadZero;
import lincks.maximilian.util.Bottom;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Container used to indicate missing values or failed operations.
 */
@ToString
@EqualsAndHashCode
public class Maybe<T> implements MonadPlus<Maybe<?>, T>, MonadZero<Maybe<?>, T> {

    private static final Maybe<Void> nothing = new Maybe<>(null);
    private final T value;

    @ApplicativeConstructor
    public Maybe(T value) {
        this.value = value;
    }

    /**
     * Constructor used to create empty instances of Maybe with {@link MonadZero#zero(Class)}.
     *
     * @return the {@link #nothing()} value of 'Maybe'.
     */
    @MZero
    public static <R> Maybe<R> nothing() {
        return (Maybe<R>) nothing;
    }

    /**
     * Cast a Maybe if it is wrapped in other Types.
     */
    public static <T> Maybe<T> unwrap(Bottom<Maybe<?>, T> m) {
        return (Maybe<T>) m;
    }

    /**
     * Checks if this is equal to the nothing value {@link #nothing}
     *
     * @return weather or not this is nothing.
     */
    public boolean isNothing() {
        //compare references
        return nothing == this;
    }

    /**
     * Returns the value of this. Always returns null when {@link #isNothing()} is true.
     *
     * @return the value of this or null if this is nothing.
     */
    public T get() {
        //defining a throwing version could be helpful
        return value;
    }

    @Override
    public <R> Maybe<R> bind(Function<T, Monad<Maybe<?>, R>> f) {
        return isNothing() ? nothing() : f.andThen(Maybe::unwrap).apply(value);
    }

    @Override
    public <R> Maybe<R> map(Function<T, R> f) {
        return unwrap(MonadPlus.super.map(f));
    }

    @Override
    public <R> Maybe<R> then(Supplier<Monad<Maybe<?>, R>> f) {
        return unwrap(MonadPlus.super.then(f));
    }

    @Override
    public Maybe<T> mplus(MonadPlus<Maybe<?>, T> other) {
        return isNothing() ? nothing() : unwrap(other);
    }

    @Override
    public <R> Maybe<R> sequence(Applicative<Maybe<?>, Function<T, R>> f) {
        return isNothing() ? nothing() : unwrap(f.map(func -> func.apply(value)));
    }
}
