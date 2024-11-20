package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.MParser;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;
import static lincks.maximilian.impl.monad.MList.toMList;
import static lincks.maximilian.parser.token.SyntaxToken.L_BRACE;
import static lincks.maximilian.parser.token.SyntaxToken.R_BRACE;

@RequiredArgsConstructor
public class Lexer {
    private final static MList<Symbol> braceSymbols = new MList<>(L_BRACE.getSymbol(), R_BRACE.getSymbol());
    private final static Predicate<Character> spacyChar = c -> Pattern.matches("\\s|\\n|\\r", Pattern.quote(c + ""));
    private final MList<Symbol> operatorSymbols;


    public MList<Symbol> getSymbols(String input) {

        String cleanedInput = input.replaceAll("(^[\\s\\n\\r]*)|([\\s\\n\\r]*$)", "");



        MList<MParser<Character, Symbol>> operatorParsers = fromSymbols(operatorSymbols);
        MList<MParser<Character, Symbol>> braceParsers = fromSymbols(braceSymbols);
        MParser<Character, Symbol> literalParser =
                MParser.matching(not(spacyChar))
                        .many3()
                        .consumeAll(spacyChar)
                        .map(list -> list.foldr((val, acc) -> val + acc, ""))
                        .map(Symbol::new);

        MParser<Character, MList<Symbol>> symbolParser =
                braceParsers
                        .mplus(operatorParsers)
                        .mplus(new MList<>(literalParser))
                        .foldr(MParser::either, MParser.<Character, Symbol>empty())
                        .many2();

        return symbolParser.parse(cleanedInput.chars().mapToObj(c -> (char) c).collect(toMList()))
                .filter(r -> r.remainingTokens().isEmpty())
                .map(MParser.ParseResult::value)
                .filter(syms -> syms.all(sym -> reservedSymbols().any(sym::equals) || !reservedSymbols().any(op -> sym.symbol().contains(op.symbol()))))
                .head();
    }

    private MList<MParser<Character, Symbol>> fromSymbols(MList<Symbol> symbols) {
        return symbols
                .map(s -> MParser.accumulating(
                                        s.symbol()
                                                .chars()
                                                .mapToObj(c -> (char) c)
                                                .map(MParser::tokenMatching)
                                                .collect(toMList())

                                ).map(list -> list.foldr((val, acc) -> acc + val, ""))
                                .map(Symbol::new)
                                .maybe((sym, ignore) -> sym, MParser.matching(spacyChar).many2())
                );
    }

    private MList<Symbol> reservedSymbols() {
        return operatorSymbols.mplus(new MList<>("(", ")").map(Symbol::new));
    }
}
