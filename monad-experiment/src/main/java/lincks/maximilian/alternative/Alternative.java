package lincks.maximilian.alternative;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.monadzero.Zero;

import java.util.function.Supplier;

/**
 * Represents a choice/combination between two Applicatives of the same type.
 *
 * @param <A> type of the Applicative
 * @param <T> wrapped type
 */
public interface Alternative<A extends Alternative<A, ?>, T> extends Applicative<A, T>, Zero<A, T> {
    //zero from MonadZero is re-used as "empty"

    /**
     * Combine two alternatives. The natural way is as a choice, where other is used of this is equivalent to <code>Mzero.zero(this.getClass())</code>, otherwise this is returned.
     *
     * @param other the alternative - Alternative
     * @return this or other
     */
    Alternative<A, T> alternative(Supplier<? extends Alternative<A, T>> other);

    //TODO useless atm, because it is evaluated eagerly
//    default Alternative<A, MList<T>> many() {
//        BiFunction<T, MList<T>, MList<T>> head = (x, xs) -> xs.append(x);
//
//        Function<MList<T>, Alternative<A, MList<T>>> pure_inner = pure(getClass());
//
//        class Helper {
//            public Supplier<Alternative<A, MList<T>>> many_v() {
//                return () -> some_v().get().alternative(() -> pure_inner.apply(new MList<>()));
//            }
//
//            public Supplier<Alternative<A, MList<T>>> some_v() {
//                return (Supplier<Alternative<A, MList<T>>>) liftA2Lazy(head, many_v());
//            }
//        }
//
//        return new Helper().many_v().get();
//}
//    Alternative<A, MList<T>> some();
}
