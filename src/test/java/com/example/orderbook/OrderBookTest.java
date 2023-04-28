package com.example.orderbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderBookTest {
    private final int ordersCount = 6_000_000;
    private final int parallelism = 16;
    private final int repeatedPrices = 100;
    private OrderBook orderBook;
    private List<ExecutorService> executors;
    private List<Order> orders;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook();
        executors = new ArrayList<>();
        IntStream.range(0, parallelism).forEach(i -> executors.add(Executors.newSingleThreadExecutor()));
        orders = new ArrayList<>();
        LongStream.range(0, ordersCount).forEach(i ->
                orders.add(new Order(i, i % 3 > 0 ? i % repeatedPrices  : (Math.random() * repeatedPrices), i % 2 == 0 ? 'B':'O', i % 10)));
        Collections.shuffle(orders);
    }

    @Test
    public void processesOrders() {
        int batch = 0;
        int batches = 100;
        do {
            int batchSubmitSize = ordersCount / batches;
            List<Order> batchedOrders = orders.subList(batch * batchSubmitSize, (batch + 1) * batchSubmitSize);
            batchedOrders.parallelStream().forEach(order -> executors.get((int) (order.getId() % parallelism)).submit(() -> orderBook.addOrder(order)));
            // 60% deletions - orders with sizes between 0 and 5
            batchedOrders.parallelStream().filter(order -> order.getSize() < 6).forEach(
                    order -> executors.get((int) (order.getId() % parallelism)).submit(() -> orderBook.deleteOrder(order.getId())));
            // 10% resizes - orders with size 9 resized to size 5, so only orders with sizes 5, 6, 7 and 8 remain
            batchedOrders.parallelStream().filter(order -> order.getSize() == 9).forEach(
                    order -> executors.get((int) (order.getId() % parallelism)).submit(() -> orderBook.resizeOrder(order.getId(), order.getSize() - 4)));
        } while (++batch < batches);
        executors.forEach(ExecutorService::shutdown);
        assertThat(executors.stream().allMatch(executorService -> {
            try {
                return executorService.awaitTermination(1L, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        })).isTrue();

        long expectedSumOfAllSizeLayers = (ordersCount / 10) * (5 + 6 + 7 + 8);
        long actualSumOfAllSizeLayers = orderBook.getOrders('B').stream().mapToLong(Order::getSize).sum()+
                orderBook.getOrders('O').stream().mapToLong(Order::getSize).sum();
        assertThat(actualSumOfAllSizeLayers).isEqualTo(expectedSumOfAllSizeLayers);
    }
}