package lincks.maximilian.monads;

import java.util.function.Function;

import static lincks.maximilian.monads.MonadPure.pure;


/**
 * Java implementation of a Monad. Every Type implementing {@link Monad} MUST define
 * a one arg Constructor annotated with {@link MonadConstructor}.
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
 *
 * @param <M> the monadic Type.
 * @param <T> the Type wrapped by the monad.
 */
public interface Monad<M extends Monad<M, ?>, T> {

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
}

