package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.monad.Maybe.unwrap;
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
}