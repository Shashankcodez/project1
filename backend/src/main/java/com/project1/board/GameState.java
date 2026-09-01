package com.project1.board;

import java.util.List;

public class GameState {

    private final Board board;
    private Color activeColor;
    private String castlingRights;
    private String enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    public GameState() {
        this.board = new Board();
        this.board.initializeStandardPosition();
        this.activeColor = Color.WHITE;
        this.castlingRights = "KQkq";
        this.enPassantSquare = "-";
        this.halfmoveClock = 0;
        this.fullmoveNumber = 1;
    }

    public Board getBoard() {
        return board;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public String getCastlingRights() {
        return castlingRights;
    }

    public String getEnPassantSquare() {
        return enPassantSquare;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public String toFen() {
        return FenBuilder.toFen(board, activeColor, castlingRights, enPassantSquare, halfmoveClock, fullmoveNumber);
    }

    public void applySanMove(String rawSan) {
        if (rawSan == null || rawSan.isBlank()) {
            throw new IllegalArgumentException("Move cannot be empty");
        }

        String san = rawSan.replaceAll("[+#?!]+$", "").trim();

        // 1. Kingside Castling
        if (san.equals("O-O") || san.equals("0-0")) {
            applyKingsideCastle();
            return;
        }

        // 2. Queenside Castling
        if (san.equals("O-O-O") || san.equals("0-0-0")) {
            applyQueensideCastle();
            return;
        }

        // 3. Pawn Promotion
        PieceType promotionType = null;
        if (san.contains("=")) {
            int eqIdx = san.indexOf('=');
            char promChar = san.charAt(eqIdx + 1);
            promotionType = switch (Character.toUpperCase(promChar)) {
                case 'R' -> PieceType.ROOK;
                case 'B' -> PieceType.BISHOP;
                case 'N' -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            };
            san = san.substring(0, eqIdx);
        }

        // 4. Standard / Disambiguated / Capture moves
        if (san.length() < 2) {
            throw new IllegalArgumentException("Invalid move notation: " + rawSan);
        }

        String destStr = san.substring(san.length() - 2);
        Position destPos = Position.fromAlgebraic(destStr);

        PieceType movingPieceType;
        char firstChar = san.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            movingPieceType = switch (firstChar) {
                case 'N' -> PieceType.KNIGHT;
                case 'B' -> PieceType.BISHOP;
                case 'R' -> PieceType.ROOK;
                case 'Q' -> PieceType.QUEEN;
                case 'K' -> PieceType.KING;
                default -> throw new IllegalArgumentException("Unknown piece type in move: " + rawSan);
            };
        } else {
            movingPieceType = PieceType.PAWN;
        }

        String prefix = san.substring(0, san.length() - 2).replace("x", "");
        if (movingPieceType != PieceType.PAWN && !prefix.isEmpty()) {
            prefix = prefix.substring(1);
        }

        Character fileDisambiguation = null;
        Integer rankDisambiguation = null;
        for (char c : prefix.toCharArray()) {
            if (c >= 'a' && c <= 'h') {
                fileDisambiguation = c;
            } else if (c >= '1' && c <= '8') {
                rankDisambiguation = c - '0';
            }
        }

        List<Move> candidateMoves = MoveGenerator.generateAllMoves(board, activeColor);
        Move matchedMove = null;

        for (Move move : candidateMoves) {
            if (!move.getTo().equals(destPos)) {
                continue;
            }
            Piece piece = board.getPiece(move.getFrom());
            if (piece == null || piece.getPieceType() != movingPieceType) {
                continue;
            }
            if (fileDisambiguation != null && move.getFrom().getFile() != fileDisambiguation) {
                continue;
            }
            if (rankDisambiguation != null && move.getFrom().getRank() != rankDisambiguation) {
                continue;
            }
            matchedMove = move;
            break;
        }

        // Check for en-passant if no standard pseudo-legal pawn move was generated
        if (matchedMove == null && movingPieceType == PieceType.PAWN && destStr.equalsIgnoreCase(enPassantSquare)) {
            int targetCol = destPos.getCol();
            int fromRow = (activeColor == Color.WHITE) ? 3 : 4;
            if (fileDisambiguation != null) {
                int fromCol = fileDisambiguation - 'a';
                if (Math.abs(fromCol - targetCol) == 1) {
                    Piece p = board.getPiece(fromRow, fromCol);
                    if (p != null && p.getColor() == activeColor && p.getPieceType() == PieceType.PAWN) {
                        matchedMove = new Move(new Position(fromRow, fromCol), destPos);
                    }
                }
            }
        }

        if (matchedMove == null) {
            throw new IllegalArgumentException("Illegal or ambiguous move in game: " + rawSan);
        }

        executeMove(matchedMove, promotionType);
    }

    private void executeMove(Move move, PieceType promotionType) {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece movingPiece = board.getPiece(from);
        Piece targetPiece = board.getPiece(to);

        boolean isPawnMove = movingPiece.getPieceType() == PieceType.PAWN;
        boolean isCapture = targetPiece != null;

        // Handle En Passant Capture
        if (isPawnMove && to.toAlgebraic().equalsIgnoreCase(enPassantSquare) && targetPiece == null) {
            int capturedPawnRow = (activeColor == Color.WHITE) ? to.getRow() + 1 : to.getRow() - 1;
            board.setPiece(capturedPawnRow, to.getCol(), null);
            isCapture = true;
        }

        board.setPiece(from, null);
        Piece pieceToPlace = (promotionType != null) ? new Piece(activeColor, promotionType) : movingPiece;
        board.setPiece(to, pieceToPlace);

        // Update En Passant Square
        if (isPawnMove && Math.abs(from.getRow() - to.getRow()) == 2) {
            int epRow = (from.getRow() + to.getRow()) / 2;
            enPassantSquare = Position.fromAlgebraic("" + to.getFile() + (8 - epRow)).toAlgebraic();
        } else {
            enPassantSquare = "-";
        }

        // Update Castling Rights
        if (movingPiece.getPieceType() == PieceType.KING) {
            if (activeColor == Color.WHITE) {
                castlingRights = castlingRights.replace("K", "").replace("Q", "");
            } else {
                castlingRights = castlingRights.replace("k", "").replace("q", "");
            }
        } else if (movingPiece.getPieceType() == PieceType.ROOK) {
            if (from.equals(Position.fromAlgebraic("a1"))) castlingRights = castlingRights.replace("Q", "");
            if (from.equals(Position.fromAlgebraic("h1"))) castlingRights = castlingRights.replace("K", "");
            if (from.equals(Position.fromAlgebraic("a8"))) castlingRights = castlingRights.replace("q", "");
            if (from.equals(Position.fromAlgebraic("h8"))) castlingRights = castlingRights.replace("k", "");
        }
        if (to.equals(Position.fromAlgebraic("a1"))) castlingRights = castlingRights.replace("Q", "");
        if (to.equals(Position.fromAlgebraic("h1"))) castlingRights = castlingRights.replace("K", "");
        if (to.equals(Position.fromAlgebraic("a8"))) castlingRights = castlingRights.replace("q", "");
        if (to.equals(Position.fromAlgebraic("h8"))) castlingRights = castlingRights.replace("k", "");

        if (castlingRights.isEmpty()) {
            castlingRights = "-";
        }

        // Update Halfmove Clock
        if (isPawnMove || isCapture) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }

        // Switch turn
        if (activeColor == Color.BLACK) {
            fullmoveNumber++;
            activeColor = Color.WHITE;
        } else {
            activeColor = Color.BLACK;
        }
    }

