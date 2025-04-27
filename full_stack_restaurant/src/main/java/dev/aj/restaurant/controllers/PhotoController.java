package dev.aj.restaurant.controllers;

import dev.aj.restaurant.domain.dtos.PhotoDto;
import dev.aj.restaurant.domain.entities.Photo;
import dev.aj.restaurant.domain.mappers.PhotoMapper;
import dev.aj.restaurant.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoMapper photoMapper;

    @PostMapping
    public ResponseEntity<PhotoDto> uploadPhoto(@RequestParam("file") MultipartFile file) {
        Photo savedPhoto = photoService.save(file);
        return ResponseEntity.ok(photoMapper.toDto(savedPhoto));
    }


}
