package com.tradetrack.tradingjournal.dto;

import com.tradetrack.tradingjournal.entity.DailyJournal;
import com.tradetrack.tradingjournal.entity.Trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DailyJournalSummaryResponse {

    private LocalDate journalDate;
    private String dayOfWeek;
    private BigDecimal startingBalance;
    private BigDecimal closingBalance;
    private BigDecimal closedPnl;
    private BigDecimal totalBrokerage;
    private BigDecimal otherCharges;
    private int tradeCount;
    private BigDecimal dailyWinRate;

    private List<Trade> trades;
    private DailyJournal journal;
    private LocalDate prevDate;
    private LocalDate nextDate;

    public DailyJournalSummaryResponse() {
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

    public int getTradeCount() { return tradeCount; }
    public void setTradeCount(int tradeCount) { this.tradeCount = tradeCount; }

    public BigDecimal getDailyWinRate() { return dailyWinRate; }
    public void setDailyWinRate(BigDecimal dailyWinRate) { this.dailyWinRate = dailyWinRate; }

    public List<Trade> getTrades() { return trades; }
    public void setTrades(List<Trade> trades) { this.trades = trades; }

    public DailyJournal getJournal() { return journal; }
    public void setJournal(DailyJournal journal) { this.journal = journal; }

    public LocalDate getPrevDate() { return prevDate; }
    public void setPrevDate(LocalDate prevDate) { this.prevDate = prevDate; }

    public LocalDate getNextDate() { return nextDate; }
    public void setNextDate(LocalDate nextDate) { this.nextDate = nextDate; }
}
