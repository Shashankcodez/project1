package com.project1.dto;

public class EvaluationRequest {

    private String fen;
    private Integer depth;

    public EvaluationRequest() {}

    public EvaluationRequest(String fen) {
        this(fen, null);
    }

    public EvaluationRequest(String fen, Integer depth) {
        this.fen = fen;
        this.depth = depth;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
