package com.project1.engine;

public class StockfishException extends RuntimeException {

    public StockfishException(String message) {
        super(message);
    }

    public StockfishException(String message, Throwable cause) {
        super(message, cause);
    }
}
