package lincks.maximilian.util;

import lombok.experimental.Delegate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;


/**
 * This appends the definition of a function with the ability to retrieve the class of the bottom which is created.
 * You need to create a new Instance <code>new BottomFunction(f){}</code> to capture type arguments.
 * This is hacky as fuck and will break sooner or later...
 * <p>
 * You can NOT use this with placeholder types. This only works when a concrete function is passed.
 * <p>
 * Therefore, prefer {@link BF#of(Function, Class)} over {@link BF#BF(Function)}.
 *
 * @param <T> the type the argument Bottom is wrapped over
 * @param <R> the type the resulting Bottom
 * @param <B> the type of the Bottom
 */

public abstract class BF<T, R, B extends Bottom<B, ?>> implements Function<T, Bottom<B, R>> {

    @Delegate
    private final Function<T, Bottom<B, R>> function;

    private final Class<?> bottomType;

    private BF(Function<T, Bottom<B, R>> function, Class<?> clazz) {
        this.function = function;
        this.bottomType = clazz;
    }

    public BF(Function<T, Bottom<B, R>> function) {
        this.function = function;
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType paramClass) {
            Type BottomTypeWrapper = paramClass.getActualTypeArguments()[2];

            if (BottomTypeWrapper instanceof ParameterizedType paramWrapper) {
                Type actualType = paramWrapper.getRawType();
                if (actualType instanceof Class<?> clazz) {
                    this.bottomType = clazz;
                    return;
                }
            }
        }
        throw new RuntimeException("An Error occurred while resolving the correct Bottom Type class.");
    }

    /**
     * Static constructor to create an instance when the required class is already known.
     *
     * @return new Instance of this.
     */
    public static <T, R, B extends Bottom<B, ?>> BF<T, R, B> of(Function<T, Bottom<B, R>> function, Class<?> clazz) {
        return new BF<>(function, clazz) {
        };
    }

    /**
     * @return the Class extending the {@link Bottom} type, which is used as the fourth argument to this class.
     */
    public Class<B> getType() {
        return (Class<B>) bottomType;
    }

    /**
     * Apply the given values to the function this class wraps.
     *
     * @param value
     * @return the result.
     */
    public <BR extends Bottom<B, R>> BR applyTyped(T value) {
        return (BR) function.apply(value);
    }
}
