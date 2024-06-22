package lincks.maximilian.monads.impl;

import lincks.maximilian.monads.Monad;
import lincks.maximilian.monads.MonadConstructor;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class MList<T> implements Monad<MList<?>, T>, List<T> {

    @Delegate
    private final ArrayList<T> list;

    @MonadConstructor
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
    public <R> Monad<MList<?>, R> bind(Function<T, Monad<MList<?>, R>> f) {

        List<R> r = list.stream()
                .map(f)
                .map(MList::unwrap)
                .flatMap(mlist -> mlist.list.stream())
                .toList();

        return new MList<>(r);
    }
}
