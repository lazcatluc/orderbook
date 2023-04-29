package com.example.orderbook;

class IncorrectLevelException extends RuntimeException {

    IncorrectLevelException(int level) {
        super("Incorrect level '" + level + "'. A positive value is expected.");
    }
}
