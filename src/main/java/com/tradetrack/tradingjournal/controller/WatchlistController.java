package com.tradetrack.tradingjournal.controller;

import com.tradetrack.tradingjournal.dto.WatchlistRequest;
import com.tradetrack.tradingjournal.entity.WatchlistItem;
import com.tradetrack.tradingjournal.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Autowired
    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<WatchlistItem>> getWatchlist(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(watchlistService.getAllWatchlist(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WatchlistItem> getWatchlistItemById(@PathVariable Long id) {
        return ResponseEntity.ok(watchlistService.getWatchlistItemById(id));
    }

    @PostMapping
    public ResponseEntity<WatchlistItem> createWatchlistItem(@Valid @RequestBody WatchlistRequest request) {
        WatchlistItem item = watchlistService.createWatchlistItem(request);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WatchlistItem> updateWatchlistItem(@PathVariable Long id, @Valid @RequestBody WatchlistRequest request) {
        WatchlistItem item = watchlistService.updateWatchlistItem(id, request);
        return ResponseEntity.ok(item);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WatchlistItem> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(watchlistService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlistItem(@PathVariable Long id) {
        watchlistService.deleteWatchlistItem(id);
        return ResponseEntity.noContent().build();
    }
}