    private void applyKingsideCastle() {
        if (activeColor == Color.WHITE) {
            Piece king = board.getPiece(Position.fromAlgebraic("e1"));
            Piece rook = board.getPiece(Position.fromAlgebraic("h1"));
            if (!castlingRights.contains("K")
                    || king == null || king.getColor() != Color.WHITE || king.getPieceType() != PieceType.KING
                    || rook == null || rook.getColor() != Color.WHITE || rook.getPieceType() != PieceType.ROOK
                    || board.getPiece(Position.fromAlgebraic("f1")) != null
                    || board.getPiece(Position.fromAlgebraic("g1")) != null) {
                throw new IllegalArgumentException("Illegal kingside castling for White");
            }
            board.setPiece(Position.fromAlgebraic("e1"), null);
            board.setPiece(Position.fromAlgebraic("h1"), null);
            board.setPiece(Position.fromAlgebraic("g1"), new Piece(Color.WHITE, PieceType.KING));
            board.setPiece(Position.fromAlgebraic("f1"), new Piece(Color.WHITE, PieceType.ROOK));
            castlingRights = castlingRights.replace("K", "").replace("Q", "");
        } else {
            Piece king = board.getPiece(Position.fromAlgebraic("e8"));
            Piece rook = board.getPiece(Position.fromAlgebraic("h8"));
            if (!castlingRights.contains("k")
                    || king == null || king.getColor() != Color.BLACK || king.getPieceType() != PieceType.KING
                    || rook == null || rook.getColor() != Color.BLACK || rook.getPieceType() != PieceType.ROOK
                    || board.getPiece(Position.fromAlgebraic("f8")) != null
                    || board.getPiece(Position.fromAlgebraic("g8")) != null) {
                throw new IllegalArgumentException("Illegal kingside castling for Black");
            }
            board.setPiece(Position.fromAlgebraic("e8"), null);
            board.setPiece(Position.fromAlgebraic("h8"), null);
            board.setPiece(Position.fromAlgebraic("g8"), new Piece(Color.BLACK, PieceType.KING));
            board.setPiece(Position.fromAlgebraic("f8"), new Piece(Color.BLACK, PieceType.ROOK));
            castlingRights = castlingRights.replace("k", "").replace("q", "");
        }
        if (castlingRights.isEmpty()) castlingRights = "-";
        enPassantSquare = "-";
        halfmoveClock++;

        if (activeColor == Color.BLACK) {
            fullmoveNumber++;
            activeColor = Color.WHITE;
        } else {
            activeColor = Color.BLACK;
        }
    }

