package dev.aj.full_stack_v5.order.domain.mappers;

import dev.aj.full_stack_v5.order.domain.dtos.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface CartMapper {

    @Mapping(target = "customerPhone", source = "cart.customer.phone")
    @Mapping(target = "customerLastName", source = "cart.customer.lastName")
    @Mapping(target = "customerFirstName", source = "cart.customer.firstName")
    @Mapping(target = "customerEmail", source = "cart.customer.email")
    Cart cartToCartDto(dev.aj.full_stack_v5.order.domain.entities.Cart cart);


}
