package com.project1.board;

import java.util.Objects;

/**
 * Represents a chess move from a starting Position to a destination Position.
 * Optionally records the captured Piece (if any).
 */
public final class Move {

    private final Position from;
    private final Position to;
    private final Piece capturedPiece;

    /**
     * Constructs a non-capturing move.
     *
     * @param from starting square position
     * @param to destination square position
     */
    public Move(Position from, Position to) {
        this(from, to, null);
    }

    /**
     * Constructs a move with an optional captured piece.
     *
     * @param from starting square position
     * @param to destination square position
     * @param capturedPiece piece captured at the destination, or null if non-capturing
     */
    public Move(Position from, Position to, Piece capturedPiece) {
        if (from == null) {
            throw new IllegalArgumentException("Starting position ('from') cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("Destination position ('to') cannot be null");
        }
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Checks if this move captures an opponent's piece.
     *
     * @return true if a piece was captured, false otherwise
     */
    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move move)) return false;
        return Objects.equals(from, move.from) &&
               Objects.equals(to, move.to) &&
               Objects.equals(capturedPiece, move.capturedPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, capturedPiece);
    }

    @Override
    public String toString() {
        if (isCapture()) {
            return from.toAlgebraic() + " -> " + to.toAlgebraic() + " (captures " + capturedPiece + ")";
        }
        return from.toAlgebraic() + " -> " + to.toAlgebraic();
    }
}
