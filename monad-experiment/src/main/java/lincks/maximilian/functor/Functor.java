package lincks.maximilian.functor;

import lincks.maximilian.util.Top;

import java.util.function.Function;

/**
 * Describes a context over a Type. The wrapped Type can be changed by mapping over it.
 */
public interface Functor<F extends Functor<F, ?>, T> extends Top<F, T> {
    /**
     * Maps the given Functor to another functor by applying f.
     *
     * @param f   the function used to map over this functor.
     * @param <R> the type the new Functor wraps over.
     * @return new Functor over R
     */
    <R> Functor<F, R> map(Function<T, R> f);
}
