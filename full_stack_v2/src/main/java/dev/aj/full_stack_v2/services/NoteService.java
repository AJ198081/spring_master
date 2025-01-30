package dev.aj.full_stack_v2.services;

import dev.aj.full_stack_v2.domain.entities.Note;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NoteService {

    Note createNoteForUser(String username, String content);

    @Transactional
    Note updateNoteForUser(String username, Long noteId, String content);

    void deleteNoteForUser(String username, Long noteId);

    @Transactional
    List<Note> getNotesForUser(String username);
}
