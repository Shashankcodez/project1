package com.project1.pgn;

import java.util.ArrayList;
import java.util.List;

public final class PgnParser {

    private PgnParser() {}

    public static List<String> extractMoves(String pgn) {
        if (pgn == null || pgn.isBlank()) {
            throw new IllegalArgumentException("PGN string cannot be null or empty");
        }

        // 1. Remove PGN metadata headers: [Key "Value"]
        String withoutHeaders = pgn.replaceAll("\\[[^\\]]*\\]", " ");

        // 2. Remove comments: { ... } and ; ...
        String withoutComments = withoutHeaders.replaceAll("\\{[^\\}]*\\}", " ")
                                               .replaceAll(";[^\r\n]*", " ");

        // 3. Remove recursive variation annotations: ( ... )
        String withoutVars = withoutComments.replaceAll("\\([^\\)]*\\)", " ");

        // 4. Tokenize
        String[] tokens = withoutVars.trim().split("\\s+");
        List<String> moves = new ArrayList<>();

        for (String token : tokens) {
            String t = token.trim();
            if (t.isEmpty()) continue;

            // Skip move numbers like "1.", "12...", "14."
            if (t.matches("^\\d+\\.+$")) continue;

            // Strip leading move numbers e.g. "1.e4" -> "e4"
            t = t.replaceAll("^\\d+\\.+", "");

            // Skip game results
            if (t.equals("1-0") || t.equals("0-1") || t.equals("1/2-1/2") || t.equals("*")) {
                continue;
            }

            // Skip numeric annotation glyphs e.g. $1, $2
            if (t.startsWith("$")) {
                continue;
            }

            if (!t.isEmpty()) {
                moves.add(t);
            }
        }

        if (moves.isEmpty()) {
            throw new IllegalArgumentException("No valid moves found in PGN");
        }

        return moves;
    }
}
