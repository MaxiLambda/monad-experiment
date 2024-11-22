package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.Effect;
import lincks.maximilian.impl.monad.Either;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.parser.ast.*;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {


    @Test
    void runMathLike() {
        //example for the language given in the README
        //! as unary prefix operator level 0
        //@ as unary prefix operator level 1
        //% as binary prefix operator level 0
        //? as binary prefix operator level 1
        //+ as binary infix operator level 0
        //* as binary infix operator level 1

        //E   -> E' | !E | % E E
        //E'  -> T E''
        //E'' -> + T E'' | eps
        //T   -> T' | @T | ? T T
        //T'  -> F T''
        //T'' -> * F T'' | eps
        //F   -> (E) | int

        Symbol exclamationS = new Symbol("!");
        Symbol atS = new Symbol("@");
        Symbol percentS = new Symbol("%");
        Symbol questionS = new Symbol("?");
        Symbol plusS = new Symbol("+");
        Symbol starS = new Symbol("*");

        Lexer lexer = new Lexer(new MList<>(exclamationS, atS, percentS, questionS, plusS, starS));

        //custom Operations
        PrefixOp<Integer> operator1 = new PrefixOp<>(exclamationS, 1, 0);
        PrefixOp<Integer> operator2 = new PrefixOp<>(atS, 1, 1);
        PrefixOp<Integer> operator3 = new PrefixOp<>(percentS, 2, 0);
        PrefixOp<Integer> operator4 = new PrefixOp<>(questionS, 2, 1);
        InfixOp<Integer> operator5 = new InfixOp<>(plusS, 0);
        InfixOp<Integer> operator6 = new InfixOp<>(starS, 1);

        Map<Symbol, OperatorToken<Integer>> operators = Stream.of(operator1, operator2, operator3, operator4, operator5, operator6).collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Integer> parser = new Parser<>(lexer, operators);

        Function<Literal<Integer>, Integer> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<Integer> v -> {
                    return Integer.valueOf(v.getSymbol().symbol());
                }
                case ValueLiteral<Integer> v -> {
                    return v.getValue();
                }
            }
        };

        Context<Integer> context = new Context<>(Map.of(
                exclamationS, l -> {
                    var x = fromLiteral.apply(l.head());
                    var res = Stream.iterate(x, i -> i - 1).mapToInt(Integer::intValue).takeWhile(i -> i > 0).reduce(1, (i, j) -> i * j);
                    return new ValueLiteral<>(res);
                },
                atS, l -> {
                    var x = fromLiteral.apply(l.head());
                    return new ValueLiteral<>(x * x);
                },
                percentS, l -> {
                    var xs = l.map(fromLiteral);
                    var x = xs.head();
                    var y = xs.tail().head();
                    return new ValueLiteral<>(x % y);
                },
                questionS, l -> {
                    var x = fromLiteral.apply(l.head());
                    return new ValueLiteral<>(-x);
                },
                plusS, l -> {
                    var xs = l.map(fromLiteral);
                    var x = xs.head();
                    var y = xs.tail().head();
                    return new ValueLiteral<>(x + y);
                },
                starS, l -> {
                    var xs = l.map(fromLiteral);
                    var x = xs.head();
                    var y = xs.tail().head();
                    return new ValueLiteral<>(x * y);
                }
        ));
        Interpreter<Integer> interpreter = new Interpreter<>(parser, fromLiteral, context);
        assertEquals(7, interpreter.run("(!1)+2*(%3@4)"));

    }

    @Test
    void minus() {
        Symbol minusS = new Symbol("-");

        Lexer lexer = new Lexer(new MList<>(minusS));

        //custom Operations
        InfixOp<Integer> operator5 = new InfixOp<>(minusS, 0);

        Map<Symbol, OperatorToken<Integer>> operators = Stream.of(operator5).collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Integer> parser = new Parser<>(lexer, operators);

        Function<Literal<Integer>, Integer> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<Integer> v -> {
                    return Integer.valueOf(v.getSymbol().symbol());
                }
                case ValueLiteral<Integer> v -> {
                    return v.getValue();
                }
            }
        };

        Context<Integer> context = new Context<>(Map.of(
                minusS, l -> {
                    var xs = l.map(fromLiteral);
                    var x = xs.head();
                    var y = xs.tail().head();
                    return new ValueLiteral<>(x - y);
                }
        ));
        Interpreter<Integer> interpreter = new Interpreter<>(parser, fromLiteral, context);
        assertEquals(-1, interpreter.run("1-1-1"));
    }


    @Test
    void customLangTest() {
        //scope is held locally only
        record Scope(Map<Symbol, Integer> scope, Either<Symbol, Integer> value) {
        }

        Symbol concat = new Symbol(",");
        Symbol storeVal = new Symbol("@");
        Symbol add = new Symbol("+");
        Symbol mul = new Symbol("*");

        Lexer lexer = new Lexer(new MList<>(concat, storeVal, add, mul));

        //custom Operations
        InfixOp<Scope> operator1 = new InfixOp<>(concat, 2);
        InfixOp<Scope> operator3 = new InfixOp<>(add, 0);
        InfixOp<Scope> operator4 = new InfixOp<>(mul, 1);
        PrefixOp<Scope> operator2 = new PrefixOp<>(storeVal, 2, 3);

        Map<Symbol, OperatorToken<Scope>> operators = Stream.of(operator1, operator2, operator3, operator4)
                .collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Scope> parser = new Parser<>(lexer, operators);
        Function<Literal<Scope>, Scope> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<Scope> v -> {
                    return new Scope(Map.of(), Either.fromEffect(
                            Effect.fromSupplier(() -> Integer.parseInt(v.getSymbol().symbol())), v.getSymbol()));
                }
                case ValueLiteral<Scope> v -> {
                    return v.getValue();
                }
            }
        };

        Context<Scope> context = new Context<>(Map.of(
                concat, l -> {
                    var xss = l.map(fromLiteral);
                    var xs = xss.head();

                    //literal
                    var x = xss.tail().head();

                    var newScope = new HashMap<>(xs.scope);
                    newScope.putAll(x.scope);

                    Either<Symbol, Integer> value = switch (x.value) {
                        case Either.Left<Symbol, Integer> left -> new Either.Right<>(newScope.get(left.value()));
                        case Either.Right<Symbol, Integer> right -> right;
                    };
                    return new ValueLiteral<>(new Scope(newScope, value));
                },
                storeVal, l -> {
                    var xss = l.map(fromLiteral);
                    var variable = xss.head();
                    var value = xss.tail().head();

                    var newScope = new HashMap<>(variable.scope);
                    newScope.putAll(value.scope);
                    //is asLeft or asRight fails things were bad anyways
                    newScope.put(variable.value().asLeft().value(), value.value.asRight().value());
                    return new ValueLiteral<>(new Scope(newScope, value.value));
                }
                ,
                add, l -> {
                    var xss = l.map(fromLiteral);
                    var xs = xss.head();
                    var x = xss.tail().head();

                    var newScope = new HashMap<>(xs.scope);
                    newScope.putAll(x.scope);

                    var val1 = switch (xs.value) {
                        case Either.Left<Symbol, Integer> left -> newScope.get(left.value());
                        case Either.Right<Symbol, Integer> right -> right.value();
                    };
                    var val2 = switch (x.value) {
                        case Either.Left<Symbol, Integer> left -> newScope.get(left.value());
                        case Either.Right<Symbol, Integer> right -> right.value();
                    };

                    return new ValueLiteral<>(new Scope(newScope, new Either.Right<>(val1 + val2)));
                }
                ,
                mul, l -> {
                    var xss = l.map(fromLiteral);
                    var xs = xss.head();
                    var x = xss.tail().head();

                    var newScope = new HashMap<>(xs.scope);
                    newScope.putAll(x.scope);

                    var val1 = switch (xs.value) {
                        case Either.Left<Symbol, Integer> left -> newScope.get(left.value());
                        case Either.Right<Symbol, Integer> right -> right.value();
                    };
                    var val2 = switch (x.value) {
                        case Either.Left<Symbol, Integer> left -> newScope.get(left.value());
                        case Either.Right<Symbol, Integer> right -> right.value();
                    };

                    return new ValueLiteral<>(new Scope(newScope, new Either.Right<>(val1 * val2)));
                }
        ));
        Interpreter<Scope> interpreter = new Interpreter<>(parser, fromLiteral, context);

        //context is held locally, therefore "@a;1;,1;+a;*2;" won't work because it is evaluated as
        // "(((@a;1;),1;)+(a;*2));"
        assertEquals(7, (interpreter.run("1+2*3").value().asRight().value()));
        assertEquals(3, (interpreter.run("@a 1,@b 2,a+b").value().asRight().value()));
        assertEquals(3, (interpreter.run("@a 1,@b 2,a+2").value().asRight().value()));
        assertEquals(6, (interpreter.run("@a 2,@b 3,a*b").value().asRight().value()));
        assertEquals(7, (interpreter.run("@a 2,@b 3,a*b+1").value().asRight().value()));
        assertEquals(9, (interpreter.run("(@a 2,@b 3,1+a)*b").value().asRight().value()));
        assertEquals(12, (interpreter.run("(@a(2+1),@b 3,1+a)*b").value().asRight().value()));
        assertEquals(12, (interpreter.run("(1+@a(2+1),a)*@b 3,b").value().asRight().value()));
    }

    @Test
    void customLangTest2() {
        //same as customLangTest but with global scope instead of local
        Symbol concat = new Symbol(",");
        Symbol storeVal = new Symbol("@");
        Symbol add = new Symbol("+");
        Symbol mul = new Symbol("*");

        Lexer lexer = new Lexer(new MList<>(concat, storeVal, add, mul));

        //custom Operations
        InfixOp<Either<Symbol, Integer>> operator1 = new InfixOp<>(concat, 2);
        InfixOp<Either<Symbol, Integer>> operator3 = new InfixOp<>(add, 0);
        InfixOp<Either<Symbol, Integer>> operator4 = new InfixOp<>(mul, 1);
        PrefixOp<Either<Symbol, Integer>> operator2 = new PrefixOp<>(storeVal, 2, 3);

        Map<Symbol, OperatorToken<Either<Symbol, Integer>>> operators = Stream.of(operator1, operator2, operator3, operator4)
                .collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Either<Symbol, Integer>> parser = new Parser<>(lexer, operators);
        Function<Literal<Either<Symbol, Integer>>, Either<Symbol, Integer>> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<Either<Symbol, Integer>> v -> {
                    return Either.fromEffect(
                            Effect.fromSupplier(() -> Integer.parseInt(v.getSymbol().symbol())), v.getSymbol());
                }
                case ValueLiteral<Either<Symbol, Integer>> v -> {
                    return v.getValue();
                }
            }
        };

        final Map<Symbol, Integer> scope = new HashMap<>();

        final Function<Either<Symbol, Integer>, Integer> resolve = (s) -> switch (s) {
            case Either.Left<Symbol, Integer> left -> scope.get(left.value());
            case Either.Right<Symbol, Integer> right -> right.value();
        };

        Context<Either<Symbol, Integer>> context = new Context<>(Map.of(
                concat, l -> {
                    var xss = l.map(fromLiteral);
                    var x = xss.tail().head();
                    return new ValueLiteral<>(x);
                },
                storeVal, l -> {
                    var xss = l.map(fromLiteral);
                    var variable = xss.head();
                    var value = xss.tail().head();

                    var resVal = resolve.apply(value);

                    scope.put(variable.asLeft().value(), resVal);

                    return new ValueLiteral<>(new Either.Right<>(resVal));
                },
                add, l -> {
                    var xss = l.map(fromLiteral);
                    var xs = xss.head();
                    var x = xss.tail().head();

                    var resVal = resolve.apply(xs) + resolve.apply(x);
                    return new ValueLiteral<>(new Either.Right<>(resVal));
                },
                mul, l -> {
                    var xss = l.map(fromLiteral);
                    var xs = xss.head();
                    var x = xss.tail().head();

                    var resVal = resolve.apply(xs) * resolve.apply(x);
                    return new ValueLiteral<>(new Either.Right<>(resVal));
                }
        ));
        Interpreter<Either<Symbol, Integer>> interpreter = new Interpreter<>(parser, fromLiteral, context);

        //all expressions form customLangTest hold
        assertEquals(7, (interpreter.run("1 +2 *3 ").asRight().value()));
        assertEquals(3, (interpreter.run("@a 1 ,@b 2 ,a +b ").asRight().value()));
        assertEquals(3, (interpreter.run("@a 1 ,@b 2 ,a +2 ").asRight().value()));
        assertEquals(6, (interpreter.run("@a 2 ,@b 3 ,a *b ").asRight().value()));
        assertEquals(7, (interpreter.run("@a 2 ,@b 3 ,a *b +1 ").asRight().value()));
        assertEquals(9, (interpreter.run("(@a 2 ,@b 3 ,1 +a )*b ").asRight().value()));
        assertEquals(12, (interpreter.run("(@a (2 +1),@b 3 ,1 +a )*b ").asRight().value()));
        assertEquals(12, (interpreter.run("(1 +@a (2 +1 ),a )*@b 3 ,b ").asRight().value()));

        //braces are no longer needed to propagate context correctly
        assertEquals(9, (interpreter.run("""
                @a 2,
                @b 3,
                (1+a)*b
                """).asRight().value()));

        //test operator precedence
        assertEquals(7, (interpreter.run("""
                @a 2,
                @b 3,
                1+a*b
                """).asRight().value()));
    }

    class Interpreter3 {
        //REMOVE EQUIVALENCES AND IMPLICATIONS

        Symbol negation = new Symbol("!");
        Symbol implication = new Symbol("=>");
        Symbol equivalence = new Symbol("<>");
        Symbol union = new Symbol("||");
        Symbol intersection = new Symbol("&&");

        Lexer lexer = new Lexer(new MList<>(negation, implication, equivalence, union, intersection));

        //custom Operations

        PrefixOp<String> negate = new PrefixOp<>(negation, 1, 4);
        InfixOp<String> intersect = new InfixOp<>(intersection, 3);
        InfixOp<String> unite = new InfixOp<>(union, 2);
        InfixOp<String> imply = new InfixOp<>(implication, 1);
        InfixOp<String> equal = new InfixOp<>(equivalence, 0);

        Map<Symbol, OperatorToken<String>> operators = Stream.of(negate, equal, imply, unite, intersect)
                .collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<String> parser = new Parser<>(lexer, operators);

        Function<Literal<String>, String> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<String> v -> {
                    return v.getSymbol().symbol();
                }
                case ValueLiteral<String> v -> {
                    return v.getValue();
                }
            }
        };

        Context<String> context = new Context<>(Map.of(
                negation, l -> {
                    //simplifies !!a to a
                    String clause = fromLiteral.apply(l.head());
                    if (clause.startsWith("!")) {
                        return new ValueLiteral<>(clause.substring(1));
                    } else {
                        return new ValueLiteral<>("!" + clause);
                    }
                },
                equivalence, l -> {
                    //a <=> b -> (!a || b) && (!b || a)
                    String x = fromLiteral.apply(l.head());
                    String y = fromLiteral.apply(l.tail().head());

                    String lit1;
                    String lit2;

                    if (x.startsWith("!")) {
                        lit1 = "(%s || %s)".formatted(x.substring(1), y);
                    } else {
                        lit1 = "(!%s || %s)".formatted(x, y);
                    }

                    if (y.startsWith("!")) {
                        lit2 = "(%s || %s)".formatted(y.substring(1), x);
                    } else {
                        lit2 = "(!%s || %s)".formatted(y, x);
                    }

                    return new ValueLiteral<>("(%s && %s)".formatted(lit1, lit2));
                },
                implication, l -> {
                    //a => b -> (!a || b)
                    String x = fromLiteral.apply(l.head());
                    String y = fromLiteral.apply(l.tail().head());

                    if (x.startsWith("!")) {
                        return new ValueLiteral<>("(%s || %s)".formatted(x.substring(1), y));
                    } else {
                        return new ValueLiteral<>("(!%s || %s)".formatted(x, y));
                    }
                },
                union, l -> {
                    String x = fromLiteral.apply(l.head());
                    String y = fromLiteral.apply(l.tail().head());

                    return new ValueLiteral<>("(%s || %s)".formatted(x, y));
                },
                intersection, l -> {
                    String x = fromLiteral.apply(l.head());
                    String y = fromLiteral.apply(l.tail().head());

                    return new ValueLiteral<>("(%s && %s)".formatted(x, y));
                }
        ));

        Interpreter<String> interpreter = new Interpreter<>(parser, fromLiteral, context);
    }


    class Interpreter4 {
        //PROPAGATE NEGATION

        Symbol negation = new Symbol("!");
        Symbol union = new Symbol("||");
        Symbol intersection = new Symbol("&&");

        Lexer lexer = new Lexer(new MList<>(negation, union, intersection));

        //custom Operations

        PrefixOp<AstExpression<?>> negate = new PrefixOp<>(negation, 1, 2);
        InfixOp<AstExpression<?>> intersect = new InfixOp<>(intersection, 1);
        InfixOp<AstExpression<?>> unite = new InfixOp<>(union, 0);

        Map<Symbol, OperatorToken<AstExpression<?>>> operators = Stream.of(negate, unite, intersect)
                .collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<AstExpression<?>> parser = new Parser<>(lexer, operators);

        Function<Symbol, AstExpression<Symbol>> symbolToAst = ValueLiteral::new;

        Function<Literal<AstExpression<?>>, AstExpression<?>> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<AstExpression<?>> v -> {
                    return v;
                }
                case ValueLiteral<AstExpression<?>> v -> {
                    return v.getValue();
                }
            }
        };

        class Helper {
            AstExpression<?> neg(AstExpression<?> expr) {
                switch (expr) {
                    case ValueLiteral<?> v -> {
                        //negate the value by returning a negated expression - RECURSIVE
                        return neg((AstExpression<?>) v.getValue());
                    }
                    case SymbolLiteral<?> v -> {
                        //negate by returning new Expression with negation operator
                        return new Expression<>(negation, new MList<>(v));
                    }
                    case Expression<?> v -> {
                        if (v.getSymbol().equals(negation)) {
                            //remove negation by just returning the first argument of the negation
                            return v.getArgs().head();
                        } else if (v.getSymbol().equals(union)) {
                            //!(a || b) -> (!a && !b)
                            return new Expression<>(intersection, v.getArgs().map(this::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                        } else if (v.getSymbol().equals(intersection)) {
                            //!(a && b) -> (!a || !b)
                            return new Expression<>(union, v.getArgs().map(this::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                        }
                        throw new RuntimeException();
                    }
                }
            }

            ;
        }

        Context<AstExpression<?>> context = new Context<>(Map.of(
                negation, l -> {
                    AstExpression<?> clause = fromLiteral.apply(l.head());
                    return new ValueLiteral<>(new Helper().neg(clause));
                },
                union, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression<Object>) e);
                    return new ValueLiteral<>(new Expression<>(union, params));
                },
                intersection, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression<Object>) e);
                    return new ValueLiteral<>(new Expression<>(intersection, params));
                }
        ));

        Interpreter<AstExpression<?>> interpreter = new Interpreter<>(parser, fromLiteral, context);

        String asString(AstExpression<?> expr) {
         switch (expr) {
                case ValueLiteral<?> v -> {
                    //negate the value by returning a negated expression - RECURSIVE
                    return asString((AstExpression<?>) v.getValue());
                }
                case SymbolLiteral<?> v -> {
                    //negate by returning new Expression with negation operator
                    return v.getSymbol().symbol();
                }
                case Expression<?> v -> {
                    if (v.getSymbol().equals(negation)) {
                        //remove negation by just returning the first argument of the negation
                        return "!%s".formatted(v.getArgs().map(this::asString).head());
                    } else if (v.getSymbol().equals(union)) {
                        //!(a || b) -> (!a && !b)
                        var args = v.getArgs().map(this::asString);
                        return "(%s || %s)".formatted(args.head(), args.tail().head());
                    } else if (v.getSymbol().equals(intersection)) {
                        //!(a || b) -> (!a && !b)
                        var args = v.getArgs().map(this::asString);
                        return "%s && %s".formatted(args.head(), args.tail().head());
                    }
                    throw new RuntimeException();
                }
            }
        }
    }

    class Interpreter5 {
        //COMBINATION OF Interpreter3 and INterpreter4

        Symbol negation = new Symbol("!");
        Symbol implication = new Symbol("=>");
        Symbol equivalence = new Symbol("<>");
        Symbol union = new Symbol("||");
        Symbol intersection = new Symbol("&&");

        Lexer lexer = new Lexer(new MList<>(negation, implication, equivalence, union, intersection));

        //custom Operations

        PrefixOp<AstExpression<?>> negate = new PrefixOp<>(negation, 1, 4);
        InfixOp<AstExpression<?>> intersect = new InfixOp<>(intersection, 3);
        InfixOp<AstExpression<?>> unite = new InfixOp<>(union, 2);
        InfixOp<AstExpression<?>> imply = new InfixOp<>(implication, 1);
        InfixOp<AstExpression<?>> equal = new InfixOp<>(equivalence, 0);

        Map<Symbol, OperatorToken<AstExpression<?>>> operators = Stream.of(negate, equal, imply, unite, intersect)
                .collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<AstExpression<?>> parser = new Parser<>(lexer, operators);

        Function<Symbol, AstExpression<Symbol>> symbolToAst = ValueLiteral::new;

        Function<Literal<AstExpression<?>>, AstExpression<?>> fromLiteral = l -> {
            switch (l) {
                case SymbolLiteral<AstExpression<?>> v -> {
                    return v;
                }
                case ValueLiteral<AstExpression<?>> v -> {
                    return v.getValue();
                }
            }
        };

        class Helper {
            AstExpression<?> neg(AstExpression<?> expr) {
                switch (expr) {
                    case ValueLiteral<?> v -> {
                        //negate the value by returning a negated expression - RECURSIVE
                        return neg((AstExpression<?>) v.getValue());
                    }
                    case SymbolLiteral<?> v -> {
                        //negate by returning new Expression with negation operator
                        return new Expression<>(negation, new MList<>(v));
                    }
                    case Expression<?> v -> {
                        if (v.getSymbol().equals(negation)) {
                            //remove negation by just returning the first argument of the negation
                            return v.getArgs().head();
                        } else if (v.getSymbol().equals(union)) {
                            //!(a || b) -> (!a && !b)
                            return new Expression<>(intersection, v.getArgs().map(this::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                        } else if (v.getSymbol().equals(intersection)) {
                            //!(a && b) -> (!a || !b)
                            return new Expression<>(union, v.getArgs().map(this::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                        }
                        throw new RuntimeException();
                    }
                }
            }

            ;
        }

        Context<AstExpression<?>> context = new Context<>(Map.of(
                negation, l -> {
                    AstExpression<?> clause = fromLiteral.apply(l.head());
                    return new ValueLiteral<>(new Helper().neg(clause));
                },
                union, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression<Object>) e);
                    return new ValueLiteral<>(new Expression<>(union, params));
                },
                intersection, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression<Object>) e);
                    return new ValueLiteral<>(new Expression<>(intersection, params));
                },
                implication, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression<Object>) e);
                    var x = params.head();
                    var y = params.tail().head();

                    var xNeg = new Expression<>(negation, new MList<>(x));

                    var impl = new Expression<>(union, new MList<>(xNeg, y));

                    return new ValueLiteral<>(impl);
                },
                equivalence, l -> {
                    var params = l.map(fromLiteral).map(e -> (AstExpression) e).map(ValueLiteral::new);
                    var x = params.head();
                    var y = params.tail().head();

                    var xNeg = new Expression<>(negation, new MList<>(x));
                    var yNeg = new Expression<>(negation, new MList<>(y));

                    var impl1 = new Expression<>(union, new MList<>(xNeg, y));
                    var impl2 = new Expression<>(union, new MList<>(yNeg, x));

                    var eq = new Expression<>(intersection, new MList<>(impl1, impl2));
                    return new ValueLiteral<>(eq);
                }
        ));

        Interpreter<AstExpression<?>> interpreter = new Interpreter<>(parser, fromLiteral, context);

        String asString(AstExpression<?> expr) {
         switch (expr) {
                case ValueLiteral<?> v -> {
                    //negate the value by returning a negated expression - RECURSIVE
                    return asString((AstExpression<?>) v.getValue());
                }
                case SymbolLiteral<?> v -> {
                    //negate by returning new Expression with negation operator
                    return v.getSymbol().symbol();
                }
                case Expression<?> v -> {
                    if (v.getSymbol().equals(negation)) {
                        //remove negation by just returning the first argument of the negation
                        return "!%s".formatted(v.getArgs().map(this::asString).head());
                    } else if (v.getSymbol().equals(union)) {
                        //!(a || b) -> (!a && !b)
                        var args = v.getArgs().map(this::asString);
                        return "(%s || %s)".formatted(args.head(), args.tail().head());
                    } else if (v.getSymbol().equals(intersection)) {
                        //!(a || b) -> (!a && !b)
                        var args = v.getArgs().map(this::asString);
                        return "%s && %s".formatted(args.head(), args.tail().head());
                    }
                    throw new RuntimeException();
                }
            }
        }
    }

    @Test
    void customLangTest3() {


        var res = new Interpreter3().interpreter.run("(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))");

        System.err.println(res);

    }

    @Test
    void customLangTest4() {
        //PROPAGATE NEGATIONS

        var i3 = new Interpreter3();
        var i4 = new Interpreter4();

        var res = i4.interpreter.run("((!x1 && !((!x3 || x2) && (!x2 || x3))) || (((!x3 || !x4) && (!x1 || (x2 || !x3))) && x4))");
        System.err.println(i4.asString(res));
    }

    @Test
    void customLangTest5(){
        var i3 = new Interpreter3();
        var i4 = new Interpreter4();
        var i5 = new Interpreter5();
        var i3res = i3.interpreter.run("(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))");
        var i4res = i4.interpreter.run(i3res);
        var i4str = i4.asString(i4res);
        var i3res2 = i3.interpreter.run(i4str);
        var ast5 = i5.interpreter.run("(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))");
        System.err.println("### (!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))");
        System.err.println("### " + i3res);
        System.err.println("### " + i4str);
        System.err.println("### " + i5.asString(ast5));
        System.err.println("### " + i3res2);
    }

    @Test
    void asasd(){
        var i5 = new Interpreter5();
        var ast5 = i5.interpreter.run("(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))");
        System.err.println(i5.asString(ast5));
    }
}