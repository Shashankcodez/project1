package com.project1.board;

import java.util.Objects;

/**
 * Represents a square on an 8x8 chessboard.
 *
 * Coordinate Convention:
 * -------------------------------------------------------------
 * Rows and columns are 0-indexed (0 to 7):
 * - row 0 = Rank 8 (Black back rank: a8 to h8)
 * - row 1 = Rank 7 (Black pawns: a7 to h7)
 * - row 2 = Rank 6
 * - row 3 = Rank 5
 * - row 4 = Rank 4
 * - row 5 = Rank 3
 * - row 6 = Rank 2 (White pawns: a2 to h2)
 * - row 7 = Rank 1 (White back rank: a1 to h1)
 *
 * - col 0 = File 'a'
 * - col 1 = File 'b'
 * - col 2 = File 'c'
 * - col 3 = File 'd'
 * - col 4 = File 'e'
 * - col 5 = File 'f'
 * - col 6 = File 'g'
 * - col 7 = File 'h'
 * -------------------------------------------------------------
 */
public final class Position {

    private final int row;
    private final int col;

    /**
     * Constructs a Position using 0-indexed row and column.
     *
     * @param row 0 (rank 8) to 7 (rank 1)
     * @param col 0 (file 'a') to 7 (file 'h')
     */
    public Position(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException(
                "Row and column must be in range [0, 7]. Given: row=" + row + ", col=" + col
            );
        }
        this.row = row;
        this.col = col;
    }

    /**
     * Checks if the given row and column indices are within the 8x8 chessboard bounds.
     *
     * @param row row index
     * @param col column index
     * @return true if row and col are between 0 and 7 inclusive
     */
    public static boolean isValid(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    /**
     * Factory method to create a Position from standard algebraic notation (e.g., "e4", "a1", "e8").
     *
     * @param notation 2-character square notation (case-insensitive file)
     * @return the corresponding Position instance
     */
    public static Position fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Algebraic notation must be 2 characters (e.g. 'e4')");
        }
        char fileChar = Character.toLowerCase(notation.charAt(0));
        char rankChar = notation.charAt(1);

        if (fileChar < 'a' || fileChar > 'h') {
            throw new IllegalArgumentException("File must be between 'a' and 'h': " + notation);
        }
        if (rankChar < '1' || rankChar > '8') {
            throw new IllegalArgumentException("Rank must be between '1' and '8': " + notation);
        }

        int col = fileChar - 'a';
        int rank = rankChar - '0';
        int row = 8 - rank;

        return new Position(row, col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRank() {
        return 8 - row;
    }

    public char getFile() {
        return (char) ('a' + col);
    }

    /**
     * Returns standard algebraic notation for this position (e.g. "e4").
     */
    public String toAlgebraic() {
        return "" + getFile() + getRank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}
