package lincks.maximilian.impl;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativeConstructorDelegate;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.monadzero.MZero;
import lincks.maximilian.util.Bottom;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.Function;
import java.util.function.Supplier;

/** Lazy, even on alternative, maybe implement non-lazy version (on alternative)*/
public interface Effect<T> extends Monad<Effect<?>, T>, Alternative<Effect<?>, T> {

    Effect<?> error = new SupplierEffect<>(() -> null);

    @MZero
    static <T> Effect<T> error() {
        return (Effect<T>) error;
    }

    static <T> Effect<T> unwrap(Bottom<Effect<?>, T> effect) {
        return (Effect<T>) effect;
    }

    static Effect<Void> fromRunnable(Runnable runnable) {
        return new SupplierEffect<>(runnable);
    }

    static <T> Effect<T> fromSupplier(Supplier<T> supplier) {
        return new SupplierEffect<>(supplier);
    }

    static <T> Effect<T> of(T value) {
        return new SupplierEffect<>(value);
    }

    default boolean isError() {
        //compare references
        //error.equals(this) is not used to Comparison by value gives the correct results,
        //even if some optimizations would use the same lambda "() -> null" multiple times.
        return this == error;
    }

    @Override
    default Effect<T> alternative(Supplier<Alternative<Effect<?>, T>> other) {
        if(isError()) {
            return unwrap(other.get());
        } else {
            return new DelegatingEffect<>(() -> {
                Effect<T> effect = evaluate();
                return effect.isError() ? unwrap(other.get()) : this;
            });
        }
    }

    /**
     * Only works if {@link Effect#isError()} is false.
     * If this is an error, a RuntimeException is thrown.
     * @return the value of this effect.
     */
    T get();

    Effect<T> evaluate();

    @EqualsAndHashCode
    @ToString
    class SupplierEffect<T> implements Effect<T> {

        //    public final static Effect<Void> NO_OP = new Effect<>(()  -> null);


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
                return unwrap(f.apply(effect.get()));
            });
        }
    }

    class DelegatingEffect<T> implements Effect<T> {

        public DelegatingEffect(Supplier<Effect<T>> effect) {
            this.effectSupplier = effect;
        }

        private Supplier<Effect<T>> effectSupplier;

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