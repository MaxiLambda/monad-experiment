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
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(7,interpreter.run("(!1;)+2;*(%3;@4;)"));

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
        assertEquals(-1,interpreter.run("1;-1;-1;"));
    }


    @Test
    void customLangTest() {

        record Scope(Map<Symbol, Integer> scope, Either<Symbol, Integer> value){}

        Symbol concat = new Symbol(",");
        Symbol storeVal = new Symbol("@");
        Symbol getVal = new Symbol("!");

        var xxx = new MList<>(concat, storeVal, getVal);
        System.out.println(xxx);
        Lexer lexer = new Lexer(xxx);

        //custom Operations
        InfixOp<Scope> operator1 = new InfixOp<>(concat, 0);
        PrefixOp<Scope> operator2 = new PrefixOp<>(storeVal, 2, 1);
        PrefixOp<Scope> operator3 = new PrefixOp<>(getVal, 1, 2);

        Map<Symbol, OperatorToken<Scope>> operators = Stream.of(operator1,operator2,operator3)
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

                    Either<Symbol, Integer> value = switch (x.value){
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
//                ,
//                getVal, l -> {
//                    var value = fromLiteral.apply(l.head());
//                    Symbol symbol = value.value.asLeft().value();
//                    var resolvedVal = value.scope.get(symbol);
//                    return new ValueLiteral<>(new Scope(value.scope, new Either.Right<>(resolvedVal)));
//                }
        ));
        Interpreter<Scope> interpreter = new Interpreter<>(parser, fromLiteral, context);
        //TODO for addition, ',' has to bind stronger than '+'
        System.out.println(interpreter.run("@a;1;@b;2;,a;"));
        //                                        ---
        //                                        scope
    }
}