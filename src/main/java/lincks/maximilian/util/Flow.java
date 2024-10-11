package lincks.maximilian.util;

import java.util.Arrays;
import java.util.function.Function;

/**
 * This class has nothing to do with monads.
 * It helps you the execute multiple functions/lambdas in sequence without .andThen.
 * {@code Flow.flow(1, i -> i * 2, i -> i + "!", String::length,...)}
 * Works with up to 16 Functions.
 */
public class Flow {
    //clojure code to create flow(...) with as many arguments as one likes
    //;;create types
    //(defn types [count] (str "<" (clojure.string/join ", " (for [x (range 1 (+ count 1))] (str "T" x))) ">"))
    //;;create arguments
    //(defn args [count] (str  "T1 val, " (clojure.string/join ", " (for [x (range 1 (+ count 1))] (str "Function<T" x ", T" (+ x 1) "> f" x)))))
    //;;body
    //(defn body [count] (str "return f1" (apply str (for [x (range 2 (+ count 1))] (str ".andThen(f" x ")"))) ".apply(val);"))
    //;;put everything together
    //(defn create [count] (str "public " (types (+ count 1)) " T" (+ count 1) " flow(" (args count) ") { " (body count) " }"))


    //TODO maybe create version of this with Maybe/Either(or Failable) or a MonadStack (Transformer) to help with possible exceptions/null values

    /**
     * Function used to chain together as many function calls as one wants. But this is not typesafe. Use a {@link Flow#flow} function for type safety.
     *
     * @param val the starting value.
     * @param fs  all Functions to apply to val.
     * @param <R> the type of the result. Added to remove boilerplate casting everytime this function is used.
     * @return The result of sequentially applying all functions in fs to val.
     */
    public static <R> R flowUnsafe(Object val, Function... fs) {
        Function f = Arrays.stream(fs).reduce(Function.identity(), Function::andThen);
        return (R) f.apply(val);
    }

    public static <T1, T2, T3> T3 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2) {
        return f1.andThen(f2).apply(val);
    }

    public static <T1, T2, T3, T4> T4 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3) {
        return f1.andThen(f2).andThen(f3).apply(val);
    }

    public static <T1, T2, T3, T4, T5> T5 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4) {
        return f1.andThen(f2).andThen(f3).andThen(f4).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6> T6 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> T7 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> T8 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> T9 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> T10 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> T11 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> T12 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> T13 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11, Function<T12, T13> f12) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).andThen(f12).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> T14 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11, Function<T12, T13> f12, Function<T13, T14> f13) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).andThen(f12).andThen(f13).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> T15 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11, Function<T12, T13> f12, Function<T13, T14> f13, Function<T14, T15> f14) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).andThen(f12).andThen(f13).andThen(f14).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> T16 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11, Function<T12, T13> f12, Function<T13, T14> f13, Function<T14, T15> f14, Function<T15, T16> f15) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).andThen(f12).andThen(f13).andThen(f14).andThen(f15).apply(val);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> T17 flow(T1 val, Function<T1, T2> f1, Function<T2, T3> f2, Function<T3, T4> f3, Function<T4, T5> f4, Function<T5, T6> f5, Function<T6, T7> f6, Function<T7, T8> f7, Function<T8, T9> f8, Function<T9, T10> f9, Function<T10, T11> f10, Function<T11, T12> f11, Function<T12, T13> f12, Function<T13, T14> f13, Function<T14, T15> f14, Function<T15, T16> f15, Function<T16, T17> f16) {
        return f1.andThen(f2).andThen(f3).andThen(f4).andThen(f5).andThen(f6).andThen(f7).andThen(f8).andThen(f9).andThen(f10).andThen(f11).andThen(f12).andThen(f13).andThen(f14).andThen(f15).andThen(f16).apply(val);
    }
}