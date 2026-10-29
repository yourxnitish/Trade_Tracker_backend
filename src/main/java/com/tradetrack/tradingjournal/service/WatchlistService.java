package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.WatchlistRequest;
import com.tradetrack.tradingjournal.entity.WatchlistItem;
import com.tradetrack.tradingjournal.exception.ResourceNotFoundException;
import com.tradetrack.tradingjournal.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    @Autowired
    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<WatchlistItem> getAllWatchlist(String status) {
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            return watchlistRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
        }
        return watchlistRepository.findAllByOrderByCreatedAtDesc();
    }

    public WatchlistItem getWatchlistItemById(Long id) {
        return watchlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist item not found with id: " + id));
    }

    @Transactional
    public WatchlistItem createWatchlistItem(WatchlistRequest request) {
        WatchlistItem item = new WatchlistItem();
        copyProperties(request, item);
        return watchlistRepository.save(item);
    }

    @Transactional
    public WatchlistItem updateWatchlistItem(Long id, WatchlistRequest request) {
        WatchlistItem item = getWatchlistItemById(id);
        copyProperties(request, item);
        return watchlistRepository.save(item);
    }

    @Transactional
    public WatchlistItem updateStatus(Long id, String status) {
        WatchlistItem item = getWatchlistItemById(id);
        item.setStatus(status.toUpperCase());
        return watchlistRepository.save(item);
    }

    @Transactional
    public void deleteWatchlistItem(Long id) {
        WatchlistItem item = getWatchlistItemById(id);
        watchlistRepository.delete(item);
    }

    private void copyProperties(WatchlistRequest request, WatchlistItem item) {
        item.setInstrumentName(request.getInstrumentName().trim());
        item.setSymbol(request.getSymbol().trim().toUpperCase());
        item.setExchange(request.getExchange() != null ? request.getExchange().trim().toUpperCase() : "NSE");
        item.setPlannedEntryPrice(request.getPlannedEntryPrice());
        item.setTargetPrice(request.getTargetPrice());
        item.setStopLoss(request.getStopLoss());
        item.setStrategy(request.getStrategy());
        item.setReasonForWatching(request.getReasonForWatching());
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            item.setStatus(request.getStatus().trim().toUpperCase());
        }
        item.setNotes(request.getNotes());
    }
}
