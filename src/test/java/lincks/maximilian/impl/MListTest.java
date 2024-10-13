package lincks.maximilian.impl;

import lincks.maximilian.monadplus.MonadPlusZero;
import org.junit.jupiter.api.Test;

import java.util.List;

import static lincks.maximilian.impl.MList.unwrap;
import static lincks.maximilian.util.func.F.curry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MListTest {

    @Test
    void testSequence() {
        MList<Integer> list1 = new MList<>(1,2,3);
        MList<Integer> list2 = new MList<>(4,5,6);

        var x = unwrap(list1.liftA2(Integer::sum, list2));

        var y = unwrap(list1.sequence(list2.liftA(curry(Integer::sum))));

        assertEquals(x,y);
    }

    @Test
    void testThen() {
        MList<Integer> list1 = new MList<>(1,2,3);
        MList<Integer> list2 = new MList<>(4,5,6);

        var x = list1.then(() -> list2);

        System.out.println(x);

        System.out.println(new MList<>().then(() -> list2));

    }

    @Test
    void testZero() {
        System.out.println(MonadPlusZero.zero(MList.class));
        System.out.println(MonadPlusZero.zero(MList.class).mplus(new MList<>(1)));

    }
}
