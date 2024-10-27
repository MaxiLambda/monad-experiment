package lincks.maximilian.applicative;

import lincks.maximilian.functor.Functor;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.util.func.F;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static lincks.maximilian.applicative.ApplicativePure.pure;
import static lincks.maximilian.util.func.F.reverse;

/**
 * An Extension of Functor with the ability to use {@link ApplicativePure#pure} to create new Instances.
 * Each implementation of Applicative must overwrite either {@link Applicative#sequence(Applicative)}
 * or{@link Applicative#liftA2(BiFunction, Applicative)}.
 * Instances need to annotate a constructor with @{@link ApplicativeConstructor} or the type declaration must be annotated
 * with @{@link ApplicativeConstructorDelegate}(Class) where Class needs a constructor with @{@link ApplicativeConstructor}.
 * The {@link ApplicativeConstructor} must accept exactly one generic Argument and produce an Instance of Applicative wrapped over the type of the argument.
 *
 * @param <A> the type of the Applicative
 * @param <T> the type the Applicative is wrapped around
 */
public interface Applicative<A extends Applicative<A, ?>, T> extends Functor<A, T> {


    /**
     * Combine an Applicative f, wrapped over a function type expecting this Applicatives type, into a new Applicative of
     * the return type of f.
     * can be defined by liftA2.
     *
     * @param f   Applicative wrapped over a function.
     * @param <R> the return type of f, and the type the resulting Applicative wraps over.
     * @return the result of applying the context of this applicative to the function in the Applicative f.
     */
    default <R> Applicative<A, R> sequence(Applicative<A, Function<T, R>> f) {
        BiFunction<Function<T, R>, T, R> x = F.uncurry(Function.identity());
        return f.liftA2(x, this);
    }

    /**
     * Applies a BiFunction to this and another applicative of the same Applicative Type to create a new Applicative of
     * that type, wrapped over the result of the function.
     * can be defined by sequence.
     *
     * @param f     the function to apply to this and other
     * @param other other applicative to feed to f
     * @param <T2>  the type the other Applicative is wrapped around and the type of the second argument of f
     * @param <R>   the result type of f and the type the resulting Applicative is wrapped around.
     * @return a new Applicative wrapped over the result a combining this with other by using f.
     */
    default <T2, R> Applicative<A, R> liftA2(BiFunction<T, T2, R> f, Applicative<A, T2> other) {
        return other.sequence((Applicative<A, Function<T2, R>>) map(F.curry(f)));
    }

    /**
     * Version of {@link #liftA2} that uses and returns and expects Suppliers to enable delayed/lazy evaluation.
     *
     * @param f     supplier of the function to apply to this and other
     * @param other supplier of other applicative to feed to the evaluated f
     * @param <T2>  the type the other Applicative is wrapped around and the type of the second argument of f
     * @param <R>   the result type of f and the type the resulting Applicative is wrapped around.
     * @return supplier of a new Applicative wrapped over the result a combining this with other by using f.
     */
    default <T2, R> Supplier<? extends Applicative<A, R>> liftA2Lazy(Supplier<BiFunction<T, T2, R>> f, Supplier<? extends Applicative<A, T2>> other) {
        return () -> other.get().sequence((Applicative<A, Function<T2, R>>) map(F.curry(f.get())));
    }

    /**
     * Applies a function to this Applicative.
     *
     * @param f   the function used to map thy type of this.
     * @param <R> the type the new Applicative is wrapped over
     * @return new Applicative wrapped over R
     */
    default <R> Applicative<A, R> liftA(Function<T, R> f) {
        return sequence(pure(f, this.getClass()));
    }

    /**
     * Create a new Applicative wrapped over a MList of num times applicative.
     * This function is eager and recursive. If num == 0, returns the applicative over an empty MList.
     *
     * @param num         the amount of instances of applicative in the result
     * @param applicative the applicative to replicate
     * @param <A>         the type of the applicative.
     * @param <T>         the type the given applicative is wrapped over
     * @return an Applicative of type A wrapped over an MList containing the value of applicative num times.
     */
    static <A extends Applicative<A, ?>, T> Applicative<A, MList<T>> replicateA(int num, Applicative<A, T> applicative) {
        BiFunction<MList<T>, T, MList<T>> f = MList::prepend;
        return num == 0
                ? pure(new MList<>(), applicative.getClass())
                : applicative.liftA2(reverse(f), replicateA(num - 1, applicative));
    }
}
