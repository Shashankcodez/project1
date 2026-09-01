package com.project1.service;

import com.project1.dto.EvaluationResponse;
import com.project1.dto.GameAnalysisResponse;
import com.project1.engine.EvaluationResult;
import com.project1.engine.StockfishEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameAnalysisServiceTest {

    @Mock
    private StockfishEngine stockfishEngine;

    private GameAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new GameAnalysisService(stockfishEngine);
    }

    @Test
    @DisplayName("evaluateFen delegates to StockfishEngine and normalizes to White perspective")
    void testEvaluateFen() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        when(stockfishEngine.getDefaultDepth()).thenReturn(15);
        when(stockfishEngine.evaluate(fen, 15)).thenReturn(new EvaluationResult("e7e5", -25, null, 15));

        EvaluationResponse response = analysisService.evaluateFen(fen, null);
        assertNotNull(response);
        assertEquals(0.25, response.getEvaluation(), 0.001);
        assertNull(response.getMate());
        assertEquals("e7e5", response.getBestMove());
        assertEquals(15, response.getDepth());
    }

    @Test
    @DisplayName("evaluateFen handles forced mate result normalized to White perspective")
    void testEvaluateFenMate() {
        String fen = "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4";
        when(stockfishEngine.evaluate(fen, 12)).thenReturn(new EvaluationResult("h5f7", null, 1, 12));

        EvaluationResponse response = analysisService.evaluateFen(fen, 12);
        assertNotNull(response);
        assertNull(response.getEvaluation());
        assertEquals(1, response.getMate());
        assertEquals("h5f7", response.getBestMove());
        assertEquals(12, response.getDepth());
    }

    @Test
    @DisplayName("analyzePgn replays game and computes evaluation changes normalized to White perspective")
    void testAnalyzePgnWithEvaluationDeltas() {
        String pgn = "1. e4 e5 2. Nf3";
        when(stockfishEngine.getDefaultDepth()).thenReturn(10);
        when(stockfishEngine.evaluate(anyString(), anyInt()))
                .thenReturn(new EvaluationResult("e7e5", -20, null, 10))   // pos 1 (Black to move, SF: -0.20 -> White: +0.20)
                .thenReturn(new EvaluationResult("g1f3", 15, null, 10))    // pos 2 (White to move, SF: +0.15 -> White: +0.15)
                .thenReturn(new EvaluationResult("b8c6", -45, null, 10));  // pos 3 (Black to move, SF: -0.45 -> White: +0.45)

        GameAnalysisResponse response = analysisService.analyzePgn(pgn, null);
        assertNotNull(response);
        assertEquals(3, response.getPositions().size());

        // Move 1 (White e4)
        assertEquals(1, response.getPositions().get(0).getMoveNumber());
        assertEquals("WHITE", response.getPositions().get(0).getColor());
        assertEquals("e4", response.getPositions().get(0).getMove());
        assertEquals(0.20, response.getPositions().get(0).getEvaluation(), 0.001);
        assertNull(response.getPositions().get(0).getEvaluationChange());

        // Move 1 (Black e5)
        assertEquals(1, response.getPositions().get(1).getMoveNumber());
        assertEquals("BLACK", response.getPositions().get(1).getColor());
        assertEquals("e5", response.getPositions().get(1).getMove());
        assertEquals(0.15, response.getPositions().get(1).getEvaluation(), 0.001);
        assertEquals(-0.05, response.getPositions().get(1).getEvaluationChange(), 0.001);

        // Move 2 (White Nf3)
        assertEquals(2, response.getPositions().get(2).getMoveNumber());
        assertEquals("WHITE", response.getPositions().get(2).getColor());
        assertEquals("Nf3", response.getPositions().get(2).getMove());
        assertEquals(0.45, response.getPositions().get(2).getEvaluation(), 0.001);
        assertEquals(0.30, response.getPositions().get(2).getEvaluationChange(), 0.001);
    }

    @Test
    @DisplayName("Validates input parameters")
    void testInputValidation() {
        assertThrows(IllegalArgumentException.class, () -> analysisService.evaluateFen(null, 10));
        assertThrows(IllegalArgumentException.class, () -> analysisService.evaluateFen("invalid-fen", 10));
        assertThrows(IllegalArgumentException.class, () -> analysisService.analyzePgn(null, 10));
        assertThrows(IllegalArgumentException.class, () -> analysisService.analyzePgn("", 10));
    }
}
