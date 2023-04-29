Improvement suggestions:

* Order should be a `record` - it makes the code much cleaner and emphasizes its immutability
* `side` should be an enum - the code would look more expressive and does not impact performance

As for OrderBook, the integration test proves that we already process orders in the microsecond range. 
Therefore, the latency of a real-life application will be dominated by network time and no improvement on the code that already runs in microseconds will help.
Depending on the real-life scenarios, we could improve the garbage collection of empty `OrderLevel` objects in the `orders` map. Currently, this collection happens upon execution of the queries `getOrders`, `getLevelPrice` and `getLevelTotalSize`. This works best if queries are much rarer than inserts, deletes and resizes. Should queries be executed much more often, we would need to rethink that part. There is also a performance impact in the queries in that order addition is suspended while a query is executed. This approach is also appropriate when queries are rare. 