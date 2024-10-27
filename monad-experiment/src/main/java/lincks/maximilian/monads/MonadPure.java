package lincks.maximilian.monads;

import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativePure;

import java.util.function.Function;

/**
 * Helper Class for {@link Monad} to complete monadic definition.
 */
public final class MonadPure {

    private MonadPure() {
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Lifts a value to a Monadic value of a given class.
     * The difference to {@link #pure} is that there is no constraint on the passed class.
     *
     * @param value the value to lift.
     * @param clazz the monad to lift to.
     * @param <M>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a new Monad M wrapping the value of type T.
     */
    public static <M extends Monad<M, T>, T> M pureUnsafeClass(T value, Class<?> clazz) {
        return ApplicativePure.pureUnsafeClass(value, clazz);
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Wrapper around {@link MonadPure#pure(Object, Class)} to use in function composition.
     * The difference to {@link #pure} is that there is no constraint on the passed class*
     *
     * @param clazz the monad to lift to.
     * @param <M>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a {@code Function<T, M>} lifting T to a Monad M wrapping T.
     */
    public static <M extends Monad<M, T>, T> Function<T, M> pureUnsafeClass(Class<?> clazz) {
        return ApplicativePure.pureUnsafeClass(clazz);
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Lifts a value to a Monadic value of a given class.
     *
     * @param value the value to lift.
     * @param clazz the monad to lift to.
     * @param <M>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a new Monad M wrapping the value of type T.
     */
    public static <M extends Monad<M, T>, T> M pure(T value, Class<M> clazz) {
        return ApplicativePure.pure(value, clazz);
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Wrapper around {@link MonadPure#pure(Object, Class)} to use in function composition
     *
     * @param clazz the monad to lift to.
     * @param <M>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a {@code Function<T, M>} lifting T to a Monad M wrapping T.
     */
    public static <M extends Monad<M, T>, T> Function<T, M> pure(Class<M> clazz) {
        return ApplicativePure.pure(clazz);
    }
}
