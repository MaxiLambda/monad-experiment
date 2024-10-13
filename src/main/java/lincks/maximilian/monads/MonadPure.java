package lincks.maximilian.monads;

import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativeConstructorDelegate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
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
     *
     * @param value the value to lift.
     * @param clazz the monad to lift to.
     * @param <M>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a new Monad M wrapping the value of type T.
     */
    public static <M extends Monad<M, T>, T> M pure(T value, Class<M> clazz) {
        try {
            return (M) getConstructor(clazz).get().newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
        return (T value) -> pure(value, clazz);
    }

    private static <M extends Monad<M, T>, T> Optional<Constructor<?>> getConstructor(Class<M> clazz) {
        Optional<Class<?>> definingClazz = Optional
                .ofNullable(clazz.getDeclaredAnnotation(ApplicativeConstructorDelegate.class))
                .map(ApplicativeConstructorDelegate::clazz);
        return Arrays.stream(definingClazz.orElse(clazz).getConstructors())
                .filter(c -> c.getDeclaredAnnotation(ApplicativeConstructor.class) != null)
                .findFirst();
    }

}
