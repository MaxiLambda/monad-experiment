package lincks.maximilian.parser.parser.ast;

public interface AstExpression<T> {
    Literal<T> eval(Context<T> context);
}
