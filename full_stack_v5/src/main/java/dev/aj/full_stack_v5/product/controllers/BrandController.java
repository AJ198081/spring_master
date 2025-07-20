package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.product.domain.dtos.BrandDto;
import dev.aj.full_stack_v5.product.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Slf4j
public class BrandController {

    private final BrandService brandService;

    @PostMapping("/")
    public ResponseEntity<BrandDto> addBrand(@RequestParam String brandName){
        return ResponseEntity.ok(brandService.addBrand(brandName));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BrandDto>> getAllBrands(){
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id){
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BrandDto> getBrandByName(@PathVariable String name){
        return ResponseEntity.ok(brandService.getBrandByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@RequestBody BrandDto brandDto, @PathVariable Long id){
        return ResponseEntity.ok(brandService.updateBrand(brandDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable Long id){
        brandService.deleteBrandById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/name/{name}")
    public ResponseEntity<Void> deleteBrandByName(@PathVariable String name){
        brandService.deleteBrandByName(name);
        return ResponseEntity.noContent().build();
    }
}
