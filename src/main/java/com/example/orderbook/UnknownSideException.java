package com.example.orderbook;

public class UnknownSideException extends RuntimeException {
    public UnknownSideException(char side) {
        super("Unkown side " + side + ", expected either 'O' for offers or 'B' for bids");
    }
}
