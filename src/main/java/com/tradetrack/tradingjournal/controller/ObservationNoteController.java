package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.dto.NoteRequest;
import com.tradetrack.tradingjournal.entity.ObservationNote;
import com.tradetrack.tradingjournal.service.ObservationNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class ObservationNoteController {

    private final ObservationNoteService noteService;

    @Autowired
    public ObservationNoteController(ObservationNoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<ObservationNote>> getNotes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(noteService.getAllNotes(category, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservationNote> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @PostMapping
    public ResponseEntity<ObservationNote> createNote(@Valid @RequestBody NoteRequest request) {
        ObservationNote note = noteService.createNote(request);
        return new ResponseEntity<>(note, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObservationNote> updateNote(@PathVariable Long id, @Valid @RequestBody NoteRequest request) {
        ObservationNote note = noteService.updateNote(id, request);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
