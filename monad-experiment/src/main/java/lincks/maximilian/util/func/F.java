package lincks.maximilian.util.func;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Static class with some utility to manipulate functions.
 */
public final class F {

    private F() {
    }

    public static <A, B, C> Function<A, Function<B, C>> curry(BiFunction<A, B, C> f) {
        return a -> b -> f.apply(a, b);
    }

    public static <A, B> Function<A, Supplier<B>> curry(Function<A, B> f) {
        return a -> () -> f.apply(a);
    }

    public static <A extends Function<B, C>, B, C> BiFunction<A, B, C> uncurry(Function<A, Function<B, C>> f) {
        return ((A a, B b) -> f.apply(a).apply(b));
    }

    public static <A, B, C> BiFunction<B, A, C> reverse(BiFunction<A, B, C> f) {
        return (B b, A a) -> f.apply(a, b);
    }

    public static <T,R> Function<T,R> constant(R r) {
        return ignore -> r;
    }

    public static <I,R> Function<I,R> constant(Supplier<R> r) {
        return ignore -> r.get();
    }

    public static <I,T,R> BiFunction<I,T,R> constant(Function<T,R> r) {
        return (ignore, t) -> r.apply(t);
    }

    public static <A,B,C> BiFunction<B,A,C> flip(BiFunction<A, B, C> f) {
        return (b,a) -> f.apply(a, b);
    }
}
