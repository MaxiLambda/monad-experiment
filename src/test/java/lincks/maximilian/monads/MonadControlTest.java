package lincks.maximilian.monads;

import lincks.maximilian.monads.impl.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.monads.MonadControl.join;
import static org.junit.jupiter.api.Assertions.*;

class MonadControlTest {

    @Test
    void joinTest() {
        Maybe<Maybe<Integer>> mmi = new Maybe<>(new Maybe<>(1));
        Maybe<Integer> mi = join(mmi);

        assertEquals(mi.get(), 1);
    }
}