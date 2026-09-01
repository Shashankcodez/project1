package com.project1.board;

/**
 * Represents a standard 8x8 chess board.
 * Uses a 2D array representation (Piece[8][8]) where:
 * - Row 0 corresponds to Rank 8 (Black's starting back rank)
 * - Row 7 corresponds to Rank 1 (White's starting back rank)
 * - Column 0 corresponds to File 'a'
 * - Column 7 corresponds to File 'h'
 */
public class Board {

    private final Piece[][] board = new Piece[8][8];

    /**
     * Creates an empty chess board.
     */
    public Board() {
        clear();
    }

    /**
     * Clears all pieces from the board.
     */
    public void clear() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
    }

    /**
     * Gets the piece at the specified Position.
     *
     * @param position target position
     * @return the Piece at the square, or null if empty
     */
    public Piece getPiece(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        return getPiece(position.getRow(), position.getCol());
    }

    /**
     * Gets the piece at the specified row and column indices.
     *
     * @param row row index (0 to 7)
     * @param col column index (0 to 7)
     * @return the Piece at the square, or null if empty
     */
    public Piece getPiece(int row, int col) {
        validateCoordinates(row, col);
        return board[row][col];
    }

    /**
     * Places a piece at the specified Position.
     *
     * @param position target position
     * @param piece piece to place, or null to clear the square
     */
    public void setPiece(Position position, Piece piece) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        setPiece(position.getRow(), position.getCol(), piece);
    }

    /**
     * Places a piece at the specified row and column indices.
     *
     * @param row row index (0 to 7)
     * @param col column index (0 to 7)
     * @param piece piece to place, or null to clear the square
     */
    public void setPiece(int row, int col, Piece piece) {
        validateCoordinates(row, col);
        board[row][col] = piece;
    }

    /**
     * Initializes the standard chess starting position for White and Black.
     */
    public void initializeStandardPosition() {
        clear();

        // Row 0 (Rank 8): Black major pieces
        board[0][0] = new Piece(Color.BLACK, PieceType.ROOK);
        board[0][1] = new Piece(Color.BLACK, PieceType.KNIGHT);
        board[0][2] = new Piece(Color.BLACK, PieceType.BISHOP);
        board[0][3] = new Piece(Color.BLACK, PieceType.QUEEN);
        board[0][4] = new Piece(Color.BLACK, PieceType.KING);
        board[0][5] = new Piece(Color.BLACK, PieceType.BISHOP);
        board[0][6] = new Piece(Color.BLACK, PieceType.KNIGHT);
        board[0][7] = new Piece(Color.BLACK, PieceType.ROOK);

        // Row 1 (Rank 7): Black pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Piece(Color.BLACK, PieceType.PAWN);
        }

        // Row 6 (Rank 2): White pawns
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Piece(Color.WHITE, PieceType.PAWN);
        }

        // Row 7 (Rank 1): White major pieces
        board[7][0] = new Piece(Color.WHITE, PieceType.ROOK);
        board[7][1] = new Piece(Color.WHITE, PieceType.KNIGHT);
        board[7][2] = new Piece(Color.WHITE, PieceType.BISHOP);
        board[7][3] = new Piece(Color.WHITE, PieceType.QUEEN);
        board[7][4] = new Piece(Color.WHITE, PieceType.KING);
        board[7][5] = new Piece(Color.WHITE, PieceType.BISHOP);
        board[7][6] = new Piece(Color.WHITE, PieceType.KNIGHT);
        board[7][7] = new Piece(Color.WHITE, PieceType.ROOK);
    }

    private void validateCoordinates(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException(
                "Coordinates out of bounds: row=" + row + ", col=" + col
            );
        }
    }

    /**
     * Produces the 8x8 ASCII representation of the board where:
     * - Lowercase letters represent Black pieces (r, n, b, q, k, p)
     * - Uppercase letters represent White pieces (R, N, B, Q, K, P)
     * - Dots (.) represent empty squares
     * - Elements in each row are separated by a space
     * - Rows are separated by newline (\n)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                sb.append(piece != null ? piece.getSymbol() : '.');
                if (col < 7) {
                    sb.append(' ');
                }
            }
            if (row < 7) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
