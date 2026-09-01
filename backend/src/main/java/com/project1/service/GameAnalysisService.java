package com.project1.service;

import com.project1.board.Color;
import com.project1.board.GameState;
import com.project1.dto.AnalyzedPosition;
import com.project1.dto.EvaluationResponse;
import com.project1.dto.GameAnalysisResponse;
import com.project1.engine.EvaluationResult;
import com.project1.engine.FenValidator;
import com.project1.engine.StockfishEngine;
import com.project1.pgn.PgnParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameAnalysisService {

    private final StockfishEngine stockfishEngine;

    @Autowired
    public GameAnalysisService(StockfishEngine stockfishEngine) {
        this.stockfishEngine = stockfishEngine;
    }

    public EvaluationResponse evaluateFen(String fen, Integer depth) {
        if (fen == null || fen.isBlank()) {
            throw new IllegalArgumentException("FEN string cannot be null or empty");
        }
        FenValidator.validate(fen);
        int evalDepth = (depth != null && depth > 0) ? depth : stockfishEngine.getDefaultDepth();
        EvaluationResult result = stockfishEngine.evaluate(fen, evalDepth);

        Double score = result.getPawnScore();
        Integer mate = result.getMate();
        String[] parts = fen.trim().split("\\s+");
        if (parts.length > 1 && "b".equals(parts[1])) {
            if (score != null) score = -score;
            if (mate != null) mate = -mate;
        }

        return new EvaluationResponse(score, mate, result.getBestMove(), result.getDepth());
    }

    public GameAnalysisResponse analyzePgn(String pgn, Integer depth) {
        if (pgn == null || pgn.isBlank()) {
            throw new IllegalArgumentException("PGN string cannot be null or empty");
        }

        List<String> moves = PgnParser.extractMoves(pgn);
        int evalDepth = (depth != null && depth > 0) ? depth : stockfishEngine.getDefaultDepth();

        GameState state = new GameState();
        List<AnalyzedPosition> positions = new ArrayList<>();

        Double previousEval = null;

        for (int i = 0; i < moves.size(); i++) {
            String moveStr = moves.get(i);
            int moveNumber = (i / 2) + 1;
            Color moveColor = state.getActiveColor();

            state.applySanMove(moveStr);
            String currentFen = state.toFen();

            EvaluationResult evalResult = stockfishEngine.evaluate(currentFen, evalDepth);
            Double currentEval = evalResult.getPawnScore();
            Integer currentMate = evalResult.getMate();

            if (state.getActiveColor() == Color.BLACK) {
                if (currentEval != null) {
                    currentEval = -currentEval;
                }
                if (currentMate != null) {
                    currentMate = -currentMate;
                }
            }

            Double evalChange = null;
            if (previousEval != null && currentEval != null) {
                evalChange = Math.round((currentEval - previousEval) * 100.0) / 100.0;
            }
            previousEval = currentEval;

            AnalyzedPosition pos = new AnalyzedPosition();
            pos.setMoveNumber(moveNumber);
            pos.setColor(moveColor.name());
            pos.setMove(moveStr);
            pos.setFen(currentFen);
            pos.setEvaluation(currentEval);
            pos.setEvaluationChange(evalChange);
            pos.setMate(currentMate);
            pos.setBestMove(evalResult.getBestMove());
            pos.setDepth(evalResult.getDepth());

            positions.add(pos);
        }

        return new GameAnalysisResponse(positions);
    }
}
