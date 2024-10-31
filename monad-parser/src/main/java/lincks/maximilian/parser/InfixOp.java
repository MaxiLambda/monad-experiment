package lincks.maximilian.parser;

import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;

public class InfixOp<T> extends OperatorToken<T> {
    public InfixOp(Symbol symbol, int level) {
        super(symbol, 2, level);
    }
}
