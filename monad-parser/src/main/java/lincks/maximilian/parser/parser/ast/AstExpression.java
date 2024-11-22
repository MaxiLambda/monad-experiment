package lincks.maximilian.parser.parser.ast;

public sealed interface AstExpression<T> permits Expression, Literal {
    Literal<T> eval(Context<T> context);
}
