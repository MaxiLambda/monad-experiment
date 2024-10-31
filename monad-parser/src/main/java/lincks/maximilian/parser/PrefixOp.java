package lincks.maximilian.parser;

import lincks.maximilian.parser.token.LiteralToken;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;

import java.util.List;
import java.util.function.Function;

public class PrefixOp<T> extends OperatorToken<T> {
    public PrefixOp(Symbol symbol, int arity, int level) {
        super(symbol, arity, level);
    }
}
