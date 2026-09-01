package com.project1.board;

public final class FenBuilder {

    private FenBuilder() {}

    public static String toFen(Board board, Color activeColor, String castlingRights, String enPassantSquare, int halfmoveClock, int fullmoveNumber) {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount);
                        emptyCount = 0;
                    }
                    sb.append(piece.getSymbol());
                }
            }
            if (emptyCount > 0) {
                sb.append(emptyCount);
            }
            if (row < 7) {
                sb.append('/');
            }
        }

        sb.append(' ').append(activeColor == Color.WHITE ? 'w' : 'b');
        sb.append(' ').append((castlingRights != null && !castlingRights.isBlank()) ? castlingRights : "-");
        sb.append(' ').append((enPassantSquare != null && !enPassantSquare.isBlank()) ? enPassantSquare : "-");
        sb.append(' ').append(halfmoveClock);
        sb.append(' ').append(fullmoveNumber);

        return sb.toString();
    }
}
