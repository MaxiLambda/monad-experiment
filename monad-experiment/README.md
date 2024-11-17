# monad-experiment

A proof of concept library bringing monads to java.
The goal of this library is to provide a generic definition of monads, so code working for all kinds of monads can be
written.

## The Monad interface

The idea is to have a monad interface from which all Monads implement.

```java
public interface Monad<M extends Monad<M, ?>, T> {

    <R> Monad<M, R> bind(Function<T, Monad<M, R>> f);

    default <R> Monad<M, R> map(Function<T, R> f) {
        return this.bind(f.andThen(pure(this.getClass())));
    }

    default <R> Monad<M, R> then(Supplier<Monad<M, R>> f) {
        return bind((T ignore) -> f.get());
    }
}
```

Additionaly all Monady should define a one argument constructor and annotate it with `@MonadConstructor`.
This is the equivalent to the pure function.

```java
public static <M extends Monad<M, T>, T> M pure(T value, Class<M> clazz)
```

can be used to invoke said constructor in generic Code not knowing the concrete implementations of the Monads.

This split into two classes is necessary, because java does not allow static methods to be part of an interface.

### Example: The Maybe Monad

An example implementation of the Monad is the Maybe class.

```java
public class Maybe<T> implements Monad<Maybe<?>, T> {

    private static final Maybe<Void> nothing = new Maybe<>(null);
    private final T value;

    @ApplicativeConstructor
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
    public <R> Maybe<R> bind(Function<T, Monad<Maybe<?>, R>> f) {
        return nothing().equals(this) ? nothing() : f.andThen(Maybe::unwrap).apply(value);
    }
}
```

It is recommended to implement a static `unwarp()` method, like the one seen in Maybe<T>. This helps because `map` and
`bind` return instances of `Monad<M, R>` and not the original type.
The casting done in the `unwrap()` method is safe, a long as no class implements a Monad of another class than itself.

### The Signature of Monad<M extends Monad<M, ?>, T> is horrible! Why?

The Signature of the Monad interface looks like this due to constraints in java generics.
It is currently (2024) not possible to have a generic type with another generic type Parameter.
It might look appealing to define a Monad as `Moand<T>` but this prevents operations like `bind`.

```java
public <R> Monad<R> bind(Function<T, Monad<R>> f);
```

This implementation of bind could return any kind of Monad, as long as it is of type `Monad<R>`. This is
bad, because we only want to use functions that return an Instance of the same Moaned we called bind on
and not any kind of Monad.

## Monads in Java

### Java has Monads already

Java already has many Monads, but you might not have noticed them as such.
The following classes are basically Monads:

* `java.util.Optional` defined by `Optional::of` and `Optional::flatMap`
* `java.util.stream.Steam` defined by `Stream::of` and `Stream::flatMap`
* `java.util.concurrent.CompletableFuture` defined by `CompletableFuture::completedFuture` and
  `CompletableFuture::thenCompose`

 [Project Reactor](https://projectreactor.io/), a popular library, used to build reactive java applications,
 uses two Monads as a main concept to represent "one or none" or "many or none" reactive values.
 
* `reactor.core.publisher.Mono` defined by `Mono::just` and `Mono::flatMap`
* `reactor.core.publisher.Just` defined by `Just::just` and `Just::flatMap`

### Why a monad class? Everything works already!

The benefit of unifying all Monads with a dedicated class, is the ability to write generic code.
Of course this requires more than just the Monad interface. Take Haskell for example, there are multiple classes 
describing the abilities of types.
![implemented_interfaces](docs/558px-FunctorHierarchy.svg.png)
If these Types are given, more abstract functionalities can be provided.

Existing libraries often provide useful monads. Sadly they don't offer ways to write common code for these monads. Utility
functions have to be rewritten for each monad, which heavily increases the amount boilerplate code. Many small but useful functions,
can be composed to the required task. This enforces code reuse and promotes functional paradigm. 

This projects type hierarchy is inspired by the haskell hierarchy seen above. Key differences are the splitting of
Haskell's `MonadPlus` into `MonadZero`, `MonadPlus` and `MonadPlusZero` and the introduction of the `Top` type as a 
common ancestor to the other types.

![type_hierarchy](docs/interfaces.png)

### Monad Libraries is Java

#### [Vavr](https://github.com/vavr-io/vavr/tree/master)
One of the more known functional-programming libraries for java is [vavr](https://github.com/vavr-io/vavr/tree/master). 
Vavr comes with a lot of functionality which can be considered a small standard-library. It provides not only
immutable data-structures, an altered collections library, property-based testing and more expressive function-interfaces but monadic data-types as well. 
Among these types are types similar to existing java types like `Option` (similar to `Optional`) and `Future` (similar to `CompletableFuture`).
`Option` and `Future` repackage existing functionality in a vavr like fashion. Other types like `Try` or `Either` are novel to the java ecosystem.
`Either` is similar to `Option`, but failure now relates to a value, for example the reason why the computation has failed.
`Try` is used to manipulate the control flow in applications. When an `Exception` is thrown, the control-flow differs from the data flow.
`Try` abstracts computations that might throw exceptions, exceptions don't interrupt the data-flow anymore. Control flows like usually.  

The `io.vavr.Value` type, which is the top-type of all monadic values in vavr, is more similar to a Functor/Alternative combination. A `Value` must
have a `map` function (Functor) to and can be checked for emptiness (Alternative without the Applicative parts - no pure function).

### [Better Monads](https://github.com/jasongoodwin/better-java-monads/tree/master)

[Better Monads](https://github.com/jasongoodwin/better-java-monads/tree/master) is a small java library trying to improve the
java 8 experience with a `Try` type. The `Try` type offers similar functionality to vavrs-Try. Better monads includes a static
`sequence` Function to transform a `List` of `CompletableFuture`s into a `CompletableFuture` of a `List` 
(`List<CompletableFuture<X>>` -> `CompletableFuture<List<X>>`). This library tries to be non-intrusive.

### [purefun](https://github.com/tonivade/purefun/tree/master)

[purefun](https://github.com/tonivade/purefun/tree/master) is a functional java library similar to this one.
It offers Monads (and implementations for many monads) and similar types (Monoid, Applicative, Functor, Foldable, etc...) and other things known from haskell
like optics (updating of nested structures) monad-transformers. All Monads from this library share a common base type.
//TODO write more
//basically the same as this lib but more powerful