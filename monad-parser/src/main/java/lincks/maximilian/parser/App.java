package lincks.maximilian.parser;

import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.parser.ast.Context;
import lincks.maximilian.parser.parser.ast.Literal;
import lincks.maximilian.parser.parser.ast.SymbolLiteral;
import lincks.maximilian.parser.parser.ast.ValueLiteral;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
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

        Lexer lexer = new Lexer(List.of(exclamationS, atS, percentS, questionS, plusS, starS));

        //custom Operations
        PrefixOp<Integer> operator1 = new PrefixOp<>(exclamationS, 1, 0);
        PrefixOp<Integer> operator2 = new PrefixOp<>(atS, 1, 1);
        PrefixOp<Integer> operator3 = new PrefixOp<>(percentS, 2, 0);
        PrefixOp<Integer> operator4 = new PrefixOp<>(questionS, 2, 1);
        InfixOp<Integer> operator5 = new InfixOp<>(plusS, 0);
        InfixOp<Integer> operator6 = new InfixOp<>(starS, 1);

        Map<Symbol, OperatorToken<Integer>> operators = Stream.of(operator1, operator2, operator3, operator4, operator5, operator6).collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Integer> parser = new Parser<>(lexer, operators, (l) -> Integer.valueOf(l.getSymbol().toString()));
        //"(!1;)+2;*%3;@4;"

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
                    return new ValueLiteral<>(x+y);
                },
                starS, l -> {
                    var xs = l.map(fromLiteral);
                    var x = xs.head();
                    var y = xs.tail().head();
                    return new ValueLiteral<>(x*y);
                }
        ));

        Interpreter<Integer> interpreter = new Interpreter<>(parser, fromLiteral, context);

        System.out.println(interpreter.run("(!1;)+2;*(%3;@4;)"));
    }
}
