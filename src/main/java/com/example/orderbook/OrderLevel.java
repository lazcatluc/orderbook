package com.example.orderbook;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class OrderLevel {

    private final char side;
    private final double price;
    private long totalSize;
    private final Map<Long, Long> orderSizes = new LinkedHashMap<>();

    OrderLevel(char side, double price) {
        this.side = side;
        this.price = price;
    }

    synchronized void addOrder(Order order) {
        Long previousSize = orderSizes.put(order.getId(), order.getSize());
        totalSize += order.getSize() - Optional.ofNullable(previousSize).orElse(0L);
    }

    synchronized void deleteOrder(long orderId) {
        totalSize -= Optional.ofNullable(orderSizes.remove(orderId)).orElse(0L);
    }

    synchronized void resize(long orderId, long newSize) {
        orderSizes.computeIfPresent(orderId, (k, oldSize) -> {
            totalSize = totalSize - oldSize + newSize;
            return newSize;
        });
    }

    synchronized long getTotalSize() {
        return totalSize;
    }

    synchronized List<Order> getOrders() {
        return orderSizes.entrySet().stream().map(entry ->
                new Order(entry.getKey(), price, side, entry.getValue())).collect(Collectors.toList());
    }

    double getPrice() {
        return price;
    }
}
