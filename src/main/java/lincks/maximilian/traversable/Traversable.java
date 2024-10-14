package lincks.maximilian.traversable;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.foldable.Foldable;
import lincks.maximilian.functor.Functor;
import lincks.maximilian.util.func.ApplicativeFunction;

import java.util.function.Function;

public interface Traversable<TR extends Traversable<TR, ?>, T> extends Foldable<TR, T>, Functor<TR, T> {
    <A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> traverse(ApplicativeFunction<T, R, A > f);

    //this can't be used to define traverse, because it only works on Traversals over Applicatives
    static <TR extends Traversable<TR, ?>, A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> sequenceA(Traversable<TR, Applicative<A, R>> traversable) {
        return traversable.traverse(new ApplicativeFunction<>(Function.identity()){});
    }

}
