package com.project1.board;

import java.util.Objects;

/**
 * Represents an immutable chess piece with a specific Color and PieceType.
 */
public final class Piece {

    private final Color color;
    private final PieceType pieceType;

    public Piece(Color color, PieceType pieceType) {
        if (color == null) {
            throw new IllegalArgumentException("Piece color cannot be null");
        }
        if (pieceType == null) {
            throw new IllegalArgumentException("Piece type cannot be null");
        }
        this.color = color;
        this.pieceType = pieceType;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Returns a single character representing this piece:
     * Uppercase for WHITE pieces (P, N, B, R, Q, K),
     * Lowercase for BLACK pieces (p, n, b, r, q, k).
     */
    public char getSymbol() {
        char baseChar = switch (pieceType) {
            case PAWN -> 'P';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case ROOK -> 'R';
            case QUEEN -> 'Q';
            case KING -> 'K';
        };
        return color == Color.WHITE ? baseChar : Character.toLowerCase(baseChar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        return color == piece.color && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, pieceType);
    }

    @Override
    public String toString() {
        return String.valueOf(getSymbol());
    }
}
