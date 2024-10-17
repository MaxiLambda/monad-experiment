package lincks.maximilian.applicative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a Type implementing {@link Applicative} with this method, if another class
 * implements the {@link ApplicativeConstructor} method.
 */
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface ApplicativeConstructorDelegate {
    Class<?> clazz();
}
