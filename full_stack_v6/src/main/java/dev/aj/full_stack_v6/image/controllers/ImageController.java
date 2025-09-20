package dev.aj.full_stack_v6.image.controllers;

import dev.aj.full_stack_v6.common.domain.entities.Image;
import dev.aj.full_stack_v6.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @SneakyThrows
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Image> saveImage(@RequestParam("files") MultipartFile multipartFile) {
        Image image = new Image(multipartFile);
        return ResponseEntity.ok(imageService.saveImage(image));
    }

    @SneakyThrows
    @PostMapping(value = "/list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<Image>> saveImages(@RequestParam("files") List<MultipartFile> multipartFiles) {
        List<Image> imageRequests = multipartFiles.stream()
                .map(Image::new)
                .toList();
        return ResponseEntity.ok(imageService.saveImages(imageRequests));
    }

    @SneakyThrows
    @PostMapping(value = "/product/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<Image>> addImagesToAProduct(
            @RequestPart("files") List<MultipartFile> files,
            @PathVariable Long productId,
            @RequestParam(value = "replaceAll", required = false, defaultValue = "false") boolean replaceAll) {
        List<Image> imageDtos = files.stream()
                .map(Image::new)
                .toList();
        return ResponseEntity.ok(imageService.saveImagesForProduct(imageDtos, productId, replaceAll));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImageByName(@PathVariable String imageName) {

        Image image = imageService.getImageByName(imageName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s".formatted(image.getFileName())
                )
                .body(new ByteArrayResource(image.getContents()));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {

        Image image = imageService.getImageById(id);

        ByteArrayResource resource = new ByteArrayResource(image.getContents());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s".formatted(image.getFileName())
                )
                .body(resource);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Image>> getImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(imageService.getImagesByProductId(productId));
    }

    @SneakyThrows
    @PutMapping("/{imageId}")
    public ResponseEntity<Image> updateImage(@RequestPart("files") MultipartFile multipartFile, @PathVariable Long imageId) {
        Image image = new Image(multipartFile);
        return ResponseEntity.ok(imageService.updateImage(image, imageId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable(value = "id", required = true) Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }
}
