package lincks.maximilian.monads.impl;

import lincks.maximilian.monads.Monad;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.monads.MonadPure.pure;
import static org.junit.jupiter.api.Assertions.*;

class MaybeTest {

    @Test
    void testBind() {
        Maybe<Integer> s = new Maybe<>(1)
                .bind(i -> new Maybe<>(String.valueOf(i)))
                .bind(i -> new Maybe<>(Integer.valueOf(i))).getM();

        assertEquals(s.get(),1);
    }
}