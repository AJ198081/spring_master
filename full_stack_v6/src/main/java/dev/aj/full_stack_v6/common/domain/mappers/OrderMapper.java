package dev.aj.full_stack_v6.common.domain.mappers;

import dev.aj.full_stack_v6.common.domain.dtos.OrderHistory;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface OrderMapper {

    @Mapping(target = "paymentId", source = "order.payment.id")
    @Mapping(target = "customerId", source = "order.customer.id")
    OrderHistory orderToOrderHistory(Order order);
}
