package com.project1.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FenBuilderTest {

    @Test
    @DisplayName("Generates exact standard starting FEN from initialized board")
    void testStandardStartingFen() {
        Board board = new Board();
        board.initializeStandardPosition();

        String fen = FenBuilder.toFen(board, Color.WHITE, "KQkq", "-", 0, 1);
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen);
    }

    @Test
    @DisplayName("Generates FEN for an empty board with custom state")
    void testEmptyBoardFen() {
        Board board = new Board();

        String fen = FenBuilder.toFen(board, Color.BLACK, "-", "-", 10, 25);
        assertEquals("8/8/8/8/8/8/8/8 b - - 10 25", fen);
    }
}
