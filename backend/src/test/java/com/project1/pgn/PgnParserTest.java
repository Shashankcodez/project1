package com.project1.pgn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PgnParserTest {

    @Test
    @DisplayName("Extracts moves from standard PGN with headers and game result")
    void testStandardPgn() {
        String pgn = """
                [Event "World Championship"]
                [Site "London"]
                [Date "2018.11.09"]
                [White "Carlsen, Magnus"]
                [Black "Caruana, Fabiano"]
                [Result "1/2-1/2"]

                1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 1/2-1/2
                """;

        List<String> moves = PgnParser.extractMoves(pgn);
        assertEquals(8, moves.size());
        assertEquals(List.of("e4", "e5", "Nf3", "Nc6", "Bb5", "a6", "Ba4", "Nf6"), moves);
    }

    @Test
    @DisplayName("Extracts moves while stripping comments, variations, and NAGs")
    void testPgnWithCommentsAndAnnotations() {
        String pgn = "1. e4 {Best by test} 1... e5 2. Nf3 (2. f4 exf4) 2... Nc6 $1 3. Bc4 Bc5 1-0";
        List<String> moves = PgnParser.extractMoves(pgn);
        assertEquals(List.of("e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5"), moves);
    }

    @Test
    @DisplayName("Extracts moves from simple move string")
    void testSimpleMoveString() {
        String pgn = "1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7#";
        List<String> moves = PgnParser.extractMoves(pgn);
        assertEquals(List.of("e4", "e5", "Qh5", "Nc6", "Bc4", "Nf6", "Qxf7#"), moves);
    }

    @Test
    @DisplayName("Throws IllegalArgumentException on null, blank or empty PGN")
    void testInvalidPgn() {
        assertThrows(IllegalArgumentException.class, () -> PgnParser.extractMoves(null));
        assertThrows(IllegalArgumentException.class, () -> PgnParser.extractMoves("   "));
        assertThrows(IllegalArgumentException.class, () -> PgnParser.extractMoves("[Event \"Empty\"]"));
    }
}
