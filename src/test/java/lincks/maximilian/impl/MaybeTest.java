package lincks.maximilian.impl;

import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.Maybe.unwrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaybeTest {

    @Test
    void testBind() {
        Maybe<Integer> s = unwrap(new Maybe<>(1)
                .bind(i -> new Maybe<>(String.valueOf(i)))
                .bind(i -> new Maybe<>(Integer.valueOf(i))));

        assertEquals(s.get(),1);
    }
}