package lincks.maximilian.monads;

import lincks.maximilian.impl.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.Maybe.unwrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MonadTest {
    @Test
    void testMap() {
       Monad<Maybe<?>,Integer> ii = new Maybe<>(1).map(i -> i +1);
        assertEquals(unwrap(ii).get(), 2);
    }

}