package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.product.domain.dtos.BrandDto;
import dev.aj.full_stack_v5.product.domain.entities.Brand;
import dev.aj.full_stack_v5.product.domain.mappers.BrandMapper;
import dev.aj.full_stack_v5.product.repositories.BrandRepository;
import dev.aj.full_stack_v5.product.service.BrandService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public Brand saveOrUpdateBrand(Brand brand) {
        return brandRepository.findBrandByName(brand.getName())
                .orElseGet(() -> brandRepository.save(brand));
    }

    @Override
    public List<String> getAvailableBrandNames() {
        return brandRepository.findAllBrandNames();
    }

    @Override
    public BrandDto addBrand(BrandDto brandDto) {
        Brand persistedBrand = brandRepository.findBrandByName(brandDto.getName())
                .orElseGet(() -> brandRepository.save(brandMapper.toBrand(brandDto)));

        return brandMapper.toBrandDto(persistedBrand);
    }

    @Override
    public BrandDto addBrand(String brandName) {
        return brandMapper.toBrandDto(
                brandRepository.findBrandByName(brandName)
                        .orElseGet(() -> brandRepository.save(
                                Brand.builder()
                                        .name(brandName)
                                        .build())
                        )
        );
    }

    @Override
    public BrandDto getBrandById(Long id) {
        return brandMapper.toBrandDto(
                brandRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Brand isn't found with id: " + id))
        );
    }

    @Override
    public BrandDto getBrandByName(String brandName) {
        return brandMapper.toBrandDto(
                brandRepository.findBrandByName(brandName)
                        .orElseThrow(() -> new NoSuchElementException("Brand isn't found with name: " + brandName))
        );
    }

    @Override
    public BrandDto updateBrand(BrandDto brandDto, Long id) {
        return brandMapper.toBrandDto(
                brandRepository.findById(id)
                        .map(brand -> {
                            brand.getProducts().clear();
                            brand.setName(brandDto.getName());
                            return brandRepository.save(brand);
                        })
                        .orElseThrow(() -> new EntityNotFoundException("Brand with id: %s not found. Unable to update products.".formatted(id)))
        );
    }

    @Override
    public void deleteBrandById(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new NoSuchElementException("Brand not found with id: " + id);
        }

        brandRepository.findById(id)
                .ifPresent(brand -> {
                            brand.getProducts()
                                    .forEach(product -> product.setBrand(null));
                            brandRepository.delete(brand);
                        }
                );
    }

    @Override
    public void deleteBrandByName(String brandName) {
        Optional.ofNullable(brandName)
                .flatMap(brandRepository::findBrandByName)
                .ifPresentOrElse(
                        brandRepository::delete,
                        () -> log.warn("Brand with name: {} not found. Unable to delete brand.", brandName)
                );
    }

    @Override
    public List<BrandDto> getAllBrands() {
        return brandMapper.toBrandDtos(brandRepository.findAll());
    }
}
