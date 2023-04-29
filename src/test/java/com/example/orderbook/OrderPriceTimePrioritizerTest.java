package com.example.orderbook;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
class OrderPriceTimePrioritizerTest {
    @Test
    void ordersBidsByHighestPriceTime() {
        OrderPriceTimePrioritizer orderPriceTimePrioritizer = OrderPriceTimePrioritizer.bids();
        orderPriceTimePrioritizer.addOrder(new Order(1, 1.0, 'B', 1));
        orderPriceTimePrioritizer.addOrder(new Order(2, 2.0, 'B', 1));
        orderPriceTimePrioritizer.addOrder(new Order(3, 2.0, 'B', 2));
        orderPriceTimePrioritizer.addOrder(new Order(4, 1.0, 'B', 2));
        orderPriceTimePrioritizer.addOrder(new Order(5, 3.0, 'B', 1));

        assertThat(orderPriceTimePrioritizer.getOrders().stream().map(Order::getId).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList(5L, 2L, 3L, 1L, 4L));
    }

    @Test
    void ordersOffersByLowestPriceTime() {
        OrderPriceTimePrioritizer orderPriceTimePrioritizer = OrderPriceTimePrioritizer.offers();
        orderPriceTimePrioritizer.addOrder(new Order(1, 1.0, 'O', 1));
        orderPriceTimePrioritizer.addOrder(new Order(2, 2.0, 'O', 1));
        orderPriceTimePrioritizer.addOrder(new Order(3, 2.0, 'O', 2));
        orderPriceTimePrioritizer.addOrder(new Order(4, 1.0, 'O', 2));
        orderPriceTimePrioritizer.addOrder(new Order(5, 3.0, 'O', 1));

        assertThat(orderPriceTimePrioritizer.getOrders().stream().map(Order::getId).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList(1L, 4L, 2L, 3L, 5L));
    }

    @Test
    void getsNewCurrentLevelAfterItWasDeleted() {
        OrderPriceTimePrioritizer orderPriceTimePrioritizer = OrderPriceTimePrioritizer.offers();
        orderPriceTimePrioritizer.addOrder(new Order(1, 1.0, 'O', 1));
        orderPriceTimePrioritizer.addOrder(new Order(2, 2.0, 'O', 1));
        orderPriceTimePrioritizer.addOrder(new Order(3, 3.0, 'O', 2));
        orderPriceTimePrioritizer.deleteOrder(2);

        assertThat(orderPriceTimePrioritizer.getLevelPrice(2)).isEqualTo(Optional.of(3.0));

        orderPriceTimePrioritizer.resizeOrder(3, 3);
        assertThat(orderPriceTimePrioritizer.getLevelTotalSize(2)).isEqualTo(3L);
    }
}