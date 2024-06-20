package lincks.maximilian.monads;

import lincks.maximilian.monads.impl.Maybe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonadTest {
    @Test
    void testMap() {
       Maybe<Integer> ii = new Maybe<>(1).map(i -> i +1);
       assertEquals(ii.get(), 2);
    }
}