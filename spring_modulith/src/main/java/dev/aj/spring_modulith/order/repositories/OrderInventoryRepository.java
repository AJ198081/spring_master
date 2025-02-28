package dev.aj.spring_modulith.order.repositories;

import dev.aj.spring_modulith.order.entities.OrderInventory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.UUID;

interface OrderInventoryRepository extends CrudRepository<OrderInventory, Long> {

    OrderInventory findByOrderIdAndInventoryId(UUID orderId, UUID inventoryId);

    @Query("SELECT SUM(oi.totalQtyPrice) FROM OrderInventory oi WHERE oi.orderId = :orderId")
    BigDecimal totalOrderPriceByOrderId(UUID orderId);

}
