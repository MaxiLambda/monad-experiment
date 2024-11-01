package lincks.maximilian.parser.parser.ast;

public interface Literal<T> extends AstExpression<T> {

    default Literal<T> eval(Context<T> context) {
        return this;
    }
}
