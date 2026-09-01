package com.project1.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockfishEngineTest {

    @Test
    @DisplayName("Parses standard UCI centipawn evaluation and bestmove")
    void testParseCentipawnOutput() {
        List<String> output = List.of(
                "Stockfish 16 by the Stockfish developers",
                "info depth 1 seldepth 1 multipv 1 score cp 20 nodes 20 nps 20000 tbhits 0 time 1 pv e2e4",
                "info depth 10 seldepth 14 multipv 1 score cp 35 nodes 45000 nps 1500000 tbhits 0 time 30 pv e2e4 e7e5 g1f3",
                "info depth 15 seldepth 22 multipv 1 score cp 42 nodes 152345 nps 2304910 tbhits 0 time 66 pv e2e4 e7e5 g1f3 b8c6",
                "bestmove e2e4 ponder e7e5"
        );

        EvaluationResult result = StockfishEngine.parseUciOutput(output);
        assertNotNull(result);
        assertEquals("e2e4", result.getBestMove());
        assertEquals(42, result.getCentipawns());
        assertNull(result.getMate());
        assertEquals(15, result.getDepth());
        assertFalse(result.isMate());
        assertEquals(0.42, result.getPawnScore(), 0.001);
    }

    @Test
    @DisplayName("Parses negative centipawn evaluation correctly")
    void testParseNegativeCentipawnOutput() {
        List<String> output = List.of(
                "info depth 12 seldepth 18 multipv 1 score cp -150 nodes 89000 nps 1800000 time 49 pv d7d5 d2d4",
                "bestmove d7d5 ponder d2d4"
        );

        EvaluationResult result = StockfishEngine.parseUciOutput(output);
        assertEquals("d7d5", result.getBestMove());
        assertEquals(-150, result.getCentipawns());
        assertNull(result.getMate());
        assertEquals(12, result.getDepth());
        assertEquals(-1.50, result.getPawnScore(), 0.001);
    }

    @Test
    @DisplayName("Parses positive and negative forced mate evaluations")
    void testParseMateOutput() {
        // Positive mate (winning in 2 moves)
        List<String> mateIn2Output = List.of(
                "info depth 8 seldepth 8 multipv 1 score mate 2 nodes 1200 time 3 pv d1h5 g7g6 h5e5",
                "bestmove d1h5"
        );
        EvaluationResult mateIn2 = StockfishEngine.parseUciOutput(mateIn2Output);
        assertEquals("d1h5", mateIn2.getBestMove());
        assertNull(mateIn2.getCentipawns());
        assertEquals(2, mateIn2.getMate());
        assertTrue(mateIn2.isMate());
        assertEquals(8, mateIn2.getDepth());

        // Negative mate (getting mated in 1 move)
        List<String> matedIn1Output = List.of(
                "info depth 6 seldepth 6 multipv 1 score mate -1 nodes 800 time 2 pv g8h8 f7f8",
                "bestmove g8h8"
        );
        EvaluationResult matedIn1 = StockfishEngine.parseUciOutput(matedIn1Output);
        assertEquals("g8h8", matedIn1.getBestMove());
        assertNull(matedIn1.getCentipawns());
        assertEquals(-1, matedIn1.getMate());
        assertTrue(matedIn1.isMate());
    }

    @Test
    @DisplayName("Throws StockfishException when output lacks bestmove or is empty")
    void testParseInvalidOutput() {
        assertThrows(StockfishException.class, () -> StockfishEngine.parseUciOutput(null));
        assertThrows(StockfishException.class, () -> StockfishEngine.parseUciOutput(List.of()));
        assertThrows(StockfishException.class, () -> StockfishEngine.parseUciOutput(List.of(
                "info depth 10 score cp 50 nodes 1000",
                "info depth 11 score cp 55 nodes 2000"
        )));
    }

    @Test
    @DisplayName("Engine configuration properties and constructors")
    void testEngineConfiguration() {
        StockfishEngine defaultEngine = new StockfishEngine();
        assertEquals("stockfish", defaultEngine.getExecutablePath());
        assertEquals(15, defaultEngine.getDefaultDepth());
        assertFalse(defaultEngine.isAlive());

        StockfishEngine customEngine = new StockfishEngine("custom/path/stockfish.exe", 20);
        assertEquals("custom/path/stockfish.exe", customEngine.getExecutablePath());
        assertEquals(20, customEngine.getDefaultDepth());
        assertFalse(customEngine.isAlive());
    }

    @Test
    @DisplayName("Validates FEN before attempting evaluation")
    void testFenValidationBeforeEvaluation() {
        try (StockfishEngine engine = new StockfishEngine("non_existent_engine")) {
            assertThrows(IllegalArgumentException.class, () -> engine.evaluate("invalid-fen-string"));
            assertThrows(IllegalArgumentException.class, () -> engine.evaluateWithTime("invalid-fen-string", 1000));
        }
    }

    @Test
    @DisplayName("Handles process start failure gracefully when executable does not exist")
    void testProcessStartFailure() {
        try (StockfishEngine engine = new StockfishEngine("invalid_path_to_stockfish_executable_12345")) {
            StockfishException exception = assertThrows(StockfishException.class,
                    () -> engine.evaluate(FenValidator.STARTING_FEN));
            assertTrue(exception.getMessage().contains("Failed to start Stockfish process"));
        }
    }

    @Test
    @DisplayName("Live Stockfish process evaluation test on standard position and mate in 1")
    void testLiveStockfishEvaluation() {
        java.io.File stockfishExe = new java.io.File("tools/stockfish/stockfish.exe");
        if (!stockfishExe.exists()) {
            stockfishExe = new java.io.File("backend/tools/stockfish/stockfish.exe");
        }
        if (stockfishExe.exists()) {
            try (StockfishEngine engine = new StockfishEngine(stockfishExe.getAbsolutePath(), 10)) {
                EvaluationResult result = engine.evaluate(FenValidator.STARTING_FEN, 10);
                assertNotNull(result);
                assertNotNull(result.getBestMove());
                assertNotNull(result.getCentipawns());
                assertTrue(result.getDepth() >= 10);

                // Mate in 1 position (Scholar's mate: Qxf7#)
                String mateIn1Fen = "r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4";
                EvaluationResult mateResult = engine.evaluate(mateIn1Fen, 10);
                assertNotNull(mateResult);
                assertEquals("h5f7", mateResult.getBestMove());
                assertTrue(mateResult.isMate());
                assertEquals(1, mateResult.getMate());
            }
        }
    }
}
