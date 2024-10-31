package lincks.maximilian.parser.token;

public abstract class SyntaxToken implements Token {

    @Override
    public TokenType getType() {
        return TokenType.SYNTAX;
    }

    public static final SyntaxToken L_BRACE = new SyntaxToken(){
        @Override
        public Symbol getSymbol() {
            return new Symbol("(");
        }
    };

    public static final SyntaxToken R_BRACE = new SyntaxToken(){
        @Override
        public Symbol getSymbol() {
            return new Symbol(")");
        }
    };

    public static final SyntaxToken LITERAL_DELIMITER = new SyntaxToken(){
        @Override
        public Symbol getSymbol() {
            return new Symbol(";");
        }
    };
}
