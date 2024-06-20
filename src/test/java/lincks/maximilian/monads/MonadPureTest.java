package lincks.maximilian.monads;

import lincks.maximilian.monads.impl.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.monads.MonadPure.pure;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MonadPureTest {

    @Test
    void createMonad() {

        Maybe<Integer> s = pure(1, Maybe.class);
        assertEquals(1, s.get());
    }
}