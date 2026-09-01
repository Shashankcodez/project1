package com.project1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project1.dto.AnalyzedPosition;
import com.project1.dto.EvaluationRequest;
import com.project1.dto.EvaluationResponse;
import com.project1.dto.GameAnalysisResponse;
import com.project1.dto.PgnAnalysisRequest;
import com.project1.engine.StockfishException;
import com.project1.service.GameAnalysisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationController.class)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameAnalysisService analysisService;

    @Test
    @DisplayName("POST /api/evaluate returns 200 OK with valid evaluation")
    void testEvaluateSuccess() throws Exception {
        EvaluationRequest request = new EvaluationRequest("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", 15);
        EvaluationResponse response = new EvaluationResponse(0.25, null, "e7e5", 15);

        when(analysisService.evaluateFen(eq(request.getFen()), eq(15))).thenReturn(response);

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluation").value(0.25))
                .andExpect(jsonPath("$.bestMove").value("e7e5"))
                .andExpect(jsonPath("$.depth").value(15));
    }

    @Test
    @DisplayName("POST /api/evaluate returns 400 Bad Request on invalid FEN")
    void testEvaluateInvalidFen() throws Exception {
        EvaluationRequest request = new EvaluationRequest("invalid-fen", 10);
        when(analysisService.evaluateFen(eq("invalid-fen"), eq(10)))
                .thenThrow(new IllegalArgumentException("Invalid FEN string: invalid-fen"));

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid FEN string: invalid-fen"));
    }

    @Test
    @DisplayName("POST /api/evaluate returns 400 Bad Request on missing FEN")
    void testEvaluateMissingFen() throws Exception {
        EvaluationRequest request = new EvaluationRequest(null, 10);

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /api/analyze/pgn returns 200 OK with analyzed positions")
    void testAnalyzePgnSuccess() throws Exception {
        PgnAnalysisRequest request = new PgnAnalysisRequest("1. e4 e5", 10);

        AnalyzedPosition pos1 = new AnalyzedPosition();
        pos1.setMoveNumber(1);
        pos1.setColor("WHITE");
        pos1.setMove("e4");
        pos1.setFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        pos1.setEvaluation(0.20);
        pos1.setBestMove("e7e5");
        pos1.setDepth(10);

        GameAnalysisResponse response = new GameAnalysisResponse(List.of(pos1));
        when(analysisService.analyzePgn(eq("1. e4 e5"), eq(10))).thenReturn(response);

        mockMvc.perform(post("/api/analyze/pgn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.positions[0].move").value("e4"))
                .andExpect(jsonPath("$.positions[0].evaluation").value(0.20))
                .andExpect(jsonPath("$.positions[0].bestMove").value("e7e5"));
    }

    @Test
    @DisplayName("POST /api/evaluate returns 500 Internal Server Error when Stockfish fails")
    void testStockfishFailure() throws Exception {
        EvaluationRequest request = new EvaluationRequest("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", 15);
        when(analysisService.evaluateFen(any(), any()))
                .thenThrow(new StockfishException("Stockfish process failed to start"));

        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Chess Engine Error"))
                .andExpect(jsonPath("$.message").value("Stockfish process failed to start"));
    }
}
