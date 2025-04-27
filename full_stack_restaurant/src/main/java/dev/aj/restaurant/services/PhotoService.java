package dev.aj.restaurant.services;

import dev.aj.restaurant.domain.entities.Photo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PhotoService {

    Photo save(MultipartFile photo);

    Optional<Resource> getPhoto(String filename);
}
