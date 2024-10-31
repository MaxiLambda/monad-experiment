package lincks.maximilian.parser;

import lincks.maximilian.parser.token.Symbol;
import lincks.maximilian.parser.token.Token;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
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

        //custom tokens
        PrefixOp<Integer> operator1 = new PrefixOp<>(new Symbol("!"), 1, 0);
        PrefixOp<Integer> operator2 = new PrefixOp<>(new Symbol("@"), 1, 1);
        PrefixOp<Integer> operator3 = new PrefixOp<>(new Symbol("%"), 2, 0);
        PrefixOp<Integer> operator4 = new PrefixOp<>(new Symbol("?"), 2, 1);
        InfixOp<Integer> operator5 = new InfixOp<>(new Symbol("+"), 0);
        InfixOp<Integer> operator6 = new InfixOp<>(new Symbol("*"),1);

        List<Token> operations = List.of(operator1, operator2, operator3, operator4, operator5, operator6);
    }
}
