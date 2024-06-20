package lincks.maximilian.monads;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public final class MonadPure {

    private MonadPure(){}

    public static <M extends Monad<M,T>, T> M pure(T value, Class<M> clazz) {
        try {
            return (M) getConstructor(clazz).get().newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <M extends Monad<M, T>, T> Function<T,M> pure(Class<M> clazz) {
        return (T value) -> {
            try {
                return (M) getConstructor(clazz).get().newInstance(value);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static <M extends Monad<M,T>, T> Optional<Constructor<?>> getConstructor(Class<M> clazz){
        Optional<Class<?>> definingClazz = Optional
                .ofNullable(clazz.getDeclaredAnnotation(MonadConstructorDelegate.class))
                .map(MonadConstructorDelegate::clazz);
        return Arrays.stream(definingClazz.orElse(clazz).getConstructors())
                .filter(c -> c.getDeclaredAnnotation(MonadConstructor.class) != null)
                .findFirst();
    }

}
