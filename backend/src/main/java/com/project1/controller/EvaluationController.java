package com.project1.controller;

import com.project1.dto.EvaluationRequest;
import com.project1.dto.EvaluationResponse;
import com.project1.dto.GameAnalysisResponse;
import com.project1.dto.PgnAnalysisRequest;
import com.project1.service.GameAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EvaluationController {

    private final GameAnalysisService analysisService;

    @Autowired
    public EvaluationController(GameAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateFen(@RequestBody EvaluationRequest request) {
        if (request == null || request.getFen() == null) {
            throw new IllegalArgumentException("Request body and 'fen' field are required");
        }
        if (request.getDepth() != null && request.getDepth() <= 0) {
            throw new IllegalArgumentException("Analysis depth must be a positive integer");
        }
        EvaluationResponse response = analysisService.evaluateFen(request.getFen(), request.getDepth());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze/pgn")
    public ResponseEntity<GameAnalysisResponse> analyzePgn(@RequestBody PgnAnalysisRequest request) {
        if (request == null || request.getPgn() == null) {
            throw new IllegalArgumentException("Request body and 'pgn' field are required");
        }
        if (request.getDepth() != null && request.getDepth() <= 0) {
            throw new IllegalArgumentException("Analysis depth must be a positive integer");
        }
        GameAnalysisResponse response = analysisService.analyzePgn(request.getPgn(), request.getDepth());
        return ResponseEntity.ok(response);
    }
}
