package com.tradetrack.tradingjournal.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class WatchlistRequest {

    @NotBlank(message = "Instrument name is required")
    private String instrumentName;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    private String exchange;
    private BigDecimal plannedEntryPrice;
    private BigDecimal targetPrice;
    private BigDecimal stopLoss;
    private String strategy;
    private String reasonForWatching;
    private String status; // WATCHING, READY, TRADED, REMOVED
    private String notes;

    public WatchlistRequest() {
    }

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
}
