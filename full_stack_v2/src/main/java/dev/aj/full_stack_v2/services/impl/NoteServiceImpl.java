package dev.aj.full_stack_v2.services.impl;

import dev.aj.full_stack_v2.domain.entities.Note;
import dev.aj.full_stack_v2.repositories.NoteRepository;
import dev.aj.full_stack_v2.services.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Transactional
    @Override
    public Note createNoteForUser(String username, String content) {
        Note note = Note.builder()
                .content(content)
                .ownerUsername(username)
                .build();
        return noteRepository.save(note);
    }

    @Transactional
    @Override
    public Note updateNoteForUser(String username, Long noteId, String content) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note Id: %d not found".formatted(noteId)));
        note.setContent(content);
        return noteRepository.save(note);
    }

    @Override
    public void deleteNoteForUser(String username, Long noteId) {
        noteRepository.deleteById(noteId);
    }

    @Transactional
    @Override
    public List<Note> getNotesForUser(String username) {
        return noteRepository.findAllByOwnerUsername(username);
    }


}
