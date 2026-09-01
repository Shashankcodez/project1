package com.project1.dto;

public class EvaluationResponse {

    private Double evaluation;
    private Integer mate;
    private String bestMove;
    private int depth;

    public EvaluationResponse() {}

    public EvaluationResponse(Double evaluation, Integer mate, String bestMove, int depth) {
        this.evaluation = evaluation;
        this.mate = mate;
        this.bestMove = bestMove;
        this.depth = depth;
    }

    public Double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Double evaluation) {
        this.evaluation = evaluation;
    }

    public Integer getMate() {
        return mate;
    }

    public void setMate(Integer mate) {
        this.mate = mate;
    }

    public String getBestMove() {
        return bestMove;
    }

    public void setBestMove(String bestMove) {
        this.bestMove = bestMove;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
