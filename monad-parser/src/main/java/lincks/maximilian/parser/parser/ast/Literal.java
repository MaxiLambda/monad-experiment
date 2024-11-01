package lincks.maximilian.parser.parser.ast;

public sealed interface Literal<T> extends AstExpression<T> permits SymbolLiteral, ValueLiteral {

    default Literal<T> eval(Context<T> context) {
        return this;
    }
}
