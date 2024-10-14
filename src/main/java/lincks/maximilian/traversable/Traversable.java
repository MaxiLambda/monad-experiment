package lincks.maximilian.traversable;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.foldable.Foldable;
import lincks.maximilian.functor.Functor;
import lincks.maximilian.util.func.ApplicativeFunction;

import java.util.function.Function;

public interface Traversable<TR extends Traversable<TR, ?>, T> extends Foldable<TR, T>, Functor<TR, T> {
    //this can't be used to define traverse, because it only works on Traversals over Applicatives
    static <TR extends Traversable<TR, ?>, A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> sequenceA(Traversable<TR, ? extends Applicative<A, R>> traversable, Class<? super A> clazz) {
        Traversable<TR, Applicative<A, R>> traversable2 = (Traversable<TR, Applicative<A, R>>) traversable;
        return traversable2.traverse(ApplicativeFunction.of(Function.identity(), clazz));
    }

    <A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> traverse(ApplicativeFunction<T, R, A> f);
}
