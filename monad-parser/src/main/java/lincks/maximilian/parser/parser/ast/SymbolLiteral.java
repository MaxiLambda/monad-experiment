package lincks.maximilian.parser.parser.ast;

import lincks.maximilian.parser.token.Symbol;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class SymbolLiteral<T> implements Literal<T> {

    //contains the string representation of the value
    private final Symbol symbol;
}
