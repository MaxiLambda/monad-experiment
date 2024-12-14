# Monads in Java

The whole repository can be found [here](https://github.com/MaxiLambda/monad-experiment).

This project was created as a project for the HKA WS 24/25. <br/>
It explores an implementation of monads in java. Inspiration was taken from haskell's implementation of monads.

The project consists of two parts and an example using the created library. Additional information for each project can
be found in the corresponding README files.

**[monad-experiment](https://github.com/MaxiLambda/monad-experiment/blob/master/monad-experiment/README.md)** contains a
framework for monads in java and some monad
implementations. This is the core part of the project. Most other monad implementations only focus on singular classes
the monad-experience uses the more general approach of defining interfaces for monadic operations. This enables abstract
implementations for all monadic values and encourages code reuse. This approach becomes more powerful when a similar
approach is used implement other abstract operations in interfaces as well. The additional interfaces provided by
monad-experiment include, `Foldable`, `Traversable`, `Functor`, `Applicative` and others.

**[monad-macro](https://github.com/MaxiLambda/monad-experiment/blob/master/monad-macro/README.md)** contains a clojure
macro to port haskell's do-notation to clojure. Java does
not support macros and for the sake of completion a 'JVM-do-notation' macro was required. In contrast to haskell's
do-notation compile-time type-safety is not enforced here (as this is not possible with clojure).

**[monad-parser](https://github.com/MaxiLambda/monad-experiment/blob/master/monad-parser/README.md)** is an example
project implementing a framework for a customizable scripting
language written with the monad implementations from monad-experiment. The user defines operators with an arity and
a precedence. Infix- (only for arity two) and prefix-Operators are supported. The monad parser library creates an
interpreter for the defined language. This can be used to define small DSLs. To show the abilities of this language, see
[cnf-calculator](https://github.com/MaxiLambda/cnf-calculator/blob/master/README.md). This is a calculator to transform
arbitrary logical expressions to cnf.
