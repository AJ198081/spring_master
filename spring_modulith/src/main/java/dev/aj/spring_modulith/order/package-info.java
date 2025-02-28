@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"inventory::dtos", "inventory", "payment"}
)
package dev.aj.spring_modulith.order;