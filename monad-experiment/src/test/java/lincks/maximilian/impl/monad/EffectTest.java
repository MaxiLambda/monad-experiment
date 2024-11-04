package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static lincks.maximilian.impl.monad.Effect.unwrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EffectTest {
    @Test
    void lazyTest() {
        AtomicInteger count = new AtomicInteger(0);
        var e = Effect.fromSupplier(() -> {
            System.out.println("hallo");
            count.incrementAndGet();
            return "hallo";
        });

        assertEquals(0, count.get());

        e = unwrap(e.map(s -> s + "!"));
        assertEquals(0, count.get());

        String s = e.get();

        assertEquals("hallo!", s);
        assertEquals(1, count.get());
    }

    @Test
    void errorTest() {
        AtomicInteger count = new AtomicInteger(0);
        var e = Effect.of(1);

        e = unwrap(e.then(() -> Effect.fromSupplier(() -> {
            throw new RuntimeException();
        })));

        e = unwrap(e.map(i -> i + 1));

        assertThrows(RuntimeException.class, e::get);
        assertEquals(0, count.get());
    }

    //many does not work atm. therefore this test would be useless
//    @Test
//    void alternative() {
//        var e  = Effect.fromRunnable(() -> System.out.println("hallo"));
//        unwrap(e.many());
//    }
}
