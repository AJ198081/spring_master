package dev.aj.full_stack_v5.order.domain.dtos;

import dev.aj.full_stack_v5.order.domain.entities.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link dev.aj.full_stack_v5.order.domain.entities.Customer}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address billingAddress;
    private Address shippingAddress;
}