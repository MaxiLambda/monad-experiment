package lincks.maximilian.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.applicative.ApplicativeConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ToString
@EqualsAndHashCode
public class MList<T> implements Monad<MList<?>, T>, List<T> {

    @Delegate
    private final ArrayList<T> list;

    @ApplicativeConstructor
    public MList(T value) {
        list = new ArrayList<>();
        list.add(value);
    }

    public MList(Collection<T> collection) {
        list = new ArrayList<>(collection);
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

        return new MList<>(r);
    }

    @Override
    public <R> MList<R> map(Function<T, R> f) {
        return unwrap(Monad.super.map(f));
    }

    @Override
    public <R> MList<R> then(Supplier<Monad<MList<?>, R>> f) {
        return unwrap(Monad.super.then(f));
    }
}
