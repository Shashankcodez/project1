package com.project1.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates and validates pseudo-legal chess moves based on piece movement rules,
 * board boundaries, friendly-piece blocking, and opponent captures.
 *
 * (Note: Check/checkmate, castling, en passant, and promotion are excluded in Phase 3).
 */
public final class MoveGenerator {

    // Offsets for Knight movements (8 L-shapes)
    private static final int[][] KNIGHT_OFFSETS = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2},  {1, 2},  {2, -1},  {2, 1}
    };

    // Directions for sliding Bishop movements (4 diagonals)
    private static final int[][] BISHOP_DIRECTIONS = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    // Directions for sliding Rook movements (4 orthogonals)
    private static final int[][] ROOK_DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    // Directions for Queen movements (all 8 directions)
    private static final int[][] QUEEN_DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    // Offsets for King movements (1 step in 8 directions)
    private static final int[][] KING_OFFSETS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    private MoveGenerator() {
        // Utility class
    }

    /**
     * Generates all pseudo-legal moves for the piece at the given position on the board.
     *
     * @param board the current chess board
     * @param from the position of the piece to move
     * @return list of valid moves, or empty list if no piece or no valid moves
     */
    public static List<Move> generateMoves(Board board, Position from) {
        if (board == null || from == null) {
            throw new IllegalArgumentException("Board and starting position cannot be null");
        }

        Piece piece = board.getPiece(from);
        if (piece == null) {
            return Collections.emptyList();
        }

        return switch (piece.getPieceType()) {
            case PAWN -> generatePawnMoves(board, from, piece);
            case KNIGHT -> generateKnightMoves(board, from, piece);
            case BISHOP -> generateBishopMoves(board, from, piece);
            case ROOK -> generateRookMoves(board, from, piece);
            case QUEEN -> generateQueenMoves(board, from, piece);
            case KING -> generateKingMoves(board, from, piece);
        };
    }

    /**
     * Generates all pseudo-legal moves for all pieces belonging to the specified color.
     *
     * @param board the current chess board
     * @param color the color of the pieces to generate moves for
     * @return list of all pseudo-legal moves for the given color
     */
    public static List<Move> generateAllMoves(Board board, Color color) {
        if (board == null || color == null) {
            throw new IllegalArgumentException("Board and color cannot be null");
        }

        List<Move> allMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() == color) {
                    allMoves.addAll(generateMoves(board, new Position(row, col)));
                }
            }
        }
        return allMoves;
    }

    /**
     * Checks if a move from 'from' to 'to' is valid according to piece movement rules.
     *
     * @param board the chess board
     * @param from starting position
     * @param to destination position
     * @return true if the move is valid, false otherwise
     */
    public static boolean isValidMove(Board board, Position from, Position to) {
        if (board == null || from == null || to == null) {
            return false;
        }
        List<Move> validMoves = generateMoves(board, from);
        for (Move move : validMoves) {
            if (move.getTo().equals(to)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given Move is valid.
     *
     * @param board the chess board
     * @param move the move to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidMove(Board board, Move move) {
        if (board == null || move == null) {
            return false;
        }
        return isValidMove(board, move.getFrom(), move.getTo());
    }

    // --- Piece specific move generation methods ---

    private static List<Move> generatePawnMoves(Board board, Position from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int direction = (piece.getColor() == Color.WHITE) ? -1 : 1;
        int startingRow = (piece.getColor() == Color.WHITE) ? 6 : 1;

        int row = from.getRow();
        int col = from.getCol();

        // 1. One-square forward move
        int oneStepRow = row + direction;
        if (Position.isValid(oneStepRow, col) && board.getPiece(oneStepRow, col) == null) {
            moves.add(new Move(from, new Position(oneStepRow, col), null));

            // 2. Two-square forward move from starting rank
            int twoStepRow = row + 2 * direction;
            if (row == startingRow && Position.isValid(twoStepRow, col) && board.getPiece(twoStepRow, col) == null) {
                moves.add(new Move(from, new Position(twoStepRow, col), null));
            }
        }

        // 3. Diagonal captures
        int[] captureCols = {col - 1, col + 1};
        for (int capCol : captureCols) {
            if (Position.isValid(oneStepRow, capCol)) {
                Piece target = board.getPiece(oneStepRow, capCol);
                if (target != null && target.getColor() != piece.getColor()) {
                    moves.add(new Move(from, new Position(oneStepRow, capCol), target));
                }
            }
        }

        return moves;
    }

    private static List<Move> generateKnightMoves(Board board, Position from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int row = from.getRow();
        int col = from.getCol();

        for (int[] offset : KNIGHT_OFFSETS) {
            int targetRow = row + offset[0];
            int targetCol = col + offset[1];

            if (Position.isValid(targetRow, targetCol)) {
                Piece target = board.getPiece(targetRow, targetCol);
                if (target == null) {
                    moves.add(new Move(from, new Position(targetRow, targetCol), null));
                } else if (target.getColor() != piece.getColor()) {
                    moves.add(new Move(from, new Position(targetRow, targetCol), target));
                }
            }
        }
        return moves;
    }

    private static List<Move> generateBishopMoves(Board board, Position from, Piece piece) {
        return generateSlidingMoves(board, from, piece, BISHOP_DIRECTIONS);
    }

    private static List<Move> generateRookMoves(Board board, Position from, Piece piece) {
        return generateSlidingMoves(board, from, piece, ROOK_DIRECTIONS);
    }

    private static List<Move> generateQueenMoves(Board board, Position from, Piece piece) {
        return generateSlidingMoves(board, from, piece, QUEEN_DIRECTIONS);
    }

    private static List<Move> generateSlidingMoves(Board board, Position from, Piece piece, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        int startRow = from.getRow();
        int startCol = from.getCol();

        for (int[] dir : directions) {
            int r = startRow + dir[0];
            int c = startCol + dir[1];

            while (Position.isValid(r, c)) {
                Piece target = board.getPiece(r, c);
                if (target == null) {
                    moves.add(new Move(from, new Position(r, c), null));
                    r += dir[0];
                    c += dir[1];
                } else {
                    if (target.getColor() != piece.getColor()) {
                        // Capture opponent piece and stop sliding in this direction
                        moves.add(new Move(from, new Position(r, c), target));
                    }
                    // Blocked by friendly piece or stopped after capturing opponent
                    break;
                }
            }
        }
        return moves;
    }

    private static List<Move> generateKingMoves(Board board, Position from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int row = from.getRow();
        int col = from.getCol();

        for (int[] offset : KING_OFFSETS) {
            int targetRow = row + offset[0];
            int targetCol = col + offset[1];

            if (Position.isValid(targetRow, targetCol)) {
                Piece target = board.getPiece(targetRow, targetCol);
                if (target == null) {
                    moves.add(new Move(from, new Position(targetRow, targetCol), null));
                } else if (target.getColor() != piece.getColor()) {
                    moves.add(new Move(from, new Position(targetRow, targetCol), target));
                }
            }
        }
        return moves;
    }
}
