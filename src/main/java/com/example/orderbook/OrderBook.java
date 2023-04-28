package com.example.orderbook;

import java.util.List;
import java.util.Optional;

public class OrderBook {
    private final OrderPriceTimePrioritizer bids = OrderPriceTimePrioritizer.bids();
    private final OrderPriceTimePrioritizer offers = OrderPriceTimePrioritizer.offers();

    public void addOrder(Order order) {
        if (order.getSize() <= 0) {
            throw new IncorrectOrderSizeException(order.getSize());
        }
        onSide(order.getSide()).addOrder(order);
    }

    public void deleteOrder(long orderId) {
        bids.deleteOrder(orderId);
        offers.deleteOrder(orderId);
    }

    public void resizeOrder(long orderId, long newSize) {
        if (newSize <= 0) {
            throw new IncorrectOrderSizeException(newSize);
        }
        bids.resizeOrder(orderId, newSize);
        offers.resizeOrder(orderId, newSize);
    }

    public Optional<Double> getLevelPrice(char side, int level) {
        if (level <= 0) {
            throw new IncorrectLevelException(level);
        }
        return onSide(side).getLevelPrice(level);
    }

    public long getLevelTotalSize(char side, int level) {
        if (level <= 0) {
            throw new IncorrectLevelException(level);
        }
        return onSide(side).getLevelTotalSize(level);
    }

    public List<Order> getOrders(char side) {
        return onSide(side).getOrders();
    }

    private OrderPriceTimePrioritizer onSide(char side) {
        return switch (side) {
            case 'B' -> bids;
            case 'O' -> offers;
            default -> throw new UnknownSideException(side);
        };
    }
}
