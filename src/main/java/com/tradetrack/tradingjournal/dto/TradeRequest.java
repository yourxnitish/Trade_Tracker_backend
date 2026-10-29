package com.tradetrack.tradingjournal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TradeRequest {

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    private LocalTime entryTime;
    private LocalTime exitTime;

    @NotBlank(message = "Instrument name is required")
    private String instrumentName;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    private String exchange;
    private String segment;

    @NotBlank(message = "Direction is required (LONG or SHORT)")
    private String direction;

    @NotBlank(message = "Status is required (OPEN or CLOSED)")
    private String status;

    @NotNull(message = "Entry price is required")
    @DecimalMin(value = "0.0001", message = "Entry price must be greater than zero")
    private BigDecimal entryPrice;

    private BigDecimal exitPrice;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private BigDecimal targetPrice;
    private BigDecimal stopLoss;
    private BigDecimal brokerage;
    private BigDecimal otherCharges;
    private String strategy;
    private String entryReason;
    private String exitReason;
    private String marketConditions;
    private String emotionalStateBefore;
    private String emotionalStateAfter;
    private String mistakes;
    private String lessonsLearned;
    private String notes;

    public TradeRequest() {
    }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public LocalTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalTime entryTime) { this.entryTime = entryTime; }

    public LocalTime getExitTime() { return exitTime; }
    public void setExitTime(LocalTime exitTime) { this.exitTime = exitTime; }

    public String getInstrumentName() { return instrumentName; }
    public void setInstrumentName(String instrumentName) { this.instrumentName = instrumentName; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getEntryPrice() { return entryPrice; }
    public void setEntryPrice(BigDecimal entryPrice) { this.entryPrice = entryPrice; }

    public BigDecimal getExitPrice() { return exitPrice; }
    public void setExitPrice(BigDecimal exitPrice) { this.exitPrice = exitPrice; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getStopLoss() { return stopLoss; }
    public void setStopLoss(BigDecimal stopLoss) { this.stopLoss = stopLoss; }

    public BigDecimal getBrokerage() { return brokerage; }
    public void setBrokerage(BigDecimal brokerage) { this.brokerage = brokerage; }

    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }

    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }

    public String getEntryReason() { return entryReason; }
    public void setEntryReason(String entryReason) { this.entryReason = entryReason; }

    public String getExitReason() { return exitReason; }
    public void setExitReason(String exitReason) { this.exitReason = exitReason; }

    public String getMarketConditions() { return marketConditions; }
    public void setMarketConditions(String marketConditions) { this.marketConditions = marketConditions; }

    public String getEmotionalStateBefore() { return emotionalStateBefore; }
    public void setEmotionalStateBefore(String emotionalStateBefore) { this.emotionalStateBefore = emotionalStateBefore; }

    public String getEmotionalStateAfter() { return emotionalStateAfter; }
    public void setEmotionalStateAfter(String emotionalStateAfter) { this.emotionalStateAfter = emotionalStateAfter; }

    public String getMistakes() { return mistakes; }
    public void setMistakes(String mistakes) { this.mistakes = mistakes; }

    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
