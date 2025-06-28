package dev.aj.full_stack_v5.product.service.impl;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import dev.aj.full_stack_v5.product.domain.mappers.ImageMapper;
import dev.aj.full_stack_v5.product.repositories.ImageRepository;
import dev.aj.full_stack_v5.product.repositories.ProductRepository;
import dev.aj.full_stack_v5.product.service.ImageService;
import dev.aj.full_stack_v5.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ImageMapper imageMapper;

    @Override
    public ImageResponseDto saveImage(ImageRequestDto imageDto) throws IOException {

        log.info("Saving image: {}", imageDto.getFile().getOriginalFilename());
        if (imageDto.getFile() == null || imageDto.getFile().isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }
        Image image = imageMapper.toImage(imageDto);
        Image savedImage = imageRepository.save(image);
        log.info("Image: {} saved successfully with ID: {}", savedImage.getFileName(), savedImage.getId());

        return imageMapper.toImageDto(savedImage);
    }


    @SneakyThrows
    @Override
    public Set<ImageResponseDto> saveImages(List<ImageRequestDto> imageRequests) {

        return imageRequests.stream()
                .map(imageRequestDto -> {
                    try {
                        return imageMapper.toImage(imageRequestDto);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(imageRepository::save)
                .map(imageMapper::toImageDto)
                .collect(toSet());
    }

    @Override
    public Set<ImageResponseDto> saveImagesForProduct(List<ImageRequestDto> images, Long productId) {
        log.info("Saving images for product with ID: {}", productId);

        Product savedProductWithImages = productRepository.findById(productId)
                .map(product -> {
                    product.getImages().addAll(imageMapper.toImages(images));
                    return productRepository.save(product);
                }).orElseThrow(() -> new EntityNotFoundException("Product with ID: %s not found.".formatted(productId)));

        return imageMapper.toImageDtos(savedProductWithImages.getImages());
    }

    @Override
    public ImageResponseDto updateImage(ImageRequestDto imageDto, Long id) throws IOException {

        log.info("Updating image with ID: {}", id);

        if (imageDto.getFile() == null || imageDto.getFile().isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image hasn't been found with ID: " + id));

            Image updatedImage = imageMapper.toImage(imageDto);
            updatedImage.setId(existingImage.getId());
            updatedImage.setProduct(existingImage.getProduct());
            updatedImage.setAuditMetaData(existingImage.getAuditMetaData());

            Image savedImage = imageRepository.save(updatedImage);
            log.info("Image updated successfully with ID: {}", savedImage.getId());
            return imageMapper.toImageDto(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        log.info("Fetching image with ID: {}", id);
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image isn't found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageResponseDto> getAllImages() {
        log.info("Fetching all images");
        return imageMapper.toImageDtos(imageRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageResponseDto> getImagesByProductId(Long id) {
        log.info("Fetching images for product ID: {}", id);
        return imageMapper.toImageDtos(imageRepository.findByProductId(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ImageResponseDto> getImagesByProductName(String name) {
        log.info("Fetching images for product name: {}", name);
        return imageMapper.toImageDtos(imageRepository.findByProductName(name));
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteImageById(Long id) {
        log.info("Deleting image with ID: {}", id);
        if (!imageRepository.existsById(id)) {
            throw new EntityNotFoundException("Image hasn't been found with ID: " + id);
        }

        imageRepository.findById(id)
                .ifPresentOrElse(
                        image -> {
                            Product associatedProduct = image.getProduct();
                            associatedProduct.getImages().remove(image);
                            productRepository.save(associatedProduct);
                            imageRepository.delete(image);
                            log.info("Image deleted successfully with ID: {}", id);
                        },
                        () -> log.warn("Image id: {} doesn't exist.", id));

    }

}
