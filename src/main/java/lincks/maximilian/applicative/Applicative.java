package lincks.maximilian.applicative;

import lincks.maximilian.functor.Functor;
import lincks.maximilian.util.func.F;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lincks.maximilian.applicative.ApplicativePure.pure;

public interface Applicative<A extends Applicative<A,?>,T> extends Functor<A,T> {



    default <R> Applicative<A,R> sequence(Applicative<A, Function<T,R>> f) {
        BiFunction<Function<T,R>,T,R> x = F.uncurry(Function.identity());
        return f.liftA2(x, this);
    }

    default <T2,R> Applicative<A,R> liftA2(BiFunction<T, T2,R> f, Applicative<A, T2> other) {
       return other.sequence((Applicative<A, Function<T2,R>>) map(F.curry(f)));
    }

    default <R> Applicative<A,R> liftA(Function<T,R> f) {
        return sequence(pure(f,this.getClass()));
    }
}
