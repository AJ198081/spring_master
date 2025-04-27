package dev.aj.restaurant.services.impl;

import dev.aj.restaurant.domain.entities.Photo;
import dev.aj.restaurant.services.PhotoService;
import dev.aj.restaurant.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PhotoServiceImpl implements PhotoService {

    private final StorageService storageService;

    @Override
    public Photo save(MultipartFile photo) {
        String fileName = UUID.randomUUID().toString();
        String storedFilePath = storageService.store(photo, fileName);
        return Photo.builder()
                .name(fileName)
                .url(storedFilePath)
                .build();
    }

    @Override
    public Optional<Resource> getPhoto(String filename) {
        return storageService.retrieve(filename);
    }
}
