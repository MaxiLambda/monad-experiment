package lincks.maximilian.applicative;

import lincks.maximilian.monads.MonadPure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates a one element constructor in a Type implementing {@link Applicative} to use for creating new instances of that Applicative
 * using {@link MonadPure#pure(Class)} and {@link MonadPure#pure(Object, Class)}.
 */
@Retention(RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface ApplicativeConstructor {
}
