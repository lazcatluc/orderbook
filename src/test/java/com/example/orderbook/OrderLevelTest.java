package com.example.orderbook;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class OrderLevelTest {
    private final char side = 'O';
    private final double price = 1.0;
    @Test
    void resizeShouldNotAffectTimePriority() {
        OrderLevel orderLevel = new OrderLevel(side, price);
        orderLevel.addOrder(new Order(1, price, side, 1));
        orderLevel.addOrder(new Order(2, price, side, 1));
        orderLevel.addOrder(new Order(3, price, side, 1));
        orderLevel.addOrder(new Order(4, price, side, 1));
        orderLevel.addOrder(new Order(5, price, side, 1));
        orderLevel.resize(2, 3);
        orderLevel.resize(5, 4);
        orderLevel.resize(2, 1);

        assertThat(orderLevel.getOrders().stream().map(Order::getId).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    void deleteAndReAddShouldAffectTimePriority() {
        OrderLevel orderLevel = new OrderLevel(side, price);
        orderLevel.addOrder(new Order(1, price, side, 1));
        orderLevel.addOrder(new Order(2, price, side, 1));
        orderLevel.addOrder(new Order(3, price, side, 1));
        orderLevel.addOrder(new Order(4, price, side, 1));
        orderLevel.addOrder(new Order(5, price, side, 1));
        orderLevel.deleteOrder(2);
        orderLevel.deleteOrder(5);
        orderLevel.addOrder(new Order(2, price, side, 1));
        orderLevel.addOrder(new Order(5, price, side, 1));

        assertThat(orderLevel.getOrders().stream().map(Order::getId).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList(1L, 3L, 4L, 2L, 5L));
    }
}