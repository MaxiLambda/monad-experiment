package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.MParser;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.function.Predicate.not;
import static lincks.maximilian.impl.monad.MList.toMList;
import static lincks.maximilian.parser.token.SyntaxToken.L_BRACE;
import static lincks.maximilian.parser.token.SyntaxToken.R_BRACE;

@RequiredArgsConstructor
public class Lexer {
    private final List<Symbol> operatorSymbols;
    private final static List<Symbol> braceSymbols = List.of(L_BRACE.getSymbol(), R_BRACE.getSymbol());


    public List<MList<Symbol>> getSymbols(String input) {

        String cleanedInput = input.replaceAll("\\s", "");

        MList<MParser<Character, Symbol>> operatorParsers = fromSymbols(operatorSymbols);
        MList<MParser<Character, Symbol>> braceParsers = fromSymbols(braceSymbols);
        MParser<Character, Symbol> literalParser = MParser.<Character>matching(not((c) -> c.equals(';')))
                .many2()
                .consume((c) -> c.equals(';'))
                .map(list -> list.foldr((val, acc) -> val + acc, ""))
                .map(Symbol::new);

        MParser<Character, MList<Symbol>> symbolParser =
                braceParsers
                        .mplus(operatorParsers)
                        .mplus(new MList<>(literalParser))
                        .foldr(MParser::either, MParser.<Character, Symbol>empty())
                        .many2();

        return symbolParser.parse(cleanedInput.chars().mapToObj(c -> (char) c).toList())
                .stream()
                .filter(r -> r.remainingTokens().isEmpty())
                .map(MParser.ParseResult::value)
                .toList();
    }

    private MList<MParser<Character, Symbol>> fromSymbols(List<Symbol> symbols) {
        return symbols.stream()
                .map(s -> MParser.accumulating(
                                s.symbol()
                                        .chars()
                                        .mapToObj(c -> (char) c)
                                        .map(MParser::tokenMatching)
                                        .collect(toMList())

                        ).map(list -> list.foldr((val, acc) -> acc + val, ""))
                        .map(Symbol::new))
                .collect(toMList());
    }

}
