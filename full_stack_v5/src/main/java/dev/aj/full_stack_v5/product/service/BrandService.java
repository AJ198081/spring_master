package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.BrandDto;
import dev.aj.full_stack_v5.product.domain.entities.Brand;

import java.util.List;

public interface BrandService {

    Brand saveOrUpdateBrand(Brand brand);

    List<String> getAvailableBrandNames();

    BrandDto addBrand(BrandDto brandDto);
    BrandDto getBrandById(Long id);
    BrandDto getBrandByName(String brandName);
    BrandDto updateBrand(BrandDto brandDto, Long id);
    void deleteBrandById(Long id);
    void deleteBrandByName(String brandName);
    List<BrandDto> getAllBrands();

    BrandDto addBrand(String brandName);
}
