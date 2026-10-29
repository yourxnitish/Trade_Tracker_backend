package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.dto.DailyJournalRequest;
import com.tradetrack.tradingjournal.dto.DailyJournalSummaryResponse;
import com.tradetrack.tradingjournal.entity.DailyJournal;
import com.tradetrack.tradingjournal.service.DailyJournalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journals")
public class DailyJournalController {

    private final DailyJournalService journalService;

    @Autowired
    public DailyJournalController(DailyJournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public ResponseEntity<List<DailyJournal>> getAllJournals() {
        return ResponseEntity.ok(journalService.getAllJournals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyJournal> getJournalById(@PathVariable Long id) {
        return ResponseEntity.ok(journalService.getJournalById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<DailyJournalSummaryResponse> getJournalByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(journalService.getJournalSummaryForDate(date));
    }

    @PostMapping
    public ResponseEntity<DailyJournal> saveJournal(@Valid @RequestBody DailyJournalRequest request) {
        DailyJournal journal = journalService.saveOrUpdateJournal(request);
        return new ResponseEntity<>(journal, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyJournal> updateJournal(@PathVariable Long id, @Valid @RequestBody DailyJournalRequest request) {
        DailyJournal journal = journalService.saveOrUpdateJournal(request);
        return ResponseEntity.ok(journal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournal(@PathVariable Long id) {
        journalService.deleteJournal(id);
        return ResponseEntity.noContent().build();
    }
}
