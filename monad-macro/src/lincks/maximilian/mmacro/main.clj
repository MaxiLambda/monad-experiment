(ns lincks.maximilian.mmacro.main
  (:import (java.util.function Function)
           (lincks.maximilian.monads Monad)))

(defn ^:private bind [^Monad m ^Function f]
  (.bind m f))

(defn ^:private then [^Monad m ^Function f]
  (.then m f))

(defn ^:private m-bind [[n-var expr] inner]
  `(bind ~expr (fn [~n-var] ~inner)))

(defn ^:private m-then [expr inner]
  `(then ~expr (fn [] ~inner)))

(defmacro doM
  "Haskell's do-Notation for Monads in clojure.
  In clojure terms this is similar to `for`. But while `for` works only on lists, `doM`
  works on all monads. It supports `bind` and `then`. Then can be used to short-circuit
  operations. `bind` is used with the `[<symbol> <expression>]` syntax, while `then` is
  used by `<expression>`.
  Usage:
  (doM [a (MList/fromList [2 1])]
      (MList.)
      [b (MList/fromList [\"a\" \"b\"])]
      (MonadPure/pure [a b] MList))
  ==> #object[lincks.maximilian.impl.monad.MList 0x37ffb7 MList(list=[])]

  (doM2 [a (MList/fromList [2 1])]
        [b (MList/fromList [\"a\" \"b\"])]
        (MonadPure/pure [a b] MList))
  => #object[lincks.maximilian.impl.monad.MList 0x1aed6057 MList(list=[[2 \"a\"], [2 \"b\"], [1 \"a\"], [1 \"b\"]])]"
  [& exprs]
  (let [[ret-expr & prev-expr] (reverse exprs)]
    (reduce (fn [inner expr]
              (if (vector? expr)
                (m-bind expr inner)
                (m-then expr inner)))
            ret-expr
            prev-expr)))

