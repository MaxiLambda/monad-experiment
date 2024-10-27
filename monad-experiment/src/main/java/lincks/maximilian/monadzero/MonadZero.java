package lincks.maximilian.monadzero;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.util.Bottom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import static lincks.maximilian.monads.MonadPure.pure;

/**
 * Describes a Class where Instances can be created for any type without a parameter.
 */
public interface MonadZero<M extends MonadZero<M, ?>, T> extends Bottom<M, T> {

    /**
     * Static method used to create a static no parameter Instance of a Class.
     * <p>
     * Used in contexts where the used Type of M is unknown.
     *
     * @param clazz the class to create the instance for.
     * @param <M>   the Type of the class.
     * @return a new Instance of type M.
     */
    static <M extends MonadZero<M, ?>> M zero(Class<M> clazz) {

        Optional<Method> creator = Arrays.stream(clazz.getMethods())
                .filter(method -> method.getDeclaredAnnotation(MZero.class) != null)
                .filter(method -> method.getParameterCount() == 0)
                .findFirst();

        return (M) creator.map(method -> {
            try {
                return method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() -> new RuntimeException("No static, 0 argument method in class '%s' is annotated with @MZero."));
    }

    static <M extends MonadZero<M, ?> & Monad<M,?>, T> Monad<M,T> filterM(Predicate<T> p, MonadZero<M,T> m) {
        Monad<M,T> monad = ((Monad<M,T>) m);
        return monad.bind(val -> p.test(val) ?  pure(val,monad.getClass()) : (Monad<M, T>) zero(m.getClass()));
    }
}
