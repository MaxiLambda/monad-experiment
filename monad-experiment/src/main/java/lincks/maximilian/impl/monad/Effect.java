package lincks.maximilian.impl.monad;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.monadzero.MZero;
import lincks.maximilian.monadzero.MonadZero;
import lincks.maximilian.util.Top;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static lincks.maximilian.monads.MonadPure.pure;

/**
 * Describes a lazy Effect which might fail.
 * The Effect is evaluated if the result is requested by {@link #get()} or when the effect is forced with {@link #evaluate()}.
 * TODO Lazy, even on alternative, maybe implement non-lazy version (on alternative)
 */
public interface Effect<T> extends MonadZero<Effect<?>, T>, Alternative<Effect<?>, T> {

    Effect<?> error = new SupplierEffect<>(() -> null);

    /**
     * Creates an empty failed effect over any Type.
     *
     * @param <T> the type of the effect.
     * @return a error/failed effect.
     */
    @MZero
    static <T> Effect<T> error() {
        return (Effect<T>) error;
    }

    /**
     * Unwrap an Effect if the typing is off.
     *
     * @param effect the effect to cast.
     * @param <T>    the type of the effect.
     * @return the cast effect.
     */
    static <T> Effect<T> unwrap(Top<Effect<?>, T> effect) {
        return (Effect<T>) effect;
    }

    /**
     * Create a new Effect from a runnable. This Effect fails if the runnable throws.
     *
     * @param runnable the function to run as an Effect.
     * @return the Effect over the Runnable.
     */
    static Effect<Void> fromRunnable(Runnable runnable) {
        return new SupplierEffect<>(runnable);
    }

    /**
     * Create a new Effect form a Supplier. This Effect fails if the Supplier throws.
     *
     * @param supplier teh function to run as an Effect.
     * @param <T>      the type of the value returned by this Effect.
     * @return a new Effect, which executes the Supplier and returns it's value.
     */
    static <T> Effect<T> fromSupplier(Supplier<T> supplier) {
        return new SupplierEffect<>(supplier);
    }

    /**
     * Factory Method for Effects, based on a single value. Nothing happens if this Effect is run. Just the given value is returned.
     * This Effect can never fail.
     *
     * @param value the value to wrap as an Effect.
     * @param <T>   the type of the Effect.
     * @return a new Effect which returns value and never fails.
     */
    static <T> Effect<T> of(T value) {
        return new SupplierEffect<>(value);
    }

    /**
     * Checks if the current effect has failed. If false is returned the effect might still fail when it's execution is requested.
     * Evaluating an Effect with {@link #evaluate()} runs the Effect (with possible side effects) and returns a new Effect.
     * Calling {@link #isError()} on that effect provides certainty if the effect can fail.
     *
     * @return weather or not this has failed yet.
     */
    default boolean isError() {
        //compare references
        //error.equals(this) is not used to Comparison by value gives the correct results,
        //even if some optimizations would use the same lambda "() -> null" multiple times.
        return this == error;
    }

    @Override
    default <R> Effect<R> map(Function<T, R> f) {
        //this is not reimplemented with the unwrap pattern because the compiler won't accept it...
        return this.bind(f.andThen(SupplierEffect::new));
    }

    @Override
    default <R> Effect<R> then(Supplier<Monad<Effect<?>, R>> f) {
        //this is not reimplemented with the unwrap pattern because the compiler won't accept it...
        return bind((T ignore) -> f.get());
    }

    @Override
    default Effect<T> filter(Predicate<T> p) {
        return unwrap(MonadZero.super.filter(p));
    }

    @Override
    <R> Effect<R> bind(Function<T, Monad<Effect<?>, R>> f);

