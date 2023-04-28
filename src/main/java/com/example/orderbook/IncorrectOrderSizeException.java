package com.example.orderbook;

public class IncorrectOrderSizeException extends RuntimeException {
    public IncorrectOrderSizeException(long size) {
        super("Incorrect size '" + size + "'. A positive value is expected");
    }
}
