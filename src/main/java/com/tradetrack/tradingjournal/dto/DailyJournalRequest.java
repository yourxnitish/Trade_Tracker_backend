package com.tradetrack.tradingjournal.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyJournalRequest {

    @NotNull(message = "Journal date is required")
    private LocalDate journalDate;

    private String dayOfWeek;
    private BigDecimal startingBalance;
    private BigDecimal closingBalance;
    private BigDecimal closedPnl;
    private BigDecimal totalBrokerage;
    private BigDecimal otherCharges;
    private Integer tradeCount;
    private BigDecimal dailyWinRate;

    private String profitReflection;
    private String lossReflection;
    private String whatWentWell;
    private String whatWentWrong;

    private String marketMovement;
    private String mistakes;
    private String riskRewardObservations;

    private String marketOutlook;
    private String emotionalControl;
    private String lessonsLearned;
    private String notes;

    public DailyJournalRequest() {
    }

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
}
