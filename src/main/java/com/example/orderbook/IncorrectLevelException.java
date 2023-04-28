package com.example.orderbook;

public class IncorrectLevelException extends RuntimeException {

    public IncorrectLevelException(int level) {
        super("Incorrect level '" + level + "'. A positive value is expected.");
    }
}
