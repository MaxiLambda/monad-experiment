package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.token.Symbol;

public interface Lexer<T> {
    MList<Symbol> getSymbols(T input);
}
