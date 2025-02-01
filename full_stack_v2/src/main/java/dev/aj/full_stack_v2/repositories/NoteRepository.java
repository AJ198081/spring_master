package dev.aj.full_stack_v2.repositories;

import dev.aj.full_stack_v2.domain.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Note findNoteById(Long id);

    List<Note> findAllByOwnerUsername(String username);
}
