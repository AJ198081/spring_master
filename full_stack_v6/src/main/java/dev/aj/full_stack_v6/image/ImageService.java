package dev.aj.full_stack_v6.image;

import dev.aj.full_stack_v6.common.domain.entities.Image;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ImageService {

    Image saveImage(Image image) throws IOException;

    Set<Image> saveImagesForProduct(List<Image> images, Long productId, boolean replaceAll);

    Image updateImage(Image image, Long id) throws IOException;

    Image getImageById(Long id);

    List<Image> getAllImages();

    List<Image> getImagesByProductId(Long id);

    @Transactional(readOnly = true)
    List<Image> getImagesByProductName(String name);

    void deleteImageById(Long id);

    Set<Image> saveImages(List<Image> images);

    Image getImageByName(String imageName);
}
