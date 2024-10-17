package lincks.maximilian.monads;

import lincks.maximilian.impl.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.monads.Monad.join;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MonadControlTest {

    @Test
    void joinTest() {
        Maybe<Maybe<Integer>> mmi = new Maybe<>(new Maybe<>(1));
        Maybe<Integer> mi = join(mmi);

        assertEquals(mi.get(), 1);
    }
}