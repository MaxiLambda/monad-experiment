package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static lincks.maximilian.impl.monad.MList.toMList;
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

    MList<Character> chars = "hallo".chars().mapToObj(i -> (char) i).collect(toMList());
    MList<Character> chars2 = "hhhoh".chars().mapToObj(i -> (char) i).collect(toMList());
    MList<Character> chars3 = "hoh".chars().mapToObj(i -> (char) i).collect(toMList());

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


        var result = results.head();
        assertEquals(new MList<>('o', 'h'), result.remainingTokens());
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


        var res = addParser.parse("14+7".chars().mapToObj(i -> (char) i).collect(toMList()));
        assertEquals(21, res.head().value());
    }

    @Test
    void parseMultipleTokens() {
        var results = exact.apply('h')
                .then(
                        MList::new,
                        exact.apply('o'))
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.head();
        assertEquals(new MList<>('h'), result.remainingTokens());
        assertEquals(new MList<>('h', 'o'), result.value());
        System.out.println(result);
    }

    @Test
    void parseManyTimes() {
        var results = exact.apply('h').many2().parse(chars2);
        assertEquals(1, results.size());

        var result = results.head();
        assertEquals(new MList<>('o', 'h'), result.remainingTokens());
        assertEquals(new MList<>('h', 'h', 'h'), result.value());
    }

    @Test
    void parsePlusMany() {
        var results = exact.apply('h')
                .plus(exact.apply('o'))
                .many2()
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.head();
        assertEquals(new MList<>(), result.remainingTokens());
        assertEquals(new MList<>('h', 'o', 'h'), result.value());
    }

    @Test
    void failParse() {
        var results = exact.apply('i').parse(chars3);
        assertEquals(0, results.size());
    }

    @Test
    void failParseEmpty() {
        var results = exact.apply('i').parse(new MList<>());
        assertEquals(0, results.size());
    }

    @Test
    void stringParser() {
        var results = string.apply("hallo").parse(chars);
        assertEquals(1, results.size());

        var result = results.head();
        assertEquals(new MList<>(), result.remainingTokens());
        assertEquals("hallo", result.value());
    }

    @Test
    void nWay() {
        var x = MParser.tokenMatching('a').map(MList::new).plus(MParser.tokenMatching('a').many2()).parse(new MList<>('a', 'a'));
        assertEquals(2, x.size());
    }

    @Test
    void someTest() {
        var r1 = MParser.tokenMatching("a").some2().then((a, b) -> a.mplus(new MList<>(b)), tokenMatching("b")).parse(new MList<>("b"));
        var r2 = MParser.tokenMatching("a").some2().then((a, b) -> a.mplus(new MList<>(b)), tokenMatching("b")).parse(new MList<>("a", "a", "b"));

        assertEquals(new MList<>(), r1.head().remainingTokens());
        assertEquals(new MList<>(), r2.head().remainingTokens());

    }

    @Test
    void empty() {
        var x = MParser.empty().then( (ignore, a) -> a,tokenMatching("a")).parse(new MList<>("a"));
        System.out.println(x);
    }
}