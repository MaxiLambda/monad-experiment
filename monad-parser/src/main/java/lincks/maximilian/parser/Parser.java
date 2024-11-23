package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.token.Symbol;

public interface Parser<T> {
    <I> AstExpression<T> run(Lexer<I> lexer,I input);
    AstExpression<T> run(MList<Symbol> symbols);
}
