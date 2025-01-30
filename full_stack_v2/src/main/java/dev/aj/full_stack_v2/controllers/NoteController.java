package dev.aj.full_stack_v2.controllers;

import dev.aj.full_stack_v2.domain.entities.Note;
import dev.aj.full_stack_v2.services.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Note> createNoteForUser(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody String content) {
        return ResponseEntity.ok(noteService.createNoteForUser(userDetails.getUsername(), content));
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotesForUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getNotesForUser(userDetails.getUsername()));
    }

    @PutMapping(path = "/{noteId}")
    public ResponseEntity<Note> updateNoteForUser(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long noteId,
                                                  @RequestBody String content) {
        Note updatedNote = noteService.updateNoteForUser(userDetails.getUsername(), noteId, content);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping(path = "/{noteId}")
    public ResponseEntity<HttpStatus> deleteNoteForUser(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable Long noteId) {
        noteService.deleteNoteForUser(userDetails.getUsername(), noteId);
        return ResponseEntity.accepted().build();
    }

}
