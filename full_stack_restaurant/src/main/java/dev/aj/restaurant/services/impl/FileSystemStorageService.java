package dev.aj.restaurant.services.impl;

import dev.aj.restaurant.exceptions.StorageException;
import dev.aj.restaurant.services.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileSystemStorageService implements StorageService {

    @Value("${file.storage.directory:src/test/resources/data}")
    private String storageDirectory;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storageDirectory);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e.getCause());
        }
    }

    @Override
    public String store(MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot save an empty file: %s".formatted(fileName));
        }

        Path destinationPath = getDestinationPath(fileName, StringUtils.getFilenameExtension(file.getOriginalFilename()));

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file %s".formatted(file.getOriginalFilename()), e.getCause());
        }

        return destinationPath.toString();
    }

    private Path getDestinationPath(String fileName, String extension) {
        if (extension == null) {
            throw new StorageException("Cannot save a file without extension: %s".formatted(fileName));
        }
        String fileNameWithExtension = "%s.%s".formatted(fileName, extension);

        Path destinationPath = rootLocation.resolve(fileNameWithExtension)
                .normalize()
                .toAbsolutePath();

        if (!destinationPath.getParent().equals(rootLocation.toAbsolutePath())) {
            throw new StorageException("Cannot store a file outside the storage directory: %s".formatted(storageDirectory));
        }
        return destinationPath;
    }

    @Override
    public Optional<Resource> retrieve(String fileName) {

        Path filePath;

        try {
            filePath = rootLocation.resolve(fileName);
        } catch (InvalidPathException e) {
            log.error("Invalid path: {}", e.getMessage());
            return Optional.empty(); // Design decision: return empty if the path is invalid
        }

        if (Files.exists(filePath)) {
            FileSystemResource fileResource = new FileSystemResource(filePath);
            if (!fileResource.exists() || !fileResource.isReadable()) {
                return Optional.of(fileResource);
            }
        }
        return Optional.empty();
    }
}
