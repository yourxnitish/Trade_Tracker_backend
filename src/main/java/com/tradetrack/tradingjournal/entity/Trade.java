package com.tradetrack.tradingjournal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "trades", indexes = {
    @Index(name = "idx_trade_date", columnList = "trade_date"),
    @Index(name = "idx_symbol", columnList = "symbol"),
    @Index(name = "idx_status", columnList = "status")
})
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "entry_time")
    private LocalTime entryTime;

    @Column(name = "exit_time")
    private LocalTime exitTime;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "exchange", length = 50)
    private String exchange; // NSE, BSE, NASDAQ, etc.

    @Column(name = "segment", length = 50)
    private String segment; // Equity, Intraday, Futures, Options, etc.

    @Column(name = "direction", nullable = false, length = 20)
    private String direction; // LONG, SHORT

    @Column(name = "status", nullable = false, length = 20)
    private String status; // OPEN, CLOSED

    @Column(name = "entry_price", precision = 15, scale = 4, nullable = false)
    private BigDecimal entryPrice;

    @Column(name = "exit_price", precision = 15, scale = 4)
    private BigDecimal exitPrice;

    @Column(name = "quantity", precision = 15, scale = 4, nullable = false)
    private BigDecimal quantity;

    @Column(name = "target_price", precision = 15, scale = 4)
    private BigDecimal targetPrice;

    @Column(name = "stop_loss", precision = 15, scale = 4)
    private BigDecimal stopLoss;

    @Column(name = "brokerage", precision = 15, scale = 4)
    private BigDecimal brokerage = BigDecimal.ZERO;

    @Column(name = "other_charges", precision = 15, scale = 4)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(name = "gross_pnl", precision = 15, scale = 4)
    private BigDecimal grossPnl = BigDecimal.ZERO;

    @Column(name = "net_pnl", precision = 15, scale = 4)
    private BigDecimal netPnl = BigDecimal.ZERO;

    @Column(name = "risk_amount", precision = 15, scale = 4)
    private BigDecimal riskAmount = BigDecimal.ZERO;

    @Column(name = "reward_amount", precision = 15, scale = 4)
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    @Column(name = "risk_reward_ratio", precision = 10, scale = 2)
    private BigDecimal riskRewardRatio = BigDecimal.ZERO;

    @Column(name = "strategy", length = 100)
    private String strategy;

    @Column(name = "entry_reason", columnDefinition = "TEXT")
    private String entryReason;

    @Column(name = "exit_reason", columnDefinition = "TEXT")
    private String exitReason;

    @Column(name = "market_conditions", length = 100)
    private String marketConditions;

    @Column(name = "emotional_state_before", length = 100)
    private String emotionalStateBefore;

    @Column(name = "emotional_state_after", length = 100)
    private String emotionalStateAfter;

    @Column(name = "mistakes", columnDefinition = "TEXT")
    private String mistakes;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Trade() {
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

    public BigDecimal getGrossPnl() { return grossPnl; }
    public void setGrossPnl(BigDecimal grossPnl) { this.grossPnl = grossPnl; }

    public BigDecimal getNetPnl() { return netPnl; }
    public void setNetPnl(BigDecimal netPnl) { this.netPnl = netPnl; }

    public BigDecimal getRiskAmount() { return riskAmount; }
    public void setRiskAmount(BigDecimal riskAmount) { this.riskAmount = riskAmount; }

    public BigDecimal getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(BigDecimal rewardAmount) { this.rewardAmount = rewardAmount; }

    public BigDecimal getRiskRewardRatio() { return riskRewardRatio; }
    public void setRiskRewardRatio(BigDecimal riskRewardRatio) { this.riskRewardRatio = riskRewardRatio; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