    /**
     * Returns a new Effect with the effect of this Effect or another effect.
     * If {@link #isError()} is true, other is returned. Otherwise, the new Effect will try to run this effect and only
     * on failure run other.
     *
     * @param other the alternative Effect to run
     * @return a new Effect which tries to run this Effects effect and runs others Effect on failure.
     */
    @Override
    default Effect<T> alternative(Supplier<? extends Alternative<Effect<?>, T>> other) {
        if (isError()) {
            return unwrap(other.get());
        } else {
            return new DelegatingEffect<>(() -> {
                Effect<T> effect = evaluate();
                return effect.isError() ? unwrap(other.get()) : this;
            });
        }
    }

    /**
     * Returns the result of running this Effect.
     * Only works if {@link Effect#isError()} is false.
     * If this is an error, a RuntimeException is thrown.
     * <p>
     * FORCES THE EVALUATION IF THIS EFFECT. SIDE-EFFECTS happen.
     *
     * @return the value of this effect.
     */
    T get();

    default T getOnError(T value){
        Effect<T> evaluated = then(this::evaluate);
        while (evaluated instanceof DelegatingEffect<T> s) {
            evaluated = s.evaluate();
        }
        //evaluation is necessary to prevent exceptions
        return evaluated.isError() ? value : evaluated.get();
    }


    /**
     * Forces the evaluation of this and returns a new Effect.
     * The new Effect is an error if an Exception was thrown, otherwise the new Effect just returns the result.
     * <p>
     * FORCES THE EVALUATION IF THIS EFFECT. SIDE-EFFECTS happen.
     *
     * @return a new Effect, either an error or an Effect which only returns the result of this Effect.
     */
    Effect<T> evaluate();

    /**
     * Effect implementation based on Suppliers.
     */
    @EqualsAndHashCode
    @ToString
    class SupplierEffect<T> implements Effect<T> {

        private final Supplier<T> supplier;

        @ApplicativeConstructor
        public SupplierEffect(T supplier) {
            this.supplier = () -> supplier;
        }

        /**
         * Constructor, hidden to have no ambiguity with the @ApplicativeConstructor
         *
         * @param supplier the supplier to use.
         */
        private SupplierEffect(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /**
         * Used to create void Effects.
         *
         * @param runnable the Effect to run.
         */
        private SupplierEffect(Runnable runnable) {
            this.supplier = () -> {
                runnable.run();
                return null;
            };
        }

        @Override
        public T get() {
            if (!isError()) return supplier.get();
            throw new RuntimeException("Error supplier, dont use get here");
        }


        //tries to do the effect of this
        //on failure: return error effect
        //on success, return new Effect with result of computation
        @Override
        public SupplierEffect<T> evaluate() {
            if (isError()) return this;
            try {
                T value = supplier.get();
                return new SupplierEffect<>(value);
            } catch (Throwable t) {
                //this cast is fine, because error is implemented as an SupplierEffect
                return (SupplierEffect<T>) error();
            }
        }

        @Override
        public <R> Effect<R> bind(Function<T, Monad<Effect<?>, R>> f) {
            return isError() ? error() : new DelegatingEffect<>(() -> {
                SupplierEffect<T> effect = evaluate();
                return effect.isError() ? Effect.error() : unwrap(f.apply(effect.get()));
            });
        }
    }

    /**
     * Effect implementation based on delegating work to other effects. Used to achieve laziness.
     * Necessary for Laziness when calling {@link Effect#bind(Function)} or similar functions.
     */
    class DelegatingEffect<T> implements Effect<T> {

        private final Supplier<Effect<T>> effectSupplier;

        public DelegatingEffect(Supplier<Effect<T>> effect) {
            this.effectSupplier = effect;
        }

        @Override
        public T get() {
            return effectSupplier.get().get();
        }

        @Override
        public <R> Effect<R> bind(Function<T, Monad<Effect<?>, R>> f) {
            return new DelegatingEffect<>(() -> {
                Effect<T> effect = effectSupplier.get();
                //bind can be called here because eventually a SupplierEffect.bind must be called
                return unwrap(effect.bind(f));
            });
        }

        @Override
        public Effect<T> evaluate() {
            return effectSupplier.get().evaluate();
        }
    }
}