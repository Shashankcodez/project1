package com.project1.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FenValidatorTest {

    @Test
    @DisplayName("Valid standard FEN strings are accepted")
    void testValidFens() {
        // Standard starting position
        assertTrue(FenValidator.isValid(FenValidator.STARTING_FEN));
        assertDoesNotThrow(() -> FenValidator.validate(FenValidator.STARTING_FEN));

        // Sicilian Defense position after 1. e4 c5
        String sicilian = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2";
        assertTrue(FenValidator.isValid(sicilian));

        // Endgame position with mate in 1 setup
        String endgame = "8/8/8/8/8/5k2/8/4K2R w - - 0 50";
        assertTrue(FenValidator.isValid(endgame));

        // Black to move with no castling and en passant
        String blackToMove = "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R b KQkq - 0 5";
        assertTrue(FenValidator.isValid(blackToMove));
    }

    @Test
    @DisplayName("Invalid FEN strings are rejected")
    void testInvalidFens() {
        // Null or blank
        assertFalse(FenValidator.isValid(null));
        assertFalse(FenValidator.isValid(""));
        assertFalse(FenValidator.isValid("   "));

        // Wrong number of components (less than 6)
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq"));

        // Invalid active color
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1"));

        // Rank piece count does not equal 8
        assertFalse(FenValidator.isValid("rnbqkbnr/ppppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")); // 9 pawns on rank 7
        assertFalse(FenValidator.isValid("rnbqkbnr/ppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));  // 7 squares on rank 7

        // Invalid castling field
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w X - 0 1"));

        // Invalid en passant square
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq z9 0 1"));

        // Negative halfmove clock
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1"));

        // Fullmove number less than 1
        assertFalse(FenValidator.isValid("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"));

        // validate() throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> FenValidator.validate("invalid fen"));
    }
}
