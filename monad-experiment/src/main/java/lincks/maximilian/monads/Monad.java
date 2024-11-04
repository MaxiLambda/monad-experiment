package lincks.maximilian.monads;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.applicative.ApplicativePure;

import java.util.function.Function;
import java.util.function.Supplier;

import static lincks.maximilian.monads.MonadPure.pure;


/**
 * Java implementation of a Monad. Every Type implementing {@link Monad} MUST define
 * a one arg Constructor annotated with {@link ApplicativeConstructor}.
 * <p>
 * It is recommended that no Monad {@code MyMonad<T> implements Monad<MyMonad<?>,T>} implements
 * this interface with a Type M that is not itself.
 * <p>
 * It is also strongly recommended to implement a function:
 * <pre>{@code
 * public static <T> MyMonad<T> unwrap(Monad<MyMonad<?>, T> m) {
 *      return (MyMonad<T>) m;
 * }}</pre>
 * This helps because {@link Monad#bind(Function)} and {@link Monad#map(Function)} both return
 * instances of {@code Monad<MyMonad<?>,T>}.
 * <p>
 * Every Monad is a {@link Applicative}. Therefore, the {@link ApplicativePure#pure} works just as well as {@link MonadPure#pure(Class)}.
 *
 * @param <M> the monadic Type.
 * @param <T> the Type wrapped by the monad.
 */
public interface Monad<M extends Monad<M, ?>, T> extends Applicative<M, T> {

    /**
     * Joins a nested Monad structure of the same type.
     *
     * @param outer   the monad to join.
     * @param <Outer> the type definition of the outer monad.
     * @param <Inner> the type definition of the inner monad.
     * @param <M>     the monadic type.
     * @param <T>     the type parameter of the monad.
     * @return the joined monad.
     */
    static <Outer extends Monad<M, Inner>, Inner extends Monad<M, T>, M extends Monad<M, ?>, T> Inner join(Outer outer) {
        return (Inner) outer.bind(m -> m);
    }

    /**
     * Creates a new Monad from a value wrapped in another monad.
     *
     * @param f   function to construct a new Monad {@code M<R>} from a value wrapped inside another Monad {@code M<T>}.
     * @param <R> the type of the new Monad.
     * @return a new Monad wrapping a R value.
     */
    <R> Monad<M, R> bind(Function<T, Monad<M, R>> f);

    /**
     * @param f   mapping function.
     * @param <R> the type fo which is mapped.
     * @return a new Monad with type R
     */
    default <R> Monad<M, R> map(Function<T, R> f) {
        //this works by calling f on the wrapped value supplied by bind and then lifting the result of f using pure
        return this.bind(f.andThen(pure(this.getClass())));
    }

    /**
     * Returns the result f by calling bind while ignoring the current value
     * <p>
     * For example when called with something like an Optional, the empty state is propagated,
     * otherwise the result of f is the new value.
     *
     * @param f   function to supply next value
     * @param <R> the type of the new monad.
     * @return new monad with type t.
     */
    default <R> Monad<M, R> then(Supplier<Monad<M, R>> f) {
        return bind((T ignore) -> f.get());
    }

}

