package dev.aj.full_stack_v5.order.domain.mappers;

import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface CustomerMapper {

    @Mapping(target = "userUsername", source = "customer.user.username")
    @Mapping(target = "cartId", source = "customer.cart.id")
    CustomerDto customerToCustomerDto(Customer customer);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Customer customerDtoToCustomer(CustomerDto customerDto);



}
