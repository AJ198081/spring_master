package dev.aj.full_stack_v5.product.controllers;

import dev.aj.full_stack_v5.product.domain.dtos.ImageRequestDto;
import dev.aj.full_stack_v5.product.domain.dtos.ImageResponseDto;
import dev.aj.full_stack_v5.product.domain.entities.Image;
import dev.aj.full_stack_v5.product.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @SneakyThrows
    @PostMapping(value = "/")
    public ResponseEntity<ImageResponseDto> addImage(@RequestParam("file") MultipartFile multipartFile) {
        ImageRequestDto imageRequestDto = new ImageRequestDto(multipartFile);
        return ResponseEntity.ok(imageService.saveImage(imageRequestDto));
    }

    @PostMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<ImageResponseDto>> addImagesToAProduct(@RequestPart("files") List<MultipartFile> files, @PathVariable Long productId) {
        List<ImageRequestDto> imageDtos = files.stream()
                .map(ImageRequestDto::new)
                .toList();
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

    @SneakyThrows
    @PutMapping("/{imageId}")
    public ResponseEntity<ImageResponseDto> updateImage(@RequestBody ImageRequestDto image, @PathVariable Long imageId) {
        return ResponseEntity.ok(imageService.updateImage(image, imageId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable(value = "id", required = true) Long id) {

        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }

}
