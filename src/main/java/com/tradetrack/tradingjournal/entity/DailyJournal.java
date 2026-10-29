package com.tradetrack.tradingjournal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_journals", uniqueConstraints = {
    @UniqueConstraint(name = "uk_journal_date", columnNames = {"journal_date"})
})
public class DailyJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_date", nullable = false)
    private LocalDate journalDate;

    @Column(name = "day_of_week", length = 30)
    private String dayOfWeek;

    @Column(name = "starting_balance", precision = 15, scale = 4)
    private BigDecimal startingBalance = BigDecimal.ZERO;

    @Column(name = "closing_balance", precision = 15, scale = 4)
    private BigDecimal closingBalance = BigDecimal.ZERO;

    @Column(name = "closed_pnl", precision = 15, scale = 4)
    private BigDecimal closedPnl = BigDecimal.ZERO;

    @Column(name = "total_brokerage", precision = 15, scale = 4)
    private BigDecimal totalBrokerage = BigDecimal.ZERO;

    @Column(name = "other_charges", precision = 15, scale = 4)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(name = "trade_count")
    private Integer tradeCount = 0;

    @Column(name = "daily_win_rate", precision = 6, scale = 2)
    private BigDecimal dailyWinRate = BigDecimal.ZERO;

    // After Trade Close section from notebook
    @Column(name = "profit_reflection", columnDefinition = "TEXT")
    private String profitReflection;

    @Column(name = "loss_reflection", columnDefinition = "TEXT")
    private String lossReflection;

    @Column(name = "what_went_well", columnDefinition = "TEXT")
    private String whatWentWell;

    @Column(name = "what_went_wrong", columnDefinition = "TEXT")
    private String whatWentWrong;

    // My Observations section from notebook
    @Column(name = "market_movement", columnDefinition = "TEXT")
    private String marketMovement;

    @Column(name = "mistakes", columnDefinition = "TEXT")
    private String mistakes;

    @Column(name = "risk_reward_observations", columnDefinition = "TEXT")
    private String riskRewardObservations;

    // Daily Notes & Psychology
    @Column(name = "market_outlook", columnDefinition = "TEXT")
    private String marketOutlook;

    @Column(name = "emotional_control", columnDefinition = "TEXT")
    private String emotionalControl;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyJournal() {
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

    public LocalDate getJournalDate() { return journalDate; }
    public void setJournalDate(LocalDate journalDate) { this.journalDate = journalDate; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public BigDecimal getStartingBalance() { return startingBalance; }
    public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }

    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }

    public BigDecimal getClosedPnl() { return closedPnl; }
    public void setClosedPnl(BigDecimal closedPnl) { this.closedPnl = closedPnl; }

    public BigDecimal getTotalBrokerage() { return totalBrokerage; }
    public void setTotalBrokerage(BigDecimal totalBrokerage) { this.totalBrokerage = totalBrokerage; }

    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }

    public Integer getTradeCount() { return tradeCount; }
    public void setTradeCount(Integer tradeCount) { this.tradeCount = tradeCount; }

    public BigDecimal getDailyWinRate() { return dailyWinRate; }
    public void setDailyWinRate(BigDecimal dailyWinRate) { this.dailyWinRate = dailyWinRate; }

    public String getProfitReflection() { return profitReflection; }
    public void setProfitReflection(String profitReflection) { this.profitReflection = profitReflection; }

    public String getLossReflection() { return lossReflection; }
    public void setLossReflection(String lossReflection) { this.lossReflection = lossReflection; }

    public String getWhatWentWell() { return whatWentWell; }
    public void setWhatWentWell(String whatWentWell) { this.whatWentWell = whatWentWell; }

    public String getWhatWentWrong() { return whatWentWrong; }
    public void setWhatWentWrong(String whatWentWrong) { this.whatWentWrong = whatWentWrong; }

    public String getMarketMovement() { return marketMovement; }
    public void setMarketMovement(String marketMovement) { this.marketMovement = marketMovement; }

    public String getMistakes() { return mistakes; }
    public void setMistakes(String mistakes) { this.mistakes = mistakes; }

    public String getRiskRewardObservations() { return riskRewardObservations; }
    public void setRiskRewardObservations(String riskRewardObservations) { this.riskRewardObservations = riskRewardObservations; }

    public String getMarketOutlook() { return marketOutlook; }
    public void setMarketOutlook(String marketOutlook) { this.marketOutlook = marketOutlook; }

    public String getEmotionalControl() { return emotionalControl; }
    public void setEmotionalControl(String emotionalControl) { this.emotionalControl = emotionalControl; }

    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
