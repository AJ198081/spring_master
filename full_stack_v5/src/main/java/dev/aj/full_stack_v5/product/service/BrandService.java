package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.entities.Brand;

import java.util.List;

public interface BrandService {

    Brand saveOrUpdateBrand(Brand brand);

    List<String> getAvailableBrandNames();

}
