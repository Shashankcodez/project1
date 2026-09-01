package com.project1.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationResultTest {

    @Test
    @DisplayName("Centipawn evaluation result formatting and properties")
    void testCentipawnEvaluation() {
        EvaluationResult eval = EvaluationResult.ofCentipawns("e2e4", 45, 12);

        assertEquals("e2e4", eval.getBestMove());
        assertEquals(45, eval.getCentipawns());
        assertNull(eval.getMate());
        assertEquals(12, eval.getDepth());
        assertFalse(eval.isMate());
        assertEquals(0.45, eval.getPawnScore(), 0.001);
        assertEquals("EvaluationResult[bestMove=e2e4, cp=45, depth=12]", eval.toString());
    }

    @Test
    @DisplayName("Mate evaluation result formatting and properties")
    void testMateEvaluation() {
        EvaluationResult mate = EvaluationResult.ofMate("d1h5", 2, 8);

        assertEquals("d1h5", mate.getBestMove());
        assertNull(mate.getCentipawns());
        assertEquals(2, mate.getMate());
        assertEquals(8, mate.getDepth());
        assertTrue(mate.isMate());
        assertNull(mate.getPawnScore());
        assertEquals("EvaluationResult[bestMove=d1h5, mate=2, depth=8]", mate.toString());
    }

    @Test
    @DisplayName("EvaluationResult equals and hashCode contract")
    void testEqualsAndHashCode() {
        EvaluationResult eval1 = new EvaluationResult("e2e4", 30, null, 10);
        EvaluationResult eval2 = new EvaluationResult("e2e4", 30, null, 10);
        EvaluationResult eval3 = new EvaluationResult("d2d4", 30, null, 10);

        assertEquals(eval1, eval2);
        assertEquals(eval1.hashCode(), eval2.hashCode());
        assertNotEquals(eval1, eval3);
    }
}
