package lincks.maximilian.monads;

public class MonadControl {
    public static  <MM extends Monad<MonType, M>,M extends Monad<MonType, T>,MonType extends Monad<MonType, ?>, T> M join(MM outer){
        return (M) outer.bind(m -> m);
    }

}
