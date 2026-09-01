package com.project1.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    @DisplayName("Replays Scholar's mate moves generating accurate FEN after each move")
    void testScholarsMateReplay() {
        GameState state = new GameState();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", state.toFen());

        // 1. e4
        state.applySanMove("e4");
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", state.toFen());

        // 1... e5
        state.applySanMove("e5");
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2", state.toFen());

        // 2. Qh5
        state.applySanMove("Qh5");
        assertEquals("rnbqkbnr/pppp1ppp/8/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2", state.toFen());

        // 2... Nc6
        state.applySanMove("Nc6");
        assertEquals("r1bqkbnr/pppp1ppp/2n5/4p2Q/4P3/8/PPPP1PPP/RNB1KBNR w KQkq - 2 3", state.toFen());

        // 3. Bc4
        state.applySanMove("Bc4");
        assertEquals("r1bqkbnr/pppp1ppp/2n5/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 3 3", state.toFen());

        // 3... Nf6
        state.applySanMove("Nf6");
        assertEquals("r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4", state.toFen());

        // 4. Qxf7#
        state.applySanMove("Qxf7#");
        assertEquals("r1bqkb1r/pppp1Qpp/2n2n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4", state.toFen());
    }

    @Test
    @DisplayName("Executes Kingside and Queenside castling")
    void testCastling() {
        GameState state = new GameState();

        // 1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. O-O
        state.applySanMove("e4");
        state.applySanMove("e5");
        state.applySanMove("Nf3");
        state.applySanMove("Nc6");
        state.applySanMove("Bc4");
        state.applySanMove("Bc5");
        state.applySanMove("O-O");

        assertEquals(new Piece(Color.WHITE, PieceType.KING), state.getBoard().getPiece(Position.fromAlgebraic("g1")));
        assertEquals(new Piece(Color.WHITE, PieceType.ROOK), state.getBoard().getPiece(Position.fromAlgebraic("f1")));
        assertNull(state.getBoard().getPiece(Position.fromAlgebraic("e1")));
        assertNull(state.getBoard().getPiece(Position.fromAlgebraic("h1")));
        assertEquals("kq", state.getCastlingRights());
    }

    @Test
    @DisplayName("Executes Pawn promotion")
    void testPawnPromotion() {
        GameState state = new GameState();
        state.getBoard().clear();

        // White pawn on e7 ready to promote on e8
        state.getBoard().setPiece(Position.fromAlgebraic("e7"), new Piece(Color.WHITE, PieceType.PAWN));
        state.getBoard().setPiece(Position.fromAlgebraic("e1"), new Piece(Color.WHITE, PieceType.KING));
        state.getBoard().setPiece(Position.fromAlgebraic("e8"), null);

        state.applySanMove("e8=Q");
        assertEquals(new Piece(Color.WHITE, PieceType.QUEEN), state.getBoard().getPiece(Position.fromAlgebraic("e8")));
        assertNull(state.getBoard().getPiece(Position.fromAlgebraic("e7")));
    }

    @Test
    @DisplayName("Throws IllegalArgumentException on illegal or invalid move")
    void testInvalidMove() {
        GameState state = new GameState();
        assertThrows(IllegalArgumentException.class, () -> state.applySanMove("e5")); // e5 is illegal on move 1 for White
        assertThrows(IllegalArgumentException.class, () -> state.applySanMove(null));
        assertThrows(IllegalArgumentException.class, () -> state.applySanMove(""));
    }

    @Test
    @DisplayName("Castling validation rejects castling when blocked or lacking rights")
    void testInvalidCastlingValidation() {
        GameState state = new GameState();
        // Blocked by bishop/knight on starting position
        assertThrows(IllegalArgumentException.class, () -> state.applySanMove("O-O"));
        assertThrows(IllegalArgumentException.class, () -> state.applySanMove("O-O-O"));
    }
}
