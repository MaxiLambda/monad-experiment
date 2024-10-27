package lincks.maximilian.monadzero;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by {@link MonadZero} to find functions to create Instances with in {@link MonadZero#zero(Class)}.
 * ONLY use this on no parameter public static functions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MZero {

}
