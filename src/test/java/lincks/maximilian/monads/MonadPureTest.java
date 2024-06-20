package lincks.maximilian.monads;

import lincks.maximilian.monads.impl.Either;
import lincks.maximilian.monads.impl.Maybe;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.monads.MonadPure.pure;
import static org.junit.jupiter.api.Assertions.*;

class MonadPureTest {

    @Test
    void createMonad() {
        Maybe<Integer> s = pure(1, Maybe.class);
        assertEquals(1, s.get());
    }

    @Test
    void createMonadByDelegate() {
        Either<String, Integer> s = pure(1, Either.class);
        assertInstanceOf(Either.Right.class,s);

        Either.Right<String, Integer> ss = (Either.Right<String, Integer>) s;

        assertEquals(1, ss.value());
    }

}