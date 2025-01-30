package dev.aj.full_stack_v2.controllers;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v2.PostgresTestContainerConfiguration;
import dev.aj.full_stack_v2.TestConfig;
import dev.aj.full_stack_v2.TestSecurityConfig;
import dev.aj.full_stack_v2.domain.entities.Note;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestContainerConfiguration.class, TestSecurityConfig.class, TestConfig.class})
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.org.springframework.security=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class NoteControllerTest {

    private static final String UPDATED = "-Updated";

    @Autowired
    private TestConfig testConfig;

    private RestClient restClient;

    @Autowired
    private Faker faker;

    @LocalServerPort
    private int port;

    @BeforeAll
    void beforeAll() {
        restClient = testConfig.restClient(port, "/api/notes");
    }

    @Test
    void createNoteForUser() {

        Note newNote = testConfig.generateNoteStream(faker).limit(1).findFirst().orElseThrow();

        ResponseEntity<Note> noteResponseEntity = restClient.post()
                .body(newNote.getContent())
                .retrieve()
                .toEntity(Note.class);

        assertNotNull(noteResponseEntity.getBody());
        assertThat(noteResponseEntity.getBody().getContent())
                .isEqualTo(newNote.getContent());

    }

    @Test
    void getNotesForUser() {
        Note newNote = testConfig.generateNoteStream(faker).limit(1).findFirst().orElseThrow();

        ResponseEntity<Note> noteResponseEntity = restClient.post()
                .body(newNote.getContent())
                .retrieve()
                .toEntity(Note.class);

        ResponseEntity<List<Note>> notesResponse = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(notesResponse.getBody());
        assertThat(notesResponse.getBody().size()).isGreaterThanOrEqualTo(1);

        assertThat(notesResponse.getBody().stream().map(Note::getContent)).contains(newNote.getContent());
    }

    @Test
    void updateNoteForUser() {

        Note newNote = testConfig.generateNoteStream(faker).limit(1).findFirst().orElseThrow();

        ResponseEntity<Note> noteResponseEntity = restClient.post()
                .body(newNote.getContent())
                .retrieve()
                .toEntity(Note.class);

        ResponseEntity<Note> updatedNoteResponseEntity = restClient.put()
                .uri("/{noteId}", Objects.requireNonNull(noteResponseEntity.getBody()).getId())
                .body(noteResponseEntity.getBody().getContent().concat(UPDATED))
                .retrieve()
                .toEntity(Note.class);

        assertThat(Objects.requireNonNull(updatedNoteResponseEntity.getBody()).getContent())
                .isEqualTo(newNote.getContent().concat(UPDATED));

    }

    @Test
    void deleteNoteForUser() {

        Note newNote = testConfig.generateNoteStream(faker).limit(1).findFirst().orElseThrow();

        ResponseEntity<Note> noteResponseEntity = restClient.post()
                .body(newNote.getContent())
                .retrieve()
                .toEntity(Note.class);

        ResponseEntity<Void> deleteResponseEntity = restClient.delete()
                .uri("/{noteId}", Objects.requireNonNull(noteResponseEntity.getBody()).getId())
                .retrieve()
                .toBodilessEntity();

        assertThat(deleteResponseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());

    }
}