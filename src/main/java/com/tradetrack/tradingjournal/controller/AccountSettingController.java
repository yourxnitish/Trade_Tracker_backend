package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.entity.AccountTransaction;
import com.tradetrack.tradingjournal.service.AccountSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class AccountSettingController {

    private final AccountSettingService settingService;

    @Autowired
    public AccountSettingController(AccountSettingService settingService) {
        this.settingService = settingService;
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
}
