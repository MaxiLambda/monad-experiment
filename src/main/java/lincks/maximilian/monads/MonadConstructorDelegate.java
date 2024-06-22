package lincks.maximilian.monads;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a Type implementing {@link <S,T> Monad<S,T>} with this method, if another class
 * implements the {@link MonadConstructor} method.
 */
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface MonadConstructorDelegate {
    Class<?> clazz();
}
