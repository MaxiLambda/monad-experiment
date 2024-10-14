package lincks.maximilian.impl;

import lincks.maximilian.monadplus.MonadPlusZero;
import lincks.maximilian.util.func.ApplicativeFunction;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.MList.unwrap;
import static lincks.maximilian.monads.MonadControl.join;
import static lincks.maximilian.util.func.F.curry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MListTest {

    @Test
    void testSequence() {
        MList<Integer> list1 = new MList<>(1, 2, 3);
        MList<Integer> list2 = new MList<>(4, 5, 6);

        var x = unwrap(list1.liftA2(Integer::sum, list2));

        var y = unwrap(list1.sequence(list2.liftA(curry(Integer::sum))));

        assertEquals(x, y);
    }

    @Test
    void testThen() {
        MList<Integer> list1 = new MList<>(1, 2, 3);
        MList<Integer> list2 = new MList<>(4, 5, 6);

        var x = list1.then(() -> list2);

        assertEquals(new MList<>(4, 5, 6, 4, 5, 6, 4, 5, 6), x);
        assertEquals(new MList<>(), new MList<>().then(() -> list2));
    }

    @Test
    void testZero() {
        System.out.println(MonadPlusZero.zero(MList.class));
        System.out.println(MonadPlusZero.zero(MList.class).mplus(new MList<>(1)));

    }

    @Test
    void testTraversable() {
        MList<Integer> list1 = new MList<>(1, 2, 3);

        var maybe = list1.traverse(new ApplicativeFunction<Integer, Integer, Maybe<?>>(Maybe::new) {
        });

        var listlist = (MList<MList<Integer>>) list1.traverse(new ApplicativeFunction<>((Integer v) -> new MList<>(1, v)) {
        });

        assertEquals(new MList<>(1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 3, 1, 1, 3, 1, 2, 3, 1, 2, 3), join(listlist));
        assertEquals(new Maybe<>(new MList<>(1,2,3)),maybe);
    }
}
