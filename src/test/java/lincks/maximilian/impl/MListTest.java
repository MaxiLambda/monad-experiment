package lincks.maximilian.impl;

import lincks.maximilian.monadzero.MonadZero;
import lincks.maximilian.traversable.Traversable;
import lincks.maximilian.util.BBF;
import lincks.maximilian.util.BF;
import org.junit.jupiter.api.Test;

import static lincks.maximilian.impl.MList.unwrap;
import static lincks.maximilian.monads.Monad.join;
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
        System.out.println(MonadZero.zero(MList.class));
        System.out.println(MonadZero.zero(MList.class).mplus(new MList<>(1)));

    }

    @Test
    void testTraversable() {
        MList<Integer> list1 = new MList<>(1, 2, 3);

        var maybe = list1.traverse(new BF<Integer, Integer, Maybe<?>>(Maybe::new) {
        });

        var listlist = (MList<MList<Integer>>) list1.traverse(new BF<>((Integer v) -> new MList<>(1, v)) {
        });

        assertEquals(new MList<>(1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, 3, 1, 1, 3, 1, 2, 3, 1, 2, 3), join(listlist));
        assertEquals(new Maybe<>(new MList<>(1, 2, 3)), maybe);

        var x = Traversable.sequenceA(new MList<>(new Maybe<>(1), new Maybe<>(2)), Maybe.class);
        assertEquals(new Maybe<>(new MList<>(1, 2)), x);

        MList<Maybe<Integer>> y = new MList<>();
        var yy = Traversable.sequenceA(y, Maybe.class);
        assertEquals(new Maybe<>(new MList<>()), yy);
    }

    @Test
    void testFoldr() {
        MList<String> list1 = new MList<>("1", "2", "3");
        String res = list1.foldr(String::concat, "");
        assertEquals("123", res);
    }

    @Test
    void testFoldrMZero() {
        MList<String> list1 = new MList<>("1", "2", "3");
        var res = MList.unwrap(list1.foldr(new BBF<String, MList<String>, String, MList<?>>((s, sm) -> sm.prepend(s)) {
        }));
        System.out.println(res);
    }
}
