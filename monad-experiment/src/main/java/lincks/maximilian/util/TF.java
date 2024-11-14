package lincks.maximilian.util;

import lombok.experimental.Delegate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;


/**
 * This appends the definition of a function with the ability to retrieve the class of the Top which is created.
 * You need to create a new Instance <code>new TopFunction(f){}</code> to capture type arguments.
 * This is hacky as fuck and will break sooner or later...
 * <p>
 * You can NOT use this with placeholder types. This only works when a concrete function is passed.
 * <p>
 * Therefore, prefer {@link TF#of(Function, Class)} over {@link TF#TF(Function)}.
 *
 * @param <T> the type the argument Top is wrapped over
 * @param <R> the type the resulting Top
 * @param <B> the type of the Top
 */

public abstract class TF<T, R, B extends Top<B, ?>> implements Function<T, Top<B, R>> {

    @Delegate
    private final Function<T, Top<B, R>> function;

    private final Class<?> TopType;

    private TF(Function<T, Top<B, R>> function, Class<?> clazz) {
        this.function = function;
        this.TopType = clazz;
    }

    public TF(Function<T, Top<B, R>> function) {
        this.function = function;
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType paramClass) {
            Type TopTypeWrapper = paramClass.getActualTypeArguments()[2];

            if (TopTypeWrapper instanceof ParameterizedType paramWrapper) {
                Type actualType = paramWrapper.getRawType();
                if (actualType instanceof Class<?> clazz) {
                    this.TopType = clazz;
                    return;
                }
            }
        }
        throw new RuntimeException("An Error occurred while resolving the correct Top Type class.");
    }

    /**
     * Static constructor to create an instance when the required class is already known.
     *
     * @return new Instance of this.
     */
    public static <T, R, B extends Top<B, ?>> TF<T, R, B> of(Function<T, Top<B, R>> function, Class<?> clazz) {
        return new TF<>(function, clazz) {
        };
    }

    /**
     * @return the Class extending the {@link Top} type, which is used as the fourth argument to this class.
     */
    public Class<B> getType() {
        return (Class<B>) TopType;
    }

    /**
     * Apply the given values to the function this class wraps.
     *
     * @param value
     * @return the result.
     */
    public <BR extends Top<B, R>> BR applyTyped(T value) {
        return (BR) function.apply(value);
    }
}
