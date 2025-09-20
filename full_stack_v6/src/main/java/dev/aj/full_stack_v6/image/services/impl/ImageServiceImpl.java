package dev.aj.full_stack_v6.image.services.impl;

import dev.aj.full_stack_v6.common.domain.entities.Image;
import dev.aj.full_stack_v6.common.domain.entities.Product;
import dev.aj.full_stack_v6.image.ImageService;
import dev.aj.full_stack_v6.image.repositories.ImageRepository;
import dev.aj.full_stack_v6.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public Image saveImage(Image image) {
        if (image == null || image.getContents() == null || image.getContents().length == 0) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        log.info("Saving image: {}", image.getFileName());
        Image savedImage = imageRepository.save(image);

        log.info("Image savedImage with ID: {} and downloadUrl: {}", savedImage.getId(), savedImage.getDownloadUrl());

        return savedImage;
    }

    @Override
    @Transactional
    public Set<Image> saveImagesForProduct(List<Image> images, Long productId, boolean replaceAll) {
        log.info("Saving {} images for product {}, replaceAll={}",
                CollectionUtils.size(images), productId, replaceAll);

        Product product = productService.getProductById(productId);

        if (replaceAll) {
            imageRepository.deleteByProductId(productId);
            if (CollectionUtils.isNotEmpty(product.getImages())) {
                product.getImages().clear();
            }
        }

        if (CollectionUtils.isEmpty(images)) {
            return new HashSet<>();
        }


        return images.stream()
                .filter(Objects::nonNull)
                .filter(img -> img.getContents() != null && img.getContents().length > 0)
                .peek(img -> img.setProduct(product))
                .map(imageRepository::save)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Image updateImage(Image image, Long id) {
        Objects.requireNonNull(id, "Image ID cannot be null");
        log.info("Updating image with ID: {}", id);
        Image existing = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image hasn't been found with ID: " + id));

        if (image == null || image.getContents() == null || image.getContents().length == 0) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        existing.setFileName(image.getFileName());
        existing.setContentType(image.getContentType());
        existing.setContents(image.getContents());

        Image saved = imageRepository.save(existing);
        saved.setDownloadUrl("download/%d".formatted(saved.getId()));
        return imageRepository.save(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image isn't found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Image> getImagesByProductId(Long id) {
        return imageRepository.findByProduct_Id(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Image> getImagesByProductName(String name) {
        return imageRepository.findByProduct_Name(name);
    }

    @Override
    @Transactional
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(image -> {
                    Product associatedProduct = image.getProduct();
                            if (associatedProduct != null) {
                                associatedProduct.getImages().remove(image);
                            }
                            imageRepository.delete(image);
                            log.info("Deleted image with ID: {}", id);
                        },
                        () -> log.warn("Image ID {} not found, no deletion occurred", id));
    }

    @Override
    @Transactional
    public Set<Image> saveImages(List<Image> images) {
        if (CollectionUtils.isEmpty(images)) return new HashSet<>();
        return images.stream()
                .filter(Objects::nonNull)
                .filter(img -> img.getContents() != null && img.getContents().length > 0)
                .map(imageRepository::save)
//                Not needed, as is set in the @PrePersist and @PreUpdate method
//                .peek(img -> img.setDownloadUrl("download/%d".formatted(img.getId())))
                .map(imageRepository::save)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Image getImageByName(String imageName) {
        return imageRepository.findImageByFileName(imageName)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Image isn't found with name: " + imageName));
    }
}
