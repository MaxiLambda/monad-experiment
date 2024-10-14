package lincks.maximilian.alternative;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.impl.MList;
import lincks.maximilian.monadzero.MonadZero;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static lincks.maximilian.applicative.ApplicativePure.pure;

public interface Alternative<A extends Alternative<A, ?>, T> extends Applicative<A, T>, MonadZero<A, T> {
    //zero from MonadZero is re-used as "empty"

    Alternative<A, T> alternative(Supplier<Alternative<A, T>> other);

    default Alternative<A, MList<T>> many() {
        BiFunction<T, MList<T>, MList<T>> head = (x, xs) -> xs.append(x);

        Function<MList<T>, Alternative<A, MList<T>>> pure_inner = pure(getClass());

        class Helper {
            public Alternative<A, MList<T>> many_v() {
                return some_v().alternative(() -> pure_inner.apply(new MList<>()));
            }

            public Alternative<A, MList<T>> some_v() {
                return (Alternative<A, MList<T>>) liftA2(head, many_v());
            }
        }

        return new Helper().many_v();
    }
//    Alternative<A, MList<T>> some();
}
