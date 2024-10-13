package lincks.maximilian.impl;

import lincks.maximilian.monadplus.MZero;
import lincks.maximilian.monadplus.MonadPlus;
import lincks.maximilian.monads.Monad;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ToString
@EqualsAndHashCode
public class MList<T> implements MonadPlus<MList<?>, T> {

    private final List<T> list;

    public MList() {
        list = List.of();
    }

    @MZero
    public static <T> MList<T> empty() {
        return new MList<>();
    }

    @ApplicativeConstructor
    public MList(T... args) {
        list = Arrays.stream(args).toList();
    }

    private MList(List<T> list) {
        this.list = List.copyOf(list);
    }

    public static <T> MList<T> unwrap(Monad<MList<?>, T> m) {
        return (MList<T>) m;
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
        return unwrap(MonadPlus.super.map(f));
    }

    @Override
    public <R> MList<R> then(Supplier<Monad<MList<?>, R>> f) {
        return unwrap(MonadPlus.super.then(f));
    }

    @Override
    public MList<T> mplus(MonadPlus<MList<?>, T> other) {
        return new MList<>();
    }

    public static <T> MList<T> fromList(List<T> list) {
        return new MList<>(list);
    }

    public List<T> toList() {
        return List.copyOf(list);
    }
}