    private void applyQueensideCastle() {
        if (activeColor == Color.WHITE) {
            Piece king = board.getPiece(Position.fromAlgebraic("e1"));
            Piece rook = board.getPiece(Position.fromAlgebraic("a1"));
            if (!castlingRights.contains("Q")
                    || king == null || king.getColor() != Color.WHITE || king.getPieceType() != PieceType.KING
                    || rook == null || rook.getColor() != Color.WHITE || rook.getPieceType() != PieceType.ROOK
                    || board.getPiece(Position.fromAlgebraic("b1")) != null
                    || board.getPiece(Position.fromAlgebraic("c1")) != null
                    || board.getPiece(Position.fromAlgebraic("d1")) != null) {
                throw new IllegalArgumentException("Illegal queenside castling for White");
            }
            board.setPiece(Position.fromAlgebraic("e1"), null);
            board.setPiece(Position.fromAlgebraic("a1"), null);
            board.setPiece(Position.fromAlgebraic("c1"), new Piece(Color.WHITE, PieceType.KING));
            board.setPiece(Position.fromAlgebraic("d1"), new Piece(Color.WHITE, PieceType.ROOK));
            castlingRights = castlingRights.replace("K", "").replace("Q", "");
        } else {
            Piece king = board.getPiece(Position.fromAlgebraic("e8"));
            Piece rook = board.getPiece(Position.fromAlgebraic("a8"));
            if (!castlingRights.contains("q")
                    || king == null || king.getColor() != Color.BLACK || king.getPieceType() != PieceType.KING
                    || rook == null || rook.getColor() != Color.BLACK || rook.getPieceType() != PieceType.ROOK
                    || board.getPiece(Position.fromAlgebraic("b8")) != null
                    || board.getPiece(Position.fromAlgebraic("c8")) != null
                    || board.getPiece(Position.fromAlgebraic("d8")) != null) {
                throw new IllegalArgumentException("Illegal queenside castling for Black");
            }
            board.setPiece(Position.fromAlgebraic("e8"), null);
            board.setPiece(Position.fromAlgebraic("a8"), null);
            board.setPiece(Position.fromAlgebraic("c8"), new Piece(Color.BLACK, PieceType.KING));
            board.setPiece(Position.fromAlgebraic("d8"), new Piece(Color.BLACK, PieceType.ROOK));
            castlingRights = castlingRights.replace("k", "").replace("q", "");
        }
        if (castlingRights.isEmpty()) castlingRights = "-";
        enPassantSquare = "-";
        halfmoveClock++;

        if (activeColor == Color.BLACK) {
            fullmoveNumber++;
            activeColor = Color.WHITE;
        } else {
            activeColor = Color.BLACK;
        }
    }
}
