package lincks.maximilian.monads;

import lincks.maximilian.impl.monad.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.monad.Maybe.unwrap;
import static lincks.maximilian.monads.Monad.join;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MonadTest {
    @Test
    void testMap() {
       Monad<Maybe<?>,Integer> ii = new Maybe<>(1).map(i -> i +1);
        assertEquals(unwrap(ii).get(), 2);
    }

    @Test
    void joinTest() {
        Maybe<Maybe<Integer>> mmi = new Maybe<>(new Maybe<>(1));
        Maybe<Integer> mi = join(mmi);

        assertEquals(mi.get(), 1);
    }

}