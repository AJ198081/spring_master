package dev.aj.full_stack_v6.common.domain.mappers;

import dev.aj.full_stack_v6.common.domain.dtos.OrderHistory;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.enums.AddressType;
import dev.aj.full_stack_v6.common.domain.events.OrderSuccessfulEvent;
import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
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

    @Mapping(target = "orderTotal", source = "order.totalPrice")
    @Mapping(target = "shippingDetails", source = "order.customer", qualifiedByName = "getShippingAddress")
    @Mapping(target = "lastName", source = "order.customer.lastName")
    @Mapping(target = "firstName", source = "order.customer.firstName")
    @Mapping(target = "customerId", source = "order.customer.id")
    OrderSuccessfulEvent orderToOrderSuccessfulEvent(Order order);


    @Named("getShippingAddress")
    default ShippingDetails getShippingAddress(Customer customer) {
        Address shippingAddress = customer.getAddresses()
                .stream()
                .filter(address -> address.getAddressType().equals(AddressType.SHIPPING))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to find customer's shipping address"));
        return new ShippingDetails(shippingAddress, customer.getUser().getEmail());
    }
}
