package com.project1.engine;

import java.util.Objects;

public final class EvaluationResult {

    private final String bestMove;
    private final Integer centipawns;
    private final Integer mate;
    private final int depth;

    public EvaluationResult(String bestMove, Integer centipawns, Integer mate, int depth) {
        this.bestMove = bestMove;
        this.centipawns = centipawns;
        this.mate = mate;
        this.depth = depth;
    }

    public static EvaluationResult ofCentipawns(String bestMove, int centipawns, int depth) {
        return new EvaluationResult(bestMove, centipawns, null, depth);
    }

    public static EvaluationResult ofMate(String bestMove, int mateInMoves, int depth) {
        return new EvaluationResult(bestMove, null, mateInMoves, depth);
    }

    public String getBestMove() {
        return bestMove;
    }

    public Integer getCentipawns() {
        return centipawns;
    }

    public Integer getMate() {
        return mate;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isMate() {
        return mate != null;
    }

    public Double getPawnScore() {
        if (centipawns == null) {
            return null;
        }
        return centipawns / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationResult that)) return false;
        return depth == that.depth &&
                Objects.equals(bestMove, that.bestMove) &&
                Objects.equals(centipawns, that.centipawns) &&
                Objects.equals(mate, that.mate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bestMove, centipawns, mate, depth);
    }

    @Override
    public String toString() {
        if (isMate()) {
            return "EvaluationResult[bestMove=" + bestMove + ", mate=" + mate + ", depth=" + depth + "]";
        }
        return "EvaluationResult[bestMove=" + bestMove + ", cp=" + centipawns + ", depth=" + depth + "]";
    }
}
