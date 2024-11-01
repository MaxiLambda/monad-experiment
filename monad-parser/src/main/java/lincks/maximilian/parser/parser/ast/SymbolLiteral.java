package lincks.maximilian.parser.parser.ast;

import lincks.maximilian.parser.token.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class SymbolLiteral<T> implements Literal<T> {

    //contains the string representation of the value
    private final Symbol symbol;
}
