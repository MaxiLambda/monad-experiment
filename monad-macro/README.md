# monad-macro

This clojure project contains the implementation of the `doM` macro.

`doM` is a port of the haskell's do notation to clojure:
```clojure
;;example 1
(doM [a (MList/fromList [2 1])]
      (MList.)
      [b (MList/fromList ["a" "b"])]
      (MonadPure/pure [a b] MList))

  ==> #object[lincks.maximilian.impl.monad.MList 0x37ffb7 \"MList(list=[])\"]

  ;;example 2
  (doM2 [a (MList/fromList [2 1])]
        [b (MList/fromList ["a" "b"])]
        (MonadPure/pure [a b] MList))

  ==> #object[lincks.maximilian.impl.monad.MList 0x1aed6057 \"MList(list=[[2 "a"], [2 "b"], [1 "a"], [1 "b"]])]\""
```

the haskell version would look like this:

```haskell
---example 1
do
      a <- [2 1]
      []
      b <- ["a" "b"]
      return $ [a b]

==> []

---example 2
do
      a <- [2 1]
      []
      b <- ["a" "b"]
      return $ [a b]
==> [(2,"a"),(2,"b"),(1,"a"),(1,"b")]
```
