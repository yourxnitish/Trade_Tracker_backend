package com.tradetrack.tradingjournal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist")
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "exchange", length = 50)
    private String exchange;

    @Column(name = "planned_entry_price", precision = 15, scale = 4)
    private BigDecimal plannedEntryPrice;

    @Column(name = "target_price", precision = 15, scale = 4)
    private BigDecimal targetPrice;

    @Column(name = "stop_loss", precision = 15, scale = 4)
    private BigDecimal stopLoss;

    @Column(name = "strategy", length = 100)
    private String strategy;

    @Column(name = "reason_for_watching", columnDefinition = "TEXT")
    private String reasonForWatching;

    @Column(name = "status", length = 30)
    private String status = "WATCHING"; // WATCHING, READY, TRADED, REMOVED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public WatchlistItem() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstrumentName() { return instrumentName; }
    public void setInstrumentName(String instrumentName) { this.instrumentName = instrumentName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public BigDecimal getPlannedEntryPrice() { return plannedEntryPrice; }
    public void setPlannedEntryPrice(BigDecimal plannedEntryPrice) { this.plannedEntryPrice = plannedEntryPrice; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getStopLoss() { return stopLoss; }
    public void setStopLoss(BigDecimal stopLoss) { this.stopLoss = stopLoss; }

    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }

    public String getReasonForWatching() { return reasonForWatching; }
    public void setReasonForWatching(String reasonForWatching) { this.reasonForWatching = reasonForWatching; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
