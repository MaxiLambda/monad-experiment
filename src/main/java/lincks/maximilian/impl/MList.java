package lincks.maximilian.impl;

import lincks.maximilian.applicative.Applicative;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monadpluszero.MonadPlusZero;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.monadzero.MZero;
import lincks.maximilian.monadzero.MonadZero;
import lincks.maximilian.traversable.Traversable;
import lincks.maximilian.util.Bottom;
import lincks.maximilian.util.func.ApplicativeFunction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static lincks.maximilian.applicative.ApplicativePure.pure;

@ToString
@EqualsAndHashCode
public class MList<T> implements MonadPlusZero<MList<?>, T>, Traversable<MList<?>, T> {

    private final List<T> list;

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

    @MZero
    public static <T> MList<T> empty() {
        return new MList<>();
    }

    public static <T, M extends Bottom<MList<?>, T>> MList<T> unwrap(M m) {
        return (MList<T>) m;
    }

    public static <T> MList<T> fromList(List<T> list) {
        return new MList<>(list);
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
    public <R> MList<R> map(Function<T, R> f) {
        return unwrap(MonadPlusZero.super.map(f));
    }

    @Override
    public <R> MList<R> then(Supplier<Monad<MList<?>, R>> f) {
        return unwrap(MonadPlusZero.super.then(f));
    }

    @Override
    public MList<T> mplus(MonadPlus<MList<?>, T> other) {
        ArrayList<T> l = new ArrayList<>(list);
        l.addAll(unwrap(other).toList());
        return new MList<>(l);
    }

    public List<T> toList() {
        return List.copyOf(list);
    }

    public T head() {
        return list.get(0);
    }

    public MList<T> tail() {
        return new MList<>(list.subList(1, list.size()));
    }

    public MList<T> append(T t) {
        List<T> l = new ArrayList<>(List.of(t));
        l.addAll(list);
        return new MList<>(l);
    }

    @Override
    public <R> MList<R> sequence(Applicative<MList<?>, Function<T, R>> f) {
        return bind(v -> (MList<R>) f.map(ff ->  ff.apply(v)));
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
    public <A extends Applicative<A, ?>, R> Applicative<A, MList<R>> traverse(ApplicativeFunction<T, R, A> f) {
        if (list.isEmpty()) {
            return (Applicative<A, MList<R>>) pure(new MList<R>(), (Class<Applicative>) f.getApplicativeType());
        } else {
            Applicative<A, MList<R>> xs = tail().traverse(f);
            Applicative<A, R> x = f.apply(head());
            return x.liftA2((R y, MList<R> ys) -> ys.append(y), xs);
        }
    }
}