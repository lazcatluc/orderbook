package com.example.orderbook;

class IncorrectOrderSizeException extends RuntimeException {
    IncorrectOrderSizeException(long size) {
        super("Incorrect size '" + size + "'. A positive value is expected");
    }
}
