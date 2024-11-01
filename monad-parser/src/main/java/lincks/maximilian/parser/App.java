package lincks.maximilian.parser;

import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
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

        Symbol symbol1 = new Symbol("!");
        Symbol symbol2 = new Symbol("@");
        Symbol symbol3 = new Symbol("%");
        Symbol symbol4 = new Symbol("?");
        Symbol symbol5 = new Symbol("+");
        Symbol symbol6 = new Symbol("*");

        Lexer lexer = new Lexer(List.of(symbol1, symbol2, symbol3, symbol4, symbol5, symbol6));

        //custom Operations
        PrefixOp<Integer> operator1 = new PrefixOp<>(symbol1, 1, 0);
        PrefixOp<Integer> operator2 = new PrefixOp<>(symbol2, 1, 1);
        PrefixOp<Integer> operator3 = new PrefixOp<>(symbol3, 2, 0);
        PrefixOp<Integer> operator4 = new PrefixOp<>(symbol4, 2, 1);
        InfixOp<Integer> operator5 = new InfixOp<>(symbol5, 0);
        InfixOp<Integer> operator6 = new InfixOp<>(symbol6, 1);

        Map<Symbol, OperatorToken<Integer>> operators = Stream.of(operator1, operator2, operator3, operator4, operator5, operator6).collect(toMap(OperatorToken::getSymbol, Function.identity()));

        Parser<Integer> parser = new Parser<>(lexer, operators, (l) -> Integer.valueOf(l.getSymbol().toString()));

        System.out.println(parser.run("(!1;)+2;*%3;@4;"));
    }
}
