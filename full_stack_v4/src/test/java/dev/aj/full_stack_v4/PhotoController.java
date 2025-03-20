package dev.aj.full_stack_v4;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    @GetMapping("/{photoName}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String photoName) {
        {
            try {
                Resource resource = new ClassPathResource("photos/" + photoName);


                if (!resource.exists()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(("Photo not found: " + photoName).getBytes());
                }

                byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());

                String contentType = Files.probeContentType(resource.getFile().toPath());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                        .body(photoBytes);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error reading photo: " + photoName).getBytes());
            }
        }
    }
}
