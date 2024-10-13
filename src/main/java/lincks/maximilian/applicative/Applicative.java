package lincks.maximilian.applicative;

import lincks.maximilian.functor.Functor;

import java.util.function.BiFunction;
import java.util.function.Function;

import static lincks.maximilian.util.func.F.convert;

public interface Applicative<A extends Applicative<A,?>,T> extends Functor<A,T> {

    //overwrite the types from Functor
    @Override
    <R> Applicative<A, R> map(Function<T, R> f);


    default <R> Applicative<A,R> sequence(Applicative<A, Function<T,R>> f) {
        BiFunction<Function<T,R>,T,R> x = convert(Function.identity());
        return f.liftA2(x, this);
    }


    default <T2,R> Applicative<A,R> liftA2(BiFunction<T, T2,R> f, Applicative<A, T2> other) {
       return other.sequence(map(convert(f)));
    }
}
