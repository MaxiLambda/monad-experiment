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
            return clazz.getConstructor(Object.class).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <M extends Monad<M, T>, T> Function<T,M> pure(Class<M> clazz) {
        return (T value) -> {
            try {
                return (M) getConstructor(clazz).get().newInstance(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static <M extends Monad<M,T>, T> Optional<Constructor<?>> getConstructor(Class<M> clazz){
        return Arrays.stream(clazz.getConstructors()).filter(c -> c.getDeclaredAnnotation(MonadConstructor.class) != null).findFirst();
    }
}
