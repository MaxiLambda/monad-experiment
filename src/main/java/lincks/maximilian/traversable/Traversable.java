package lincks.maximilian.traversable;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.foldable.Foldable;
import lincks.maximilian.functor.Functor;
import lincks.maximilian.util.BF;

/**
 * A traversable context over a type. The shape of the traversable is preserved.
 * All traversals are Instances of {@link Foldable} and {@link Functor}.
 *
 * @param <TR>
 * @param <T>
 */
public interface Traversable<TR extends Traversable<TR, ?>, T> extends Foldable<TR, T>, Functor<TR, T> {
    //this can't be used to define traverse, because it only works on Traversals over Applicatives
    static <TR extends Traversable<TR, ?>, A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> sequenceA(Traversable<TR, ? extends Applicative<A, R>> traversable, Class<? super A> clazz) {
        Traversable<TR, Applicative<A, R>> traversable2 = (Traversable<TR, Applicative<A, R>>) traversable;
        return traversable2.traverse(BF.<Applicative<A, R>, R, A>of(i -> i, clazz));
    }

    <A extends Applicative<A, ?>, R> Applicative<A, ? extends Traversable<TR, R>> traverse(BF<T, R, A> f);
}
