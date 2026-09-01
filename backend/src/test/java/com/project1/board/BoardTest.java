package com.project1.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    @DisplayName("A newly created board has empty squares before initialization")
    void testNewBoardIsEmptyBeforeInitialization() {
        Board board = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                assertNull(board.getPiece(row, col),
                        "Square at (" + row + ", " + col + ") should be null before initialization");
                assertNull(board.getPiece(new Position(row, col)),
                        "Position at (" + row + ", " + col + ") should be null before initialization");
            }
        }
    }

    @Test
    @DisplayName("The standard starting position contains correct pieces on important squares (e1, e8, a1, h1, a8, h8, e2, e7)")
    void testStandardStartingPositionKeySquares() {
        Board board = new Board();
        board.initializeStandardPosition();

        // e1: White King
        Piece e1 = board.getPiece(Position.fromAlgebraic("e1"));
        assertNotNull(e1);
        assertEquals(Color.WHITE, e1.getColor());
        assertEquals(PieceType.KING, e1.getPieceType());

        // e8: Black King
        Piece e8 = board.getPiece(Position.fromAlgebraic("e8"));
        assertNotNull(e8);
        assertEquals(Color.BLACK, e8.getColor());
        assertEquals(PieceType.KING, e8.getPieceType());

        // a1: White Rook
        Piece a1 = board.getPiece(Position.fromAlgebraic("a1"));
        assertNotNull(a1);
        assertEquals(Color.WHITE, a1.getColor());
        assertEquals(PieceType.ROOK, a1.getPieceType());

        // h1: White Rook
        Piece h1 = board.getPiece(Position.fromAlgebraic("h1"));
        assertNotNull(h1);
        assertEquals(Color.WHITE, h1.getColor());
        assertEquals(PieceType.ROOK, h1.getPieceType());

        // a8: Black Rook
        Piece a8 = board.getPiece(Position.fromAlgebraic("a8"));
        assertNotNull(a8);
        assertEquals(Color.BLACK, a8.getColor());
        assertEquals(PieceType.ROOK, a8.getPieceType());

        // h8: Black Rook
        Piece h8 = board.getPiece(Position.fromAlgebraic("h8"));
        assertNotNull(h8);
        assertEquals(Color.BLACK, h8.getColor());
        assertEquals(PieceType.ROOK, h8.getPieceType());

        // e2: White Pawn
        Piece e2 = board.getPiece(Position.fromAlgebraic("e2"));
        assertNotNull(e2);
        assertEquals(Color.WHITE, e2.getColor());
        assertEquals(PieceType.PAWN, e2.getPieceType());

        // e7: Black Pawn
        Piece e7 = board.getPiece(Position.fromAlgebraic("e7"));
        assertNotNull(e7);
        assertEquals(Color.BLACK, e7.getColor());
        assertEquals(PieceType.PAWN, e7.getPieceType());
    }

    @Test
    @DisplayName("White and black pieces have the correct Color and PieceType across back ranks and pawns")
    void testWhiteAndBlackPieceColorsAndTypes() {
        Board board = new Board();
        board.initializeStandardPosition();

        // Check White back rank pieces (Rank 1, Row 7)
        assertEquals(new Piece(Color.WHITE, PieceType.ROOK), board.getPiece(Position.fromAlgebraic("a1")));
        assertEquals(new Piece(Color.WHITE, PieceType.KNIGHT), board.getPiece(Position.fromAlgebraic("b1")));
        assertEquals(new Piece(Color.WHITE, PieceType.BISHOP), board.getPiece(Position.fromAlgebraic("c1")));
        assertEquals(new Piece(Color.WHITE, PieceType.QUEEN), board.getPiece(Position.fromAlgebraic("d1")));
        assertEquals(new Piece(Color.WHITE, PieceType.KING), board.getPiece(Position.fromAlgebraic("e1")));
        assertEquals(new Piece(Color.WHITE, PieceType.BISHOP), board.getPiece(Position.fromAlgebraic("f1")));
        assertEquals(new Piece(Color.WHITE, PieceType.KNIGHT), board.getPiece(Position.fromAlgebraic("g1")));
        assertEquals(new Piece(Color.WHITE, PieceType.ROOK), board.getPiece(Position.fromAlgebraic("h1")));

        // Check White pawns (Rank 2, Row 6)
        for (char file = 'a'; file <= 'h'; file++) {
            Piece pawn = board.getPiece(Position.fromAlgebraic(file + "2"));
            assertNotNull(pawn);
            assertEquals(Color.WHITE, pawn.getColor());
            assertEquals(PieceType.PAWN, pawn.getPieceType());
        }

        // Check empty ranks (Ranks 3-6, Rows 2-5)
        for (int rank = 3; rank <= 6; rank++) {
            for (char file = 'a'; file <= 'h'; file++) {
                assertNull(board.getPiece(Position.fromAlgebraic("" + file + rank)));
            }
        }

        // Check Black pawns (Rank 7, Row 1)
        for (char file = 'a'; file <= 'h'; file++) {
            Piece pawn = board.getPiece(Position.fromAlgebraic(file + "7"));
            assertNotNull(pawn);
            assertEquals(Color.BLACK, pawn.getColor());
            assertEquals(PieceType.PAWN, pawn.getPieceType());
        }

        // Check Black back rank pieces (Rank 8, Row 0)
        assertEquals(new Piece(Color.BLACK, PieceType.ROOK), board.getPiece(Position.fromAlgebraic("a8")));
        assertEquals(new Piece(Color.BLACK, PieceType.KNIGHT), board.getPiece(Position.fromAlgebraic("b8")));
        assertEquals(new Piece(Color.BLACK, PieceType.BISHOP), board.getPiece(Position.fromAlgebraic("c8")));
        assertEquals(new Piece(Color.BLACK, PieceType.QUEEN), board.getPiece(Position.fromAlgebraic("d8")));
        assertEquals(new Piece(Color.BLACK, PieceType.KING), board.getPiece(Position.fromAlgebraic("e8")));
        assertEquals(new Piece(Color.BLACK, PieceType.BISHOP), board.getPiece(Position.fromAlgebraic("f8")));
        assertEquals(new Piece(Color.BLACK, PieceType.KNIGHT), board.getPiece(Position.fromAlgebraic("g8")));
        assertEquals(new Piece(Color.BLACK, PieceType.ROOK), board.getPiece(Position.fromAlgebraic("h8")));
    }

    @Test
    @DisplayName("Verify all 32 pieces are correctly present in standard setup")
    void testStandardSetupPieceCount() {
        Board board = new Board();
        board.initializeStandardPosition();

        int totalPieces = 0;
        int whitePieces = 0;
        int blackPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    totalPieces++;
                    if (piece.getColor() == Color.WHITE) {
                        whitePieces++;
                    } else if (piece.getColor() == Color.BLACK) {
                        blackPieces++;
                    }
                }
            }
        }

        assertEquals(32, totalPieces, "Total piece count in standard setup should be 32");
        assertEquals(16, whitePieces, "White piece count should be 16");
        assertEquals(16, blackPieces, "Black piece count should be 16");
    }

    @Test
    @DisplayName("The exact ASCII output of the starting position matches the required format")
    void testExactAsciiOutputOfStartingPosition() {
        Board board = new Board();
        board.initializeStandardPosition();

        String expectedAscii = String.join("\n",
                "r n b q k b n r",
                "p p p p p p p p",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                "P P P P P P P P",
                "R N B Q K B N R"
        );

        assertEquals(expectedAscii, board.toString());
    }

    @Test
    @DisplayName("ASCII output of an empty board displays dots on all squares")
    void testAsciiOutputOfEmptyBoard() {
        Board board = new Board();

        String expectedAscii = String.join("\n",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . .",
                ". . . . . . . ."
        );

        assertEquals(expectedAscii, board.toString());
    }

    @Test
    @DisplayName("Position coordinate mapping for a1, h1, a8, h8, e1, e8")
    void testPositionKeySquareMappings() {
        Position a1 = Position.fromAlgebraic("a1");
        assertEquals(7, a1.getRow());
        assertEquals(0, a1.getCol());
        assertEquals(1, a1.getRank());
        assertEquals('a', a1.getFile());

        Position h1 = Position.fromAlgebraic("h1");
        assertEquals(7, h1.getRow());
        assertEquals(7, h1.getCol());
        assertEquals(1, h1.getRank());
        assertEquals('h', h1.getFile());

        Position a8 = Position.fromAlgebraic("a8");
        assertEquals(0, a8.getRow());
        assertEquals(0, a8.getCol());
        assertEquals(8, a8.getRank());
        assertEquals('a', a8.getFile());

        Position h8 = Position.fromAlgebraic("h8");
        assertEquals(0, h8.getRow());
        assertEquals(7, h8.getCol());
        assertEquals(8, h8.getRank());
        assertEquals('h', h8.getFile());

        Position e1 = Position.fromAlgebraic("e1");
        assertEquals(7, e1.getRow());
        assertEquals(4, e1.getCol());
        assertEquals(1, e1.getRank());
        assertEquals('e', e1.getFile());

        Position e8 = Position.fromAlgebraic("e8");
        assertEquals(0, e8.getRow());
        assertEquals(4, e8.getCol());
        assertEquals(8, e8.getRank());
        assertEquals('e', e8.getFile());
    }

    @Test
    @DisplayName("Position throws IllegalArgumentException on invalid algebraic strings and out-of-bounds coordinates")
    void testInvalidPositionCoordinatesAndAlgebraic() {
        // Out-of-bounds row/col
        assertThrows(IllegalArgumentException.class, () -> new Position(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Position(8, 0));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, 8));

        // Invalid algebraic notations
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic(null));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic(""));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("e"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("e9"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("e0"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("i4"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("z1"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("e44"));
    }

    @Test
    @DisplayName("Board throws IllegalArgumentException on out-of-bounds coordinates or null Position")
    void testBoardOutOfBoundsAndNullSafety() {
        Board board = new Board();
        Piece pawn = new Piece(Color.WHITE, PieceType.PAWN);

        assertThrows(IllegalArgumentException.class, () -> board.getPiece(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getPiece(8, 0));
        assertThrows(IllegalArgumentException.class, () -> board.getPiece(0, -1));
        assertThrows(IllegalArgumentException.class, () -> board.getPiece(0, 8));

        assertThrows(IllegalArgumentException.class, () -> board.setPiece(-1, 0, pawn));
        assertThrows(IllegalArgumentException.class, () -> board.setPiece(8, 0, pawn));
        assertThrows(IllegalArgumentException.class, () -> board.setPiece(0, -1, pawn));
        assertThrows(IllegalArgumentException.class, () -> board.setPiece(0, 8, pawn));

        assertThrows(IllegalArgumentException.class, () -> board.getPiece(null));
        assertThrows(IllegalArgumentException.class, () -> board.setPiece(null, pawn));
    }

    @Test
    @DisplayName("Corner getPiece and setPiece operations for a1, a8, h1, h8")
    void testCornerGetAndSetPiece() {
        Board board = new Board();

        Position a1 = Position.fromAlgebraic("a1");
        Position a8 = Position.fromAlgebraic("a8");
        Position h1 = Position.fromAlgebraic("h1");
        Position h8 = Position.fromAlgebraic("h8");

        Piece whiteRook = new Piece(Color.WHITE, PieceType.ROOK);
        Piece blackRook = new Piece(Color.BLACK, PieceType.ROOK);

        board.setPiece(a1, whiteRook);
        board.setPiece(h1, whiteRook);
        board.setPiece(a8, blackRook);
        board.setPiece(h8, blackRook);

        assertEquals(whiteRook, board.getPiece(a1));
        assertEquals(whiteRook, board.getPiece(h1));
        assertEquals(blackRook, board.getPiece(a8));
        assertEquals(blackRook, board.getPiece(h8));

        assertEquals(whiteRook, board.getPiece(7, 0));
        assertEquals(whiteRook, board.getPiece(7, 7));
        assertEquals(blackRook, board.getPiece(0, 0));
        assertEquals(blackRook, board.getPiece(0, 7));
    }

    @Test
    @DisplayName("Board clear() removes all pieces from an initialized board")
    void testBoardClear() {
        Board board = new Board();
        board.initializeStandardPosition();

        board.clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                assertNull(board.getPiece(row, col),
                        "Square at (" + row + ", " + col + ") should be null after clear()");
            }
        }
    }
}
