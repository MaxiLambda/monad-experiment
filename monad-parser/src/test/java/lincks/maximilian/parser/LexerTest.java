package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.token.Symbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {

    @Test
    void run() {
        Lexer lexer = new Lexer(new MList<>("*", "+", "->", "-").map(Symbol::new));

        String input = "->)2;(3;*4;-->4;";
        var res = lexer.getSymbols(input);

        assertEquals(new MList<>(
                "->", ")", "2", "(", "3", "*", "4", "-", "->", "4"
        ).map(Symbol::new), res);
    }
}