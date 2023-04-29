package com.example.orderbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class OrderPriceTimePrioritizer {
    private final char side;

    private final Map<Double, OrderLevel> orders;

    private final Map<Long, OrderLevel> orderLevels;

    private OrderPriceTimePrioritizer(char side, Comparator<Double> priceComparator) {
        this.side = side;
        this.orders = new TreeMap<>(priceComparator);
        this.orderLevels = new ConcurrentHashMap<>();
    }

    public static OrderPriceTimePrioritizer bids() {
        return new OrderPriceTimePrioritizer('B', Comparator.reverseOrder());
    }

    public static OrderPriceTimePrioritizer offers() {
        return new OrderPriceTimePrioritizer('O', Comparator.naturalOrder());
    }

    public void addOrder(Order order) {
        OrderLevel orderLevel;
        synchronized (orders) {
            orderLevel = orders.computeIfAbsent(order.getPrice(), price -> new OrderLevel(side, price));
        }
        orderLevel.addOrder(order);
        orderLevels.put(order.getId(), orderLevel);
    }

    public void deleteOrder(long orderId) {
        OrderLevel orderLevel = orderLevels.remove(orderId);
        if (orderLevel == null) {
            return;
        }
        orderLevel.deleteOrder(orderId);
    }

    public void resizeOrder(long orderId, long newSize) {
        Optional.ofNullable(orderLevels.get(orderId)).ifPresent(orderLevel -> orderLevel.resize(orderId, newSize));
    }

    public List<Order> getOrders() {
        List<OrderLevel> orderLevels;
        synchronized (orders) {
            Collection<OrderLevel> values = orders.values();
            values.removeIf(e -> e.getTotalSize() == 0);
            orderLevels = new ArrayList<>(values);
        }
        return orderLevels.stream().flatMap(orderLevel -> orderLevel.getOrders().stream()).collect(Collectors.toList());
    }

    public Optional<Double> getLevelPrice(int level) {
        synchronized (orders) {
            Iterator<OrderLevel> values = orders.values().iterator();
            int myLevel = 0;
            while (values.hasNext()) {
                OrderLevel orderLevel = values.next();
                if (orderLevel.getTotalSize() == 0) {
                    values.remove();
                } else {
                    myLevel++;
                    if (myLevel == level) {
                        return Optional.of(orderLevel.getPrice());
                    }
                }
            }
        }
        return Optional.empty();
    }

    public long getLevelTotalSize(int level) {
        synchronized (orders) {
            Iterator<OrderLevel> values = orders.values().iterator();
            int myLevel = 0;
            while (values.hasNext()) {
                long currentSize = values.next().getTotalSize();
                if (currentSize == 0) {
                    values.remove();
                } else {
                    myLevel++;
                    if (myLevel == level) {
                        return currentSize;
                    }
                }
            }
        }
        return 0;
    }

}
