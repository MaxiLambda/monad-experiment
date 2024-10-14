package lincks.maximilian.monadzero;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public interface MonadZero<M extends MonadZero<M, ?>, T> {

    static <M extends MonadZero<M, T>, T> M zero(Class<M> clazz) {

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
}
