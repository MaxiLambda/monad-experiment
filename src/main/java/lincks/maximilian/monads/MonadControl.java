package lincks.maximilian.monads;

public class MonadControl {

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
    public static <Outer extends Monad<M, Inner>, Inner extends Monad<M, T>, M extends Monad<M, ?>, T> Inner join(Outer outer) {
        return (Inner) outer.bind(m -> m);
    }
}
