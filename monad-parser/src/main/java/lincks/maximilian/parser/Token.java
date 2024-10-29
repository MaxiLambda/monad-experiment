package lincks.maximilian.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Token {

    //TODO make Token an Interface or abstract, make PrefixOp, InfixOp, the new INT-Token, L_BRACE, R_BRACE extend/implement it.
    // add common interface for PrefixOp and InfixOp with "action" or similar
    public static final Token L_BRACE = new Token("(");
    public static final Token R_BRACE = new Token(")");
    //TODO replace with class extending token
    public static final Token INT = new Token("int");

    private final String symbol;
}
