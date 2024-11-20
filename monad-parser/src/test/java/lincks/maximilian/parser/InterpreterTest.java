package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.Effect;
import lincks.maximilian.impl.monad.Either;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.parser.ast.Context;
import lincks.maximilian.parser.parser.ast.Literal;
import lincks.maximilian.parser.parser.ast.SymbolLiteral;
import lincks.maximilian.parser.parser.ast.ValueLiteral;
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


}