package lincks.maximilian.util.func;

import lincks.maximilian.applicative.Applicative;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * This appends the definition of a function with the ability to retrieve the class of the applicative which is created.
 * You need to create a new Instance `new ApplicativeFunction(f){}` to capture type arguments.
 * This is hacky as fuck and will break sooner or later...
 *
 * @param <T> The type of the value to map
 * @param <R> The new type wrapped by the applicative type A
 * @param <A> The Applicative type to return (wrapped over R)
 */
public abstract class ApplicativeFunction<T, R, A extends Applicative<A, ?>> implements Function<T, Applicative<A, R>> {

    @Delegate
    private final Function<T, Applicative<A, R>> function;

    @Getter
    private final Class<?> applicativeType;

    public static <T, R, A extends Applicative<A, ?>> ApplicativeFunction<T,R,A> of(Function<T, Applicative<A, R>> function, Class<?> clazz) {
        return new ApplicativeFunction<>(function, clazz){};
    }

    private ApplicativeFunction(Function<T, Applicative<A, R>> function, Class<?> clazz) {
        this.function = function;
        this.applicativeType = clazz;
    }

    public ApplicativeFunction(Function<T, Applicative<A, R>> function) {
        this.function = function;
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType paramClass) {
            Type applicativeTypeWrapper = paramClass.getActualTypeArguments()[2];

            if (applicativeTypeWrapper instanceof ParameterizedType paramWrapper) {
                Type actualType = paramWrapper.getRawType();
                if (actualType instanceof Class<?> clazz) {
                    this.applicativeType = clazz;
                    return;
                }
            }
        }
        throw new RuntimeException("An Error occurred while resolving the correct applicative Type class.");
    }
}
