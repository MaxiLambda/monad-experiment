package lincks.maximilian.applicative;

import lincks.maximilian.monads.MonadPure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * Helper Class for {@link Applicative} to complete monadic/applicative definition.
 */
public final class ApplicativePure {

    private ApplicativePure() {
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Lifts a value to a Monadic/Applicative value of a given class.
     * The difference to {@link #pure} is that there is no constraint on the passed class.
     *
     * @param value the value to lift.
     * @param clazz the monad/applicative to lift to.
     * @param <A>   the type of the monad/applicative.
     * @param <T>   the type of the value to lift.
     * @return a new Monad/Applicative A wrapping the value of type T.
     */
    public static <A extends Applicative<A, T>, T> A pureUnsafeClass(T value, Class<?> clazz) {
        try {
            return (A) getConstructorUnsafeClass(clazz).get().newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Wrapper around {@link MonadPure#pure(Object, Class)} to use in function composition
     * The difference to {@link #pure} is that there is no constraint on the passed class.
     *
     * @param clazz the monad to lift to.
     * @param <A>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a {@code Function<T, M>} lifting T to a Monad M wrapping T.
     */
    public static <A extends Applicative<A, T>, T> Function<T, A> pureUnsafeClass(Class<?> clazz) {
        return (T value) -> pureUnsafeClass(value, clazz);
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Lifts a value to a Monadic/Applicative value of a given class.
     *
     * @param value the value to lift.
     * @param clazz the monad/applicative to lift to.
     * @param <A>   the type of the monad/applicative.
     * @param <T>   the type of the value to lift.
     * @return a new Monad/Applicative A wrapping the value of type T.
     */
    public static <A extends Applicative<A, T>, T> A pure(T value, Class<A> clazz) {
        try {
            return (A) getConstructor(clazz).get().newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ONLY WORKS FOR CLASSES HAVING A CONSTRUCTOR WITH {@link ApplicativeConstructor}.
     * Wrapper around {@link MonadPure#pure(Object, Class)} to use in function composition
     *
     * @param clazz the monad to lift to.
     * @param <A>   the type of the monad.
     * @param <T>   the type of the value to lift.
     * @return a {@code Function<T, M>} lifting T to a Monad M wrapping T.
     */
    public static <A extends Applicative<A, T>, T> Function<T, A> pure(Class<A> clazz) {
        return (T value) -> pure(value, clazz);
    }

    private static <A extends Applicative<A, T>, T> Optional<Constructor<?>> getConstructor(Class<A> clazz) {
        Optional<Class<?>> definingClazz = Optional
                .ofNullable(clazz.getDeclaredAnnotation(ApplicativeConstructorDelegate.class))
                .map(ApplicativeConstructorDelegate::clazz);
        return Arrays.stream(definingClazz.orElse(clazz).getConstructors())
                .filter(c -> c.getDeclaredAnnotation(ApplicativeConstructor.class) != null)
                .findFirst();
    }

    private static <A extends Applicative<A, T>, T> Optional<Constructor<?>> getConstructorUnsafeClass(Class<?> clazz) {
        Optional<Class<?>> definingClazz = Optional
                .ofNullable(clazz.getDeclaredAnnotation(ApplicativeConstructorDelegate.class))
                .map(ApplicativeConstructorDelegate::clazz);
        return Arrays.stream(definingClazz.orElse(clazz).getConstructors())
                .filter(c -> c.getDeclaredAnnotation(ApplicativeConstructor.class) != null)
                .findFirst();
    }

}
