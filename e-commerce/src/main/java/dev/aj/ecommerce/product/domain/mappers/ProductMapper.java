package dev.aj.ecommerce.product.domain.mappers;

import dev.aj.ecommerce.auth.config.AuthConfig;
import dev.aj.ecommerce.product.domain.dtos.ProductRequest;
import dev.aj.ecommerce.product.domain.dtos.ProductResponse;
import dev.aj.ecommerce.product.domain.entities.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = AuthConfig.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductMapper {

    ProductResponse toDto(Product product);

    Product toEntity(ProductRequest productRequest);
}
