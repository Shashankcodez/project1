package com.project1.dto;

public class AnalyzedPosition {

    private int moveNumber;
    private String color;
    private String move;
    private String fen;
    private Double evaluation;
    private Double evaluationChange;
    private Integer mate;
    private String bestMove;
    private int depth;

    public AnalyzedPosition() {}

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public Double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Double evaluation) {
        this.evaluation = evaluation;
    }

    public Double getEvaluationChange() {
        return evaluationChange;
    }

    public void setEvaluationChange(Double evaluationChange) {
        this.evaluationChange = evaluationChange;
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
