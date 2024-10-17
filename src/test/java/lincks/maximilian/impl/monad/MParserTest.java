package lincks.maximilian.impl.monad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MParserTest {
    Function<Character, MParser<Character, Character>> digit = MParser::tokenMatching;
    Function<String, MParser<Character, String>> string = (str) ->
            str.chars()
                    .mapToObj(c -> (char) c)
                    .map(digit)
                    .reduce(
                            new MParser<>(""),
                            (acc, chrParser) -> acc.then((strR, chrR) -> strR + chrR, chrParser),
                            (l, r) -> l.then(String::concat, r));


    List<Character> chars = "hallo".chars().mapToObj(i -> (char) i).toList();
    List<Character> chars2 = "hhhoh".chars().mapToObj(i -> (char) i).toList();
    List<Character> chars3 = "hoh".chars().mapToObj(i -> (char) i).toList();

    //many does not work atm. therefore this test would be useless
//    @Test
//    void many() {
//        var p = unwrap(digit.apply('h').many()).parse(chars2);
//        System.out.println(p);
//    }

    @Test
    void parseSingleToken() {
        var results = digit.apply('h').parse(chars3);
        assertEquals(results.size(), 1);

        var result = results.getFirst();
        assertEquals(List.of('o', 'h'), result.remainingTokens());
        assertEquals('h', result.value());
    }

    @Test
    void parseMultipleTokens() {
        var results = digit.apply('h')
                .then(
                        List::of,
                        digit.apply('o'))
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of('h'), result.remainingTokens());
        assertEquals(List.of('h', 'o'), result.value());
        System.out.println(result);
    }

    @Test
    void parseManyTimes() {
        var results = digit.apply('h').many2().parse(chars2);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of('o', 'h'), result.remainingTokens());
        assertEquals(List.of('h', 'h', 'h'), result.value());
    }

    @Test
    void parsePlusMany() {
        var results = digit.apply('h')
                .plus(digit.apply('o'))
                .many2()
                .parse(chars3);
        assertEquals(1, results.size());

        var result = results.getFirst();
        assertEquals(List.of(), result.remainingTokens());
        assertEquals(List.of('h', 'o', 'h'), result.value());
    }

    @Test
    void failParse() {
        var results = digit.apply('i').parse(chars3);
        assertEquals(0, results.size());
    }

    @Test
    void failParseEmpty() {
        var results = digit.apply('i').parse(List.of());
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
}