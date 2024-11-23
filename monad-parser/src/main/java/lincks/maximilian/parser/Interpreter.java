package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.parser.ast.Context;
import lincks.maximilian.parser.parser.ast.Literal;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class Interpreter<T> {

    private final Function<Literal<T>, T> fromLiteral;
    private final Context<T> context;

    public <I> T run(Lexer<I> lexer, Parser<T> parser, I input) {
        MList<Symbol> symbols = lexer.getSymbols(input);

        return run(parser, symbols);
    }

    public T run(Parser<T> parser, MList<Symbol> input) {
        AstExpression<T> ast = parser.run(input);

        return run(ast);
    }

    public T run(AstExpression<T> ast) {
        Literal<T> result = ast.eval(context);
        return fromLiteral.apply(result);
    }

}
