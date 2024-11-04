package lincks.maximilian.impl.monad;

import lincks.maximilian.monadzero.Zero;
import lincks.maximilian.traversable.Traversable;
import lincks.maximilian.util.BBF;
import lincks.maximilian.util.BF;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static lincks.maximilian.impl.monad.MList.unwrap;
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
        assertEquals(Zero.zero(MList.class), MList.empty());
        assertEquals(Zero.zero(MList.class).mplus(new MList<>(1)), new MList<>(1));
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
        //they have to be equal, because the values are accumulated into a new list
        assertEquals(res, list1);
    }

    @Test
    void filterVsFilter() {
        MList<Integer> list1 = new MList<>(1, 2, 3, 4);
        Predicate<Integer> p = i -> i % 2 == 0;
        assertEquals(new MList<>(2, 4), list1.filter(p));
        assertEquals(new MList<>(2, 4), list1.filter(p));
    }

    @Test
    void filterMTest() {
        MList<Integer> list1 = new MList<>(1, 2, 3, 4);
        Function<Integer, Maybe<Boolean>> p = i -> new Maybe<>(i % 2 == 0);
        Function<Integer, Maybe<Boolean>> pEmpty = i -> i == 3 ? Maybe.nothing() : new Maybe<>(i % 2 == 0);
        BF<Integer, Boolean, Maybe<?>> p2 = new BF<>(i -> new Maybe<>(i % 2 == 0)) {
        };

        assertEquals(new MList<>(2, 4), Maybe.unwrap(list1.filterM(p, Maybe.class)).get());
        assertEquals(Maybe.nothing(), Maybe.unwrap(list1.filterM(pEmpty, Maybe.class)));
        assertEquals(new MList<>(2, 4), Maybe.unwrap(list1.filterM(p2)).get());
    }
}
