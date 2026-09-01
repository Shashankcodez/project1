package com.project1.dto;

import java.util.ArrayList;
import java.util.List;

public class GameAnalysisResponse {

    private List<AnalyzedPosition> positions;

    public GameAnalysisResponse() {
        this.positions = new ArrayList<>();
    }

    public GameAnalysisResponse(List<AnalyzedPosition> positions) {
        this.positions = positions;
    }

    public List<AnalyzedPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<AnalyzedPosition> positions) {
        this.positions = positions;
    }
}
