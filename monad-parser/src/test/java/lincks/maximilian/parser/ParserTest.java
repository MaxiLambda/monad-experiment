package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.example.LexerImpl;
import lincks.maximilian.parser.example.ParserImpl;
import lincks.maximilian.parser.parser.ast.Expression;
import lincks.maximilian.parser.parser.ast.SymbolLiteral;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    Symbol symbol1 = new Symbol("!");
    Symbol symbol2 = new Symbol("@");
    Symbol symbol3 = new Symbol("%");
    Symbol symbol4 = new Symbol("?");
    Symbol symbol5 = new Symbol("+");
    Symbol symbol6 = new Symbol("*");

    LexerImpl lexer = new LexerImpl(new MList<>(symbol1, symbol2, symbol3, symbol4, symbol5, symbol6));

    //custom Operations
    PrefixOp<Integer> operator1 = new PrefixOp<>(symbol1, 1, 0);
    PrefixOp<Integer> operator2 = new PrefixOp<>(symbol2, 1, 1);
    PrefixOp<Integer> operator3 = new PrefixOp<>(symbol3, 2, 0);
    PrefixOp<Integer> operator4 = new PrefixOp<>(symbol4, 2, 1);
    InfixOp<Integer> operator5 = new InfixOp<>(symbol5, 0);
    InfixOp<Integer> operator6 = new InfixOp<>(symbol6, 1);

    Map<Symbol, OperatorToken<Integer>> operators = Stream.of(operator1, operator2, operator3, operator4, operator5, operator6).collect(toMap(OperatorToken::getSymbol, Function.identity()));

    ParserImpl<Integer> parser = new ParserImpl<>(operators);

    @Test
    void infixTest() {
        var x = parser.run(lexer,"1+2");
        assertEquals(new Expression<>(symbol5, new MList<>(
                new SymbolLiteral<>(new Symbol("1")),
                new SymbolLiteral<>(new Symbol("2")))), x);
    }

    @Test
    void bracesTest() {
        var x = parser.run(lexer,"(!1)+2");
        assertEquals(new Expression<>(symbol5, new MList<>(
                new Expression<>(symbol1, new MList<>(new SymbolLiteral<>(new Symbol("1")))),
                new SymbolLiteral<>(new Symbol("2")))), x);

        var xx = parser.run(lexer,"!1*2");
        assertEquals(new Expression<>(symbol1, new MList<>(
                new Expression<>(symbol6, new MList<>(
                        new SymbolLiteral<>(new Symbol("1")),
                        new SymbolLiteral<>(new Symbol("2"))))
        )), xx);
    }

    @Test
    void singleValueTest() {
        var x = parser.run(lexer,"1");
        var xx = parser.run(lexer,"(1)");
        assertEquals(new SymbolLiteral<>(new Symbol("1")),x);
        assertEquals(new SymbolLiteral<>(new Symbol("1")),xx);
    }

    @Test
    void binaryPrefixTest() {
        var x = parser.run(lexer,"? 1 2");
        assertEquals(new Expression<>(symbol4, new MList<>(
                new SymbolLiteral<>(new Symbol("1")),
                new SymbolLiteral<>(new Symbol("2")))), x);
    }

    @Test
    void complicatedTest() {
        var x = parser.run(lexer,"(!1)+2*(%3@4)");
        assertEquals(new Expression<>(symbol5, new MList<>(
                new Expression<>(symbol1, new MList<>(new SymbolLiteral<>(new Symbol("1")))),
                new Expression<>(symbol6, new MList<>(
                        new SymbolLiteral<>(new Symbol("2")),
                        new Expression<>(symbol3, new MList<>(
                                new SymbolLiteral<>(new Symbol("3")),
                                new Expression<>(symbol2, new MList<>(
                                        new SymbolLiteral<>(new Symbol("4"))
                                ))
                        ))
                )))),x);
    }

}