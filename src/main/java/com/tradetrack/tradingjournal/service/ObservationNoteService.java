package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.NoteRequest;
import com.tradetrack.tradingjournal.entity.ObservationNote;
import com.tradetrack.tradingjournal.exception.ResourceNotFoundException;
import com.tradetrack.tradingjournal.repository.ObservationNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObservationNoteService {

    private final ObservationNoteRepository noteRepository;

    @Autowired
    public ObservationNoteService(ObservationNoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<ObservationNote> getAllNotes(String category, String search) {
        List<ObservationNote> notes;
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            notes = noteRepository.findByCategoryOrderByCreatedAtDesc(category.trim());
        } else {
            notes = noteRepository.findAllByOrderByCreatedAtDesc();
        }

        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.trim().toLowerCase();
            notes = notes.stream()
                    .filter(n -> (n.getTitle() != null && n.getTitle().toLowerCase().contains(lowerSearch))
                            || (n.getDescription() != null && n.getDescription().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }

        return notes;
    }

    public ObservationNote getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
    }

    @Transactional
    public ObservationNote createNote(NoteRequest request) {
        ObservationNote note = new ObservationNote();
        note.setTitle(request.getTitle().trim());
        note.setCategory(request.getCategory().trim());
        note.setDescription(request.getDescription().trim());
        note.setRelatedDate(request.getRelatedDate());
        note.setRelatedTradeId(request.getRelatedTradeId());
        return noteRepository.save(note);
    }

    @Transactional
    public ObservationNote updateNote(Long id, NoteRequest request) {
        ObservationNote note = getNoteById(id);
        note.setTitle(request.getTitle().trim());
        note.setCategory(request.getCategory().trim());
        note.setDescription(request.getDescription().trim());
        note.setRelatedDate(request.getRelatedDate());
        note.setRelatedTradeId(request.getRelatedTradeId());
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id) {
        ObservationNote note = getNoteById(id);
        noteRepository.delete(note);
    }
}
