package com.project1.engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockfishEngine implements AutoCloseable {

    private final String executablePath;
    private final int defaultDepth;

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public StockfishEngine() {
        this("stockfish", 15);
    }

    public StockfishEngine(@Value("${stockfish.path:stockfish}") String executablePath) {
        this(executablePath, 15);
    }

    public StockfishEngine(
            @Value("${stockfish.path:stockfish}") String executablePath,
            @Value("${stockfish.default-depth:15}") int defaultDepth) {
        this.executablePath = executablePath;
        this.defaultDepth = defaultDepth;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    public int getDefaultDepth() {
        return defaultDepth;
    }

    public synchronized boolean isAlive() {
        return process != null && process.isAlive();
    }

    public synchronized void start() {
        if (isAlive()) {
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(executablePath);
            pb.redirectErrorStream(true);
            process = pb.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            sendCommand("uci");
            waitForResponse("uciok", 5000);
            sendCommand("isready");
            waitForResponse("readyok", 5000);
        } catch (IOException e) {
            stop();
            throw new StockfishException("Failed to start Stockfish process at: " + executablePath, e);
        }
    }

    public synchronized void sendCommand(String command) throws IOException {
        if (!isAlive()) {
            throw new StockfishException("Stockfish process is not running");
        }
        writer.write(command);
        writer.newLine();
        writer.flush();
    }

    public synchronized EvaluationResult evaluate(String fen) {
        return evaluate(fen, defaultDepth);
    }

    public synchronized EvaluationResult evaluate(String fen, int depth) {
        FenValidator.validate(fen);
        if (!isAlive()) {
            start();
        }

        try {
            sendCommand("isready");
            waitForResponse("readyok", 5000);

            sendCommand("position fen " + fen);
            sendCommand("go depth " + depth);

            List<String> output = readUntilBestMove();
            return parseUciOutput(output);
        } catch (IOException e) {
            throw new StockfishException("Error communicating with Stockfish during evaluation", e);
        }
    }

    public synchronized EvaluationResult evaluateWithTime(String fen, long movetimeMs) {
        FenValidator.validate(fen);
        if (!isAlive()) {
            start();
        }

        try {
            sendCommand("isready");
            waitForResponse("readyok", 5000);

            sendCommand("position fen " + fen);
            sendCommand("go movetime " + movetimeMs);

            List<String> output = readUntilBestMove();
            return parseUciOutput(output);
        } catch (IOException e) {
            throw new StockfishException("Error communicating with Stockfish during evaluation", e);
        }
    }

    private void waitForResponse(String expected, long timeoutMs) throws IOException {
        long startTime = System.currentTimeMillis();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals(expected) || line.startsWith(expected)) {
                return;
            }
            if ((System.currentTimeMillis() - startTime) > timeoutMs) {
                break;
            }
        }
        throw new StockfishException("Timed out waiting for Stockfish response: " + expected);
    }

    private List<String> readUntilBestMove() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
            if (line.startsWith("bestmove")) {
                break;
            }
        }
        if (line == null) {
            throw new StockfishException("Stockfish process terminated unexpectedly during analysis");
        }
        return lines;
    }

    public static EvaluationResult parseUciOutput(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new StockfishException("Cannot parse empty Stockfish output");
        }

        String bestMove = null;
        Integer centipawns = null;
        Integer mate = null;
        int depth = 0;

        for (String line : lines) {
            if (line.startsWith("info ") && line.contains("score")) {
                String[] tokens = line.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    if ("depth".equals(tokens[i]) && i + 1 < tokens.length) {
                        try {
                            depth = Integer.parseInt(tokens[i + 1]);
                        } catch (NumberFormatException ignored) {}
                    } else if ("score".equals(tokens[i]) && i + 2 < tokens.length) {
                        String scoreType = tokens[i + 1];
                        String scoreVal = tokens[i + 2];
                        try {
                            if ("cp".equals(scoreType)) {
                                centipawns = Integer.parseInt(scoreVal);
                                mate = null;
                            } else if ("mate".equals(scoreType)) {
                                mate = Integer.parseInt(scoreVal);
                                centipawns = null;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } else if (line.startsWith("bestmove")) {
                String[] tokens = line.split("\\s+");
                if (tokens.length > 1) {
                    bestMove = tokens[1];
                }
            }
        }

        if (bestMove == null) {
            throw new StockfishException("No bestmove found in Stockfish output");
        }

        return new EvaluationResult(bestMove, centipawns, mate, depth);
    }

    public synchronized void stop() {
        if (process != null) {
            try {
                if (writer != null) {
                    writer.write("quit");
                    writer.newLine();
                    writer.flush();
                }
            } catch (Exception ignored) {}

            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException ignored) {}

            process.destroy();
            try {
                if (!process.waitFor(1, java.util.concurrent.TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }

            process = null;
            reader = null;
            writer = null;
        }
    }

    @Override
    public void close() {
        stop();
    }
}
