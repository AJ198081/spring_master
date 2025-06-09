package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;

import java.util.List;
import java.util.Set;

public interface ImageService {
    ImageResponseDto saveImage(ImageRequestDto imageDto);
    Set<ImageResponseDto> saveImagesForProduct(List<ImageRequestDto> images, Long productId);
    ImageResponseDto updateImage(ImageRequestDto imageDto, Long id);
    Image getImageById(Long id);

    List<ImageResponseDto> getAllImages();
    List<ImageResponseDto> getImagesByProductId(Long id);
    List<ImageResponseDto> getImagesByProductName(String name);

    void deleteImageById(Long id);

}
