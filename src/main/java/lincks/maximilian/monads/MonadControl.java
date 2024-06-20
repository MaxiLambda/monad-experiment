package lincks.maximilian.monads;

public class MonadControl {
    public static <Outer extends Monad<M, Inner>, Inner extends Monad<M, T>, M extends Monad<M, ?>, T> Inner join(Outer outer) {
        return (Inner) outer.bind(m -> m);
    }
}
