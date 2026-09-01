package com.project1.engine;

public final class FenValidator {

    public static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private FenValidator() {}

    public static boolean isValid(String fen) {
        if (fen == null || fen.isBlank()) {
            return false;
        }

        String[] parts = fen.trim().split("\\s+");
        if (parts.length != 6) {
            return false;
        }

        // 1. Piece placement: 8 ranks separated by '/'
        String[] ranks = parts[0].split("/");
        if (ranks.length != 8) {
            return false;
        }
        for (String rank : ranks) {
            int count = 0;
            for (char c : rank.toCharArray()) {
                if (Character.isDigit(c)) {
                    int num = c - '0';
                    if (num < 1 || num > 8) return false;
                    count += num;
                } else if ("pnbrqkPNBRQK".indexOf(c) != -1) {
                    count += 1;
                } else {
                    return false;
                }
            }
            if (count != 8) {
                return false;
            }
        }

        // 2. Active color ('w' or 'b')
        if (!"w".equals(parts[1]) && !"b".equals(parts[1])) {
            return false;
        }

        // 3. Castling availability ('-' or any valid combination of K, Q, k, q)
        if (!parts[2].matches("^([KQkq]+|-)$")) {
            return false;
        }

        // 4. En passant target square ('-' or algebraic coordinate)
        if (!parts[3].matches("^(-|[a-h][1-8])$")) {
            return false;
        }

        // 5. Halfmove clock
        try {
            int halfmove = Integer.parseInt(parts[4]);
            if (halfmove < 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        // 6. Fullmove number
        try {
            int fullmove = Integer.parseInt(parts[5]);
            if (fullmove < 1) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static void validate(String fen) {
        if (!isValid(fen)) {
            throw new IllegalArgumentException("Invalid FEN string: " + fen);
        }
    }
}
