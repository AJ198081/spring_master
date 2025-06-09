package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import dev.aj.full_stack_v5.product.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/")
    public ResponseEntity<ImageResponseDto> addImage(@RequestBody ImageRequestDto image) {
        return ResponseEntity.ok(imageService.saveImage(image));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<Set<ImageResponseDto>> addImagesToAProduct(@RequestBody List<ImageRequestDto> imageDtos, @PathVariable Long productId) {
        return ResponseEntity.ok(imageService.saveImagesForProduct(imageDtos, productId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ImageResponseDto>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {

        Image image = imageService.getImageById(id);

        ByteArrayResource resource = new ByteArrayResource(image.getImage());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s".formatted(image.getFileName())
                )
                .body(resource);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageResponseDto>> getImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(imageService.getImagesByProductId(productId));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ImageResponseDto> updateImage(@RequestBody ImageRequestDto image, @PathVariable Long imageId) {
        return ResponseEntity.ok(imageService.updateImage(image, imageId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteImageById(Long id) {

        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }

}
