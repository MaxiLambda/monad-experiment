package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static lincks.maximilian.impl.monad.MParser.tokenMatching;
import static lincks.maximilian.impl.monad.MParser.unwrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MParserTest {
    Function<Character, MParser<Character, Character>> exact = MParser::tokenMatching;
    Function<String, MParser<Character, String>> string = (str) ->
            str.chars()
                    .mapToObj(c -> (char) c)
                    .map(exact)
                    .reduce(
                            new MParser<>(""),
                            (acc, chrParser) -> acc.then((strR, chrR) -> strR + chrR, chrParser),
                            (l, r) -> l.then(String::concat, r));

    List<Character> chars = "hallo".chars().mapToObj(i -> (char) i).toList();
    List<Character> chars2 = "hhhoh".chars().mapToObj(i -> (char) i).toList();
    List<Character> chars3 = "hoh".chars().mapToObj(i -> (char) i).toList();

    //many does not work atm. therefore use many2
    @Test
    void many() {
        var p = unwrap(exact.apply('h').many2()).parse(chars2);
        System.out.println(p);
    }

    @Test
    void parseSingleToken() {
        var results = exact.apply('h').parse(chars3);
        assertEquals(results.size(), 1);
        System.out.println(results);


        var result = results.getFirst();
        assertEquals(List.of('o', 'h'), result.remainingTokens());
        assertEquals('h', result.value());
    }

    @Test
    void addParser() {
        //can parse all positive integers
        MParser<Character, Integer> numParser = "1234567890".chars()
                .mapToObj(i -> (char) i)
                .map(exact)
                .reduce(MParser.empty(), MParser::either)
                .many2()
                .map(chars -> chars.foldr((val, acc) -> val + acc, ""))
                .map(Integer::valueOf);

        MParser<Character, Integer> addParser = numParser.then((i, op) -> i, MParser.tokenMatching('+')).then(Integer::sum, numParser);


        var res = addParser.parse("14+7".chars().mapToObj(i -> (char) i).toList());
        assertEquals(21, res.getFirst().value());
    }

    @Test
    void parseMultipleTokens() {
        var results = exact.apply('h')
                .then(
                        List::of,
                        exact.apply('o'))
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of('h'), result.remainingTokens());
        assertEquals(List.of('h', 'o'), result.value());
        System.out.println(result);
    }

    @Test
    void parseManyTimes() {
        var results = exact.apply('h').many2().parse(chars2);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of('o', 'h'), result.remainingTokens());
        assertEquals(List.of('h', 'h', 'h'), result.value());
    }

    @Test
    void parsePlusMany() {
        var results = exact.apply('h')
                .plus(exact.apply('o'))
                .many2()
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of(), result.remainingTokens());
        assertEquals(List.of('h', 'o', 'h'), result.value());
    }

    @Test
    void failParse() {
        var results = exact.apply('i').parse(chars3);
        assertEquals(0, results.size());
    }

    @Test
    void failParseEmpty() {
        var results = exact.apply('i').parse(List.of());
        assertEquals(0, results.size());
    }

    @Test
    void stringParser() {
        var results = string.apply("hallo").parse(chars);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of(), result.remainingTokens());
        assertEquals("hallo", result.value());
    }

    @Test
    void nWay() {
        var x = MParser.tokenMatching('a').map(MList::new).plus(MParser.tokenMatching('a').many2()).parse(List.of('a', 'a'));
        assertEquals(2, x.size());
    }

    @Test
    void someTest() {
        var r1 = MParser.tokenMatching("a").some2().then((a, b) -> a.mplus(new MList<>(b)), tokenMatching("b")).parse(List.of("b"));
        var r2 = MParser.tokenMatching("a").some2().then((a, b) -> a.mplus(new MList<>(b)), tokenMatching("b")).parse(List.of("a", "a", "b"));

        assertEquals(List.of(), r1.getFirst().remainingTokens());
        assertEquals(List.of(), r2.getFirst().remainingTokens());

    }

    @Test
    void empty() {
        var x = MParser.empty().then( (ignore, a) -> a,tokenMatching("a")).parse(List.of("a"));
        System.out.println(x);
    }
}