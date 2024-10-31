package lincks.maximilian.parser;

import lincks.maximilian.parser.token.Symbol;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static lincks.maximilian.impl.monad.MList.toMList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LexerTest {

    @Test
    void run() {
        Lexer lexer = new Lexer(Stream.of("*", "+", "->", "-").map(Symbol::new).toList());

        String input = "->)2;(3;*4;-->4;";
        var res = lexer.getSymbols(input);
        assertEquals(List.of(Stream.of(
                "->", ")", "2", "(", "3", "*", "4", "-", "->", "4"
        ).map(Symbol::new).collect(toMList())), res);
    }
}