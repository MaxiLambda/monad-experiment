package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.monad.Either.unwrap;
import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void useRight() {
        Either<String, Integer> m = unwrap(new Either.Right<String, Integer>(1).map(i -> i +1));

        assertInstanceOf(Either.Right.class, m);
        Either.Right<String, Integer> right = (Either.Right<String, Integer>) m;
        assertEquals(2, right.value());
    }

    @Test
    void useLeft() {
        String lambda = "lambda";
        Either<String, String> m = new Either.Left<String, String>(lambda).map(i -> i +"!");

        assertInstanceOf(Either.Left.class, m);
        Either.Left<String, String> left = (Either.Left<String, String>) m;
        assertEquals(lambda, left.value());
    }

    @Test
    void useRightLeft() {
        Either<String, Integer> m =new Either.Right<String, Integer>(1)
                .map(i -> i +1)
                .bind(ignore -> new Either.Left<>("oh no"));

        assertInstanceOf(Either.Left.class, m);
        Either.Left<String, Integer> left = (Either.Left<String, Integer>) m;
        assertEquals("oh no", left.value());
    }
}