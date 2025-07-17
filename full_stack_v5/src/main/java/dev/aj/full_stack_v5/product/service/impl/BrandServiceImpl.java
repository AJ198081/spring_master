package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.product.domain.entities.Brand;
import dev.aj.full_stack_v5.product.repositories.BrandRepository;
import dev.aj.full_stack_v5.product.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public Brand saveOrUpdateBrand(Brand brand) {

        return brandRepository.findBrandByName(brand.getName())
                .orElseGet(() -> brandRepository.save(brand));
    }

    @Override
    public List<String> getAvailableBrandNames() {
        return brandRepository.findAllBrandNames();
    }
}
