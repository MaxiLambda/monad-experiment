package lincks.maximilian.impl.monad;

import lincks.maximilian.alternative.Alternative;
import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.monadzero.MZero;
import lincks.maximilian.monadzero.MonadZero;
import lincks.maximilian.traversable.Traversable;
import lincks.maximilian.util.BF;
import lincks.maximilian.util.Bottom;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static lincks.maximilian.applicative.ApplicativePure.pureUnsafeClass;

/**
 * Eager Implementation of a Monadic List.
 * Backed by regular Lists. Each list manipulation creates a new List.
 * This class lacks quite a few functions that regular java lists provide.
 * Use {@link #toList()} and {@link #fromList(List)} for these functions for now.
 */
@ToString
@EqualsAndHashCode
public class MList<T> implements MonadPlus<MList<?>, T>, Traversable<MList<?>, T>, Alternative<MList<?>, T> {
    //Create LazyList implementation => can be created from suppliers and is only evaluated on request
    //evaluated values must be preserved => two lists needed internally
    //map, bind etc. create new Lists
    private final List<T> list;

    /**
     * Create a new empty list.
     */
    public MList() {
        list = List.of();
    }

    @ApplicativeConstructor
    public MList(T arg) {
        list = List.of(arg);
    }

    public MList(T... args) {
        list = Arrays.stream(args).toList();
    }

    private MList(List<T> list) {
        this.list = List.copyOf(list);
    }

    /**
     * static function used to create new MLists with {@link MonadZero#zero(Class)}
     */
    @MZero
    public static <T> MList<T> empty() {
        return new MList<>();
    }


    /**
     * Cast to MList if wrapped in other type.
     */
    public static <T> MList<T> unwrap(Bottom<MList<?>, T> m) {
        return (MList<T>) m;
    }

    /**
     * crate a new MList from list. An immutable copy is used.
     */
    public static <T> MList<T> fromList(List<T> list) {
        return new MList<>(list);
    }

    @Override
    public <R> MList<R> sequence(Applicative<MList<?>, Function<T, R>> f) {
        return bind(v -> (MList<R>) f.map(ff -> ff.apply(v)));
    }

    @Override
    public <R> R foldr(BiFunction<T, R, R> acc, R identity) {
        AtomicReference<R> r = new AtomicReference<>(identity);
        list.reversed().forEach(val -> {
            r.set(acc.apply(val, r.get()));
        });
        return r.get();
    }

    @Override
    public <A extends Applicative<A, ?>, R> Applicative<A, MList<R>> traverse(BF<T, R, A> f) {
        if (list.isEmpty()) {
            return (Applicative<A, MList<R>>) pureUnsafeClass(new MList<R>(), f.getType());
        } else {
            Applicative<A, MList<R>> xs = tail().traverse(f);
            Applicative<A, R> x = f.applyTyped(head());
            return x.liftA2((R y, MList<R> ys) -> ys.prepend(y), xs);
        }
    }

    @Override
    public MList<T> alternative(Supplier<? extends Alternative<MList<?>, T>> other) {
        return list.isEmpty() ? unwrap(other.get()) : this;
    }

    @Override
    public <R> MList<R> bind(Function<T, Monad<MList<?>, R>> f) {

        List<R> r = list.stream()
                .map(f)
                .map(MList::unwrap)
                .flatMap(mlist -> mlist.list.stream())
                .toList();

        return fromList(r);
    }

    @Override
    public MList<T> mplus(MonadPlus<MList<?>, T> other) {
        ArrayList<T> l = new ArrayList<>(list);
        l.addAll(unwrap(other).toList());
        return new MList<>(l);
    }

    @Override
    public <R> MList<R> map(Function<T, R> f) {
        return unwrap(MonadPlus.super.map(f));
    }

    @Override
    public <R> MList<R> then(Supplier<Monad<MList<?>, R>> f) {
        return unwrap(MonadPlus.super.then(f));
    }


    /**
     * @return a copy of this as a List.
     */
    public List<T> toList() {
        return List.copyOf(list);
    }

    /**
     * @return the first element of the list. Throws an exception if called on an empty list.
     */
    public T head() {
        return list.getFirst();
    }

    /**
     * @return a new MList without the first element. Throws an excepting if called on an empty list.
     */
    public MList<T> tail() {
        return new MList<>(list.subList(1, list.size()));
    }

    /**
     * Prepends a value to this.
     *
     * @param t the value to prepend.
     * @return a new MList with t prepended.
     */
    public MList<T> prepend(T t) {
        List<T> l = new ArrayList<>(List.of(t));
        l.addAll(list);
        return new MList<>(l);
    }

    /**
     * Filter MList by a predicate.
     *
     * @param p the predicate
     * @return a new MList only with values that satisfy p.
     */
    public MList<T> filter(Predicate<T> p) {
        return new MList<>(list.stream().filter(p).toList());
    }

}