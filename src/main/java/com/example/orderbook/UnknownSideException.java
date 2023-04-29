package com.example.orderbook;

class UnknownSideException extends RuntimeException {
    UnknownSideException(char side) {
        super("Unkown side " + side + ", expected either 'O' for offers or 'B' for bids");
    }
}
