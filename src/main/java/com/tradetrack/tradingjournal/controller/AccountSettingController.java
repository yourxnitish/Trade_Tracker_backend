package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.entity.AccountTransaction;
import com.tradetrack.tradingjournal.repository.*;
import com.tradetrack.tradingjournal.service.AccountSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class AccountSettingController {

    private final AccountSettingService settingService;
    private final TradeRepository tradeRepository;
    private final DailyJournalRepository dailyJournalRepository;
    private final WatchlistRepository watchlistRepository;
    private final ObservationNoteRepository observationNoteRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    @Autowired
    public AccountSettingController(AccountSettingService settingService,
                                    TradeRepository tradeRepository,
                                    DailyJournalRepository dailyJournalRepository,
                                    WatchlistRepository watchlistRepository,
                                    ObservationNoteRepository observationNoteRepository,
                                    AccountTransactionRepository accountTransactionRepository) {
        this.settingService = settingService;
        this.tradeRepository = tradeRepository;
        this.dailyJournalRepository = dailyJournalRepository;
        this.watchlistRepository = watchlistRepository;
        this.observationNoteRepository = observationNoteRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getSettings() {
        return ResponseEntity.ok(settingService.getAllSettings());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> updateSettings(@RequestBody Map<String, String> settings) {
        return ResponseEntity.ok(settingService.updateSettings(settings));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<AccountTransaction>> getTransactions() {
        return ResponseEntity.ok(settingService.getTransactions());
    }

    @PostMapping("/transactions")
    public ResponseEntity<AccountTransaction> addTransaction(@RequestBody Map<String, Object> body) {
        String type = (String) body.get("type");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String dateStr = (String) body.get("date");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        String notes = (String) body.get("notes");

        return ResponseEntity.ok(settingService.addTransaction(type, amount, date, notes));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        settingService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-data")
    @Transactional
    public ResponseEntity<Map<String, String>> resetAllData() {
        tradeRepository.deleteAllInBatch();
        dailyJournalRepository.deleteAllInBatch();
        watchlistRepository.deleteAllInBatch();
        observationNoteRepository.deleteAllInBatch();
        accountTransactionRepository.deleteAllInBatch();

        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "All trading records, daily journals, watchlist items, and notes have been cleared.");
        return ResponseEntity.ok(response);
    }
}
