package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import static lincks.maximilian.applicative.Applicative.replicateA;
import static lincks.maximilian.impl.monad.Maybe.unwrap;
import static lincks.maximilian.monadzero.MonadZero.filterM;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaybeTest {

    @Test
    void testBind() {
        Maybe<Integer> s = unwrap(new Maybe<>(1)
                .bind(i -> new Maybe<>(String.valueOf(i)))
                .bind(i -> new Maybe<>(Integer.valueOf(i))));

        assertEquals(s.get(),1);
    }

    @Test
    void testNothing() {
        assertEquals(Maybe.<Integer>nothing().map(i -> i +1), Maybe.nothing());

        assertEquals(Maybe.<Integer>nothing().bind(i -> new Maybe<>(i +1)), Maybe.nothing());
    }

    @Test
    void testFilterM() {
        assertEquals(Maybe.<Integer>nothing(), filterM(i -> i % 2 == 0, new Maybe<>(1)));
        assertEquals(new Maybe<>(0), filterM(i -> i % 2 == 0, new Maybe<>(0)));
    }

    @Test
    void replicateTest() {
        var m = new Maybe<>(1);
        Maybe<MList<Integer>> list = unwrap(replicateA(5, m));

        assertEquals(new Maybe<>(new MList<>(1, 1, 1, 1, 1)), list);
    }
}