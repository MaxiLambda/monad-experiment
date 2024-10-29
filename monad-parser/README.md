## Informations about the topic
* https://www.geeksforgeeks.org/construction-of-ll1-parsing-table/
* https://www.usna.edu/Users/cs/wcbrown/courses/F20SI413/firstFollowPredict/ffp.html

# Example Math-like Parser written with the monad-experiment Library

This is a configurable parser used to parse math like languages.
It supports pre-fix operators with arbitrary arity (>0) and binary infix operators.
Operators can bind with different strengths.

The basic language pattern is the following:

```
<-X> is a placeholder Token for custom prefix-operators with an arity of X
<+> is a placeholder Token for custom infix-operators
eps is the empty terminal
int is a placeholder for any iteger token

//weaker binding
E   -> E' | <-1> E | <-2> E E | <-3> E E E |...
E'  -> T E''
E'' -> <+> T E'' | eps

//stronger binding
T   -> T' | <-1> T | <-2> T T | <-3> T T T |...
T'  -> F T''
T'' -> <+> F T'' | eps

F   -> (E) | int
```

It is possible to define an arbitrary amount of levels of binding:

```
E_0   -> E_0' | <-1> E_0 | <-2> E_0 E_0 | <-3> E_0 E_0 E_0 |...
E_0'  -> E_1 E_0''
E_0'' -> <+> E_1 E_0'' | eps

E_1   -> E_1' | <-1> E_1 | <-2> E_1 E_1 | <-3> E_1 E_1 E_1 |...
E_1'  -> E_2 E_1''
E_1'' -> <+> E_2 E_1'' | eps

...as often as required

E_N   -> E_N' | <-1> E_N | <-2> E_N E_N | <-3> E_N E_N E_N |...
E_N'  -> F E_N''
E_N'' -> <+> F E_N'' | eps

F   -> (E_0) | int
```

To parse things a parsing table is needed. Assuming the following language:

```
! as unary prefix operator
@ as unary prefix operator

? as binary prefix operator
% as binary prefix operator

+ as binary infix operator
* as binary infix operator

//weaker binding
E   -> E' | !E | % E E
E'  -> T E''
E'' -> + T E'' | eps

//stronger binding
T   -> T' | @T | ? T T
T'  -> F T''
T'' -> * F T'' | eps

F   -> (E) | int
```

This must lead to the following first-follow-set and parsing table:

|                          | First          | Follow              |
|--------------------------|----------------|---------------------|
| E   -> E' \| !E \| % E E | ! % @ ? \( int | ! % @ ? ( ) int     |
| E'  -> T E''             | @ ? \( int     | ! % @ ? ( ) int     |
| E'' -> + T E'' \| eps    | + eps          | ! % @ ? ( ) int     |
| T   -> T' \| @T \| ? T T | @ ? \( int     | ! % + @ ? ( ) int   |
| T'  -> F T''             | int \(         | ! % + @ ? ( ) int   |
| T'' -> * F T'' \| eps    | * eps          | ! % + @ ? ( ) int   |
| F   -> (E) \| int        | int \(         | ! % + @ ? * ( ) int |

Note: The follow set is way smaller if no postfix-arity > 1 operations are present.


|     | !   | %     | +       | @     | ?     | *       | \(    | \)  | int   | $   |  
|-----|-----|-------|---------|-------|-------|---------|-------|-----|-------|-----|  
| E   | !E  | % E E |         | E'    | E'    |         | E'    |     | E'    |     |  
| E'  |     |       |         | T E'' | T E'' |         | T E'' |     | T E'' |     |  
| E'' | eps | eps   | + T E'' | eps   | eps   |         | eps   | eps | eps   | eps |  
| T   |     |       |         | @ T   | ? T T |         | T'    |     | T'    |     |  
| T'  |     |       |         |       |       |         | F T'' |     | F T'' |     |  
| T'' | eps | eps   | eps     | eps   | eps   | * F T'' | eps   | eps | eps   | eps |  
| F   |     |       |         |       |       |         | ( E ) |     | int   |     |  
Note: "$" denotes the end of the input.