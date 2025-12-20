@ApplicationModule(
        allowedDependencies = {"common :: entities", "security", "product", "cart", "order", "common", "clients", "payment"}
)
package dev.aj.full_stack_v6.order;

import org.springframework.modulith.ApplicationModule;