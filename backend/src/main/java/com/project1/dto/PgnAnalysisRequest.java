package com.project1.dto;

public class PgnAnalysisRequest {

    private String pgn;
    private Integer depth;

    public PgnAnalysisRequest() {}

    public PgnAnalysisRequest(String pgn) {
        this(pgn, null);
    }

    public PgnAnalysisRequest(String pgn, Integer depth) {
        this.pgn = pgn;
        this.depth = depth;
    }

    public String getPgn() {
        return pgn;
    }

    public void setPgn(String pgn) {
        this.pgn = pgn;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
