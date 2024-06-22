# monad-experiment
A prove of concept library bringing monads to java.
The goal of this library is to provide a generic definition of monads, so code working for all kinds of monads can be written.

The idea is to have a monad interface from which all Monads implement. 
```java
public interface Monad<M extends Monad<M, ?>, T> {

    <R> Monad<M, R> bind(Function<T, Monad<M, R>> f);

    default <R> Monad<M, R> map(Function<T, R> f) {
        return this.bind(f.andThen(pure(this.getClass())));
    }
}
```
Additionaly all Monady should define a one argument constructor and annotade it with @MonadConstructor.
This is the equivalent to the pure function. 
```java
public static <M extends Monad<M, T>, T> M pure(T value, Class<M> clazz)
```
can be used to invoke said constructor in generic Code not knowing the concrete implementations of the Monads.

This split into two classes is neccessary, because java does not allow static methods to be part of an interface.

An exmple implemetation of the Monad is the Maybe class.
```java
public class Maybe<T> implements Monad<Maybe<?>, T> {

    private static final Maybe<Void> nothing = new Maybe<>(null);
    private final T value;

    @MonadConstructor
    public Maybe(T value) {
        this.value = value;
    }

    public static <R> Maybe<R> nothing() {
        return (Maybe<R>) nothing;
    }

    public static <T> Maybe<T> unwrap(Monad<Maybe<?>, T> m) {
        return (Maybe<T>) m;
    }

    public T get() {
        return value;
    }

    @Override
    public <R> Monad<Maybe<?>, R> bind(Function<T, Monad<Maybe<?>, R>> f) {
        return nothing().equals(this) ? nothing() : f.apply(value);
    }
}
```
It is recommended to implement a static `unwarp()` method, like the one seen in Maybe<T>. This helps because `map` and `bind` return instances of `Monad<M, R>` and not the original type.
The casting done in the `unwrap()` method is safe, a long as no class implements a Monad of another class than itself.
