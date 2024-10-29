package lincks.maximilian.parser;

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
        Token token1 = new Token("!");
        Token token2 = new Token("@");
        Token token3 = new Token("%");
        Token token4 = new Token("?");
        Token token5 = new Token("+");
        Token token6 = new Token("*");

        List<Token> operations = List.of(token1, token2, token3, token4, token5, token6);

        List<PrefixOp> prefixOps = List.of(
                new PrefixOp(0, 1, token1),
                new PrefixOp(1, 1, token2),
                new PrefixOp(0, 2, token3),
                new PrefixOp(1, 2, token4));

        List<InfixOp> infixOps = List.of(
                new InfixOp(0, token5),
                new InfixOp(1, token6));
    }
}
