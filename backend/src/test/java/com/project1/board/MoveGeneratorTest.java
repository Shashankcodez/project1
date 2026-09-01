package com.project1.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    @Test
    @DisplayName("Move value object should store properties and format string correctly")
    void testMoveObjectProperties() {
        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e4");
        Piece captured = new Piece(Color.BLACK, PieceType.PAWN);

        Move nonCaptureMove = new Move(from, to);
        assertEquals(from, nonCaptureMove.getFrom());
        assertEquals(to, nonCaptureMove.getTo());
        assertNull(nonCaptureMove.getCapturedPiece());
        assertFalse(nonCaptureMove.isCapture());
        assertEquals("e2 -> e4", nonCaptureMove.toString());

        Move captureMove = new Move(from, to, captured);
        assertEquals(captured, captureMove.getCapturedPiece());
        assertTrue(captureMove.isCapture());
        assertTrue(captureMove.toString().contains("captures"));

        assertEquals(new Move(from, to), new Move(Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4")));
        assertThrows(IllegalArgumentException.class, () -> new Move(null, to));
        assertThrows(IllegalArgumentException.class, () -> new Move(from, null));
    }

    @Test
    @DisplayName("Standard starting position generates exactly 20 moves for White and 20 for Black")
    void testStandardStartingPositionMovesCount() {
        Board board = new Board();
        board.initializeStandardPosition();

        List<Move> whiteMoves = MoveGenerator.generateAllMoves(board, Color.WHITE);
        List<Move> blackMoves = MoveGenerator.generateAllMoves(board, Color.BLACK);

        // 16 pawn moves (each pawn can advance 1 or 2 squares) + 4 knight moves (2 moves each for b1/g1 or b8/g8)
        assertEquals(20, whiteMoves.size(), "White should have exactly 20 pseudo-legal opening moves");
        assertEquals(20, blackMoves.size(), "Black should have exactly 20 pseudo-legal opening moves");
    }

    @Test
    @DisplayName("White and Black Pawns: 1-square, 2-square, and blocked forward movement")
    void testPawnForwardMovementAndBlocking() {
        Board board = new Board();

        // White pawn on starting rank e2
        board.setPiece(Position.fromAlgebraic("e2"), new Piece(Color.WHITE, PieceType.PAWN));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e2"));
        assertEquals(2, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e3"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e4"))));

        // Block 1 square in front (at e3)
        board.setPiece(Position.fromAlgebraic("e3"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e2"));
        assertEquals(0, moves.size(), "Pawn blocked immediately in front cannot move 1 or 2 squares");

        // Clear e3 and block 2 squares in front (at e4)
        board.setPiece(Position.fromAlgebraic("e3"), null);
        board.setPiece(Position.fromAlgebraic("e4"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e2"));
        assertEquals(1, moves.size(), "Pawn should only be able to move 1 square if 2nd square is blocked");
        assertEquals(Position.fromAlgebraic("e3"), moves.get(0).getTo());

        // White pawn not on starting rank cannot make 2-square jump
        board.clear();
        board.setPiece(Position.fromAlgebraic("e3"), new Piece(Color.WHITE, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e3"));
        assertEquals(1, moves.size());
        assertEquals(Position.fromAlgebraic("e4"), moves.get(0).getTo());

        // Black pawn on starting rank e7
        board.clear();
        board.setPiece(Position.fromAlgebraic("e7"), new Piece(Color.BLACK, PieceType.PAWN));
        List<Move> blackMoves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e7"));
        assertEquals(2, blackMoves.size());
        assertTrue(blackMoves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e6"))));
        assertTrue(blackMoves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e5"))));
    }

    @Test
    @DisplayName("Pawn diagonal captures for White and Black, including edge file handling")
    void testPawnDiagonalCaptures() {
        Board board = new Board();

        // White pawn at d4 with Black targets at c5 and e5, and a friendly piece at d5
        board.setPiece(Position.fromAlgebraic("d4"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("c5"), new Piece(Color.BLACK, PieceType.KNIGHT));
        board.setPiece(Position.fromAlgebraic("e5"), new Piece(Color.BLACK, PieceType.BISHOP));
        board.setPiece(Position.fromAlgebraic("d5"), new Piece(Color.WHITE, PieceType.ROOK)); // blocks forward

        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(2, moves.size(), "Forward is blocked; only 2 diagonal captures should be available");
        assertTrue(moves.stream().allMatch(Move::isCapture));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("c5"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e5"))));

        // Friendly pieces on diagonals cannot be captured
        board.setPiece(Position.fromAlgebraic("c5"), new Piece(Color.WHITE, PieceType.KNIGHT));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(1, moves.size());
        assertEquals(Position.fromAlgebraic("e5"), moves.get(0).getTo());

        // Edge file pawn (a-file) only captures right diagonal
        board.clear();
        board.setPiece(Position.fromAlgebraic("a2"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("b3"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("a2"));
        assertEquals(3, moves.size(), "Should have a3, a4 (forward moves) and b3 (diagonal capture)");
    }

    @Test
    @DisplayName("Knight: 8 L-shaped moves, jumping over pieces, friendly blocking, and captures")
    void testKnightMovement() {
        Board board = new Board();

        // Knight in center (d4) on empty board has 8 moves
        board.setPiece(Position.fromAlgebraic("d4"), new Piece(Color.WHITE, PieceType.KNIGHT));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(8, moves.size());

        // Surround knight with friendly pieces to test jumping over pieces
        board.setPiece(Position.fromAlgebraic("d5"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("d3"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("c4"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("e4"), new Piece(Color.WHITE, PieceType.PAWN));
        // Knight still jumps over surrounding pieces to all 8 destination squares
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(8, moves.size(), "Knight should jump over adjacent pieces");

        // Put friendly piece on target e6 and opponent piece on target c6
        board.setPiece(Position.fromAlgebraic("e6"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("c6"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(7, moves.size(), "Friendly target blocked, opponent target captured");
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e6"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("c6")) && m.isCapture()));

        // Corner knight (a1) has only 2 moves: b3 and c2
        board.clear();
        board.setPiece(Position.fromAlgebraic("a1"), new Piece(Color.WHITE, PieceType.KNIGHT));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("a1"));
        assertEquals(2, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("b3"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("c2"))));
    }

    @Test
    @DisplayName("Bishop: diagonal sliding, friendly blocking, and captures")
    void testBishopMovement() {
        Board board = new Board();

        // Bishop at d4 on empty board
        board.setPiece(Position.fromAlgebraic("d4"), new Piece(Color.WHITE, PieceType.BISHOP));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        // d4 diagonals: (c5, b6, a7) + (e5, f6, g7, h8) + (c3, b2, a1) + (e3, f2, g1) = 3 + 4 + 3 + 3 = 13
        assertEquals(13, moves.size());

        // Friendly piece at f6 blocks f6, g7, h8
        board.setPiece(Position.fromAlgebraic("f6"), new Piece(Color.WHITE, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        // Ray toward top-right now only has e5
        assertEquals(10, moves.size());
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("f6"))));

        // Opponent piece at b2 allows capture on b2 but blocks a1
        board.setPiece(Position.fromAlgebraic("b2"), new Piece(Color.BLACK, PieceType.ROOK));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("b2")) && m.isCapture()));
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("a1"))));
    }

    @Test
    @DisplayName("Rook: horizontal and vertical sliding, friendly blocking, and captures")
    void testRookMovement() {
        Board board = new Board();

        // Rook at d4 on empty board has 14 moves (7 horizontal, 7 vertical)
        board.setPiece(Position.fromAlgebraic("d4"), new Piece(Color.WHITE, PieceType.ROOK));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(14, moves.size());

        // Friendly piece at d6 blocks d6, d7, d8 (leaving only d5 up)
        board.setPiece(Position.fromAlgebraic("d6"), new Piece(Color.WHITE, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(11, moves.size());
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("d6"))));

        // Opponent piece at b4 allows capture on b4 but blocks a4
        board.setPiece(Position.fromAlgebraic("b4"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("b4")) && m.isCapture()));
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("a4"))));
    }

    @Test
    @DisplayName("Queen: combined rook and bishop sliding moves")
    void testQueenMovement() {
        Board board = new Board();

        // Queen at d4 on empty board has 14 (orthogonal) + 13 (diagonal) = 27 moves
        board.setPiece(Position.fromAlgebraic("d4"), new Piece(Color.WHITE, PieceType.QUEEN));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("d4"));
        assertEquals(27, moves.size());
    }

    @Test
    @DisplayName("King: 1-square movement in 8 directions, corner boundaries, blocking and captures")
    void testKingMovement() {
        Board board = new Board();

        // King at e4 on empty board has 8 moves
        board.setPiece(Position.fromAlgebraic("e4"), new Piece(Color.WHITE, PieceType.KING));
        List<Move> moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e4"));
        assertEquals(8, moves.size());

        // Friendly piece at e5, Opponent piece at d5
        board.setPiece(Position.fromAlgebraic("e5"), new Piece(Color.WHITE, PieceType.PAWN));
        board.setPiece(Position.fromAlgebraic("d5"), new Piece(Color.BLACK, PieceType.PAWN));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("e4"));
        assertEquals(7, moves.size());
        assertFalse(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("e5"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("d5")) && m.isCapture()));

        // King in corner (a1) has only 3 moves: a2, b2, b1
        board.clear();
        board.setPiece(Position.fromAlgebraic("a1"), new Piece(Color.WHITE, PieceType.KING));
        moves = MoveGenerator.generateMoves(board, Position.fromAlgebraic("a1"));
        assertEquals(3, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("a2"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("b2"))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(Position.fromAlgebraic("b1"))));
    }

    @Test
    @DisplayName("isValidMove correctly validates pseudo-legal moves")
    void testIsValidMove() {
        Board board = new Board();
        board.initializeStandardPosition();

        // White pawn e2 -> e4 is valid
        assertTrue(MoveGenerator.isValidMove(board, Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4")));
        assertTrue(MoveGenerator.isValidMove(board, new Move(Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4"))));

        // White pawn e2 -> e5 is invalid
        assertFalse(MoveGenerator.isValidMove(board, Position.fromAlgebraic("e2"), Position.fromAlgebraic("e5")));

        // White knight b1 -> c3 is valid
        assertTrue(MoveGenerator.isValidMove(board, Position.fromAlgebraic("b1"), Position.fromAlgebraic("c3")));

        // Blocked white rook a1 -> a3 is invalid
        assertFalse(MoveGenerator.isValidMove(board, Position.fromAlgebraic("a1"), Position.fromAlgebraic("a3")));

        // Empty square or null input is invalid
        assertFalse(MoveGenerator.isValidMove(board, Position.fromAlgebraic("e4"), Position.fromAlgebraic("e5")));
        assertFalse(MoveGenerator.isValidMove(null, Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4")));
        assertFalse(MoveGenerator.isValidMove(board, null, Position.fromAlgebraic("e4")));
    }
}
