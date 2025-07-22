package dev.aj.full_stack_v5.product.service;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ImageService {
    ImageResponseDto saveImage(ImageRequestDto imageDto) throws IOException;
    Set<ImageResponseDto> saveImagesForProduct(List<ImageRequestDto> images, Long productId, boolean replaceAll);
    ImageResponseDto updateImage(ImageRequestDto imageDto, Long id) throws IOException;
    Image getImageById(Long id);

    List<ImageResponseDto> getAllImages();
    List<ImageResponseDto> getImagesByProductId(Long id);

    @Transactional(readOnly = true)
    List<ImageResponseDto> getImagesByProductName(String name);

    void deleteImageById(Long id);

    Set<ImageResponseDto> saveImages(List<ImageRequestDto> imageRequests);

    Image getImageByName(String imageName);
}
