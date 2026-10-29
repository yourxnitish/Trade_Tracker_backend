package com.tradetrack.tradingjournal.dto;

import com.tradetrack.tradingjournal.entity.Trade;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private BigDecimal startingBalance;
    private BigDecimal currentBalance;
    private BigDecimal totalRealizedPnl;
    private long totalTrades;
    private long closedTrades;
    private long winningTrades;
    private long losingTrades;
    private long breakevenTrades;
    private long openTrades;
    private BigDecimal winRate;
    private BigDecimal totalBrokerage;
    private BigDecimal totalOtherCharges;
    private BigDecimal totalCharges;
    private BigDecimal avgProfitWinningTrade;
    private BigDecimal avgLossLosingTrade;
    private BigDecimal profitFactor;
    private Trade bestTrade;
    private Trade worstTrade;

    private List<Map<String, Object>> dailyPnlList;
    private List<Map<String, Object>> cumulativePnlList;
    private List<Map<String, Object>> monthlyPnlList;
    private List<Map<String, Object>> pnlByInstrument;
    private List<Map<String, Object>> balanceHistory;

    public DashboardSummaryResponse() {
    }

    public BigDecimal getStartingBalance() { return startingBalance; }
    public void setStartingBalance(BigDecimal startingBalance) { this.startingBalance = startingBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public BigDecimal getTotalRealizedPnl() { return totalRealizedPnl; }
    public void setTotalRealizedPnl(BigDecimal totalRealizedPnl) { this.totalRealizedPnl = totalRealizedPnl; }

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }

    public long getClosedTrades() { return closedTrades; }
    public void setClosedTrades(long closedTrades) { this.closedTrades = closedTrades; }

    public long getWinningTrades() { return winningTrades; }
    public void setWinningTrades(long winningTrades) { this.winningTrades = winningTrades; }

    public long getLosingTrades() { return losingTrades; }
    public void setLosingTrades(long losingTrades) { this.losingTrades = losingTrades; }

    public long getBreakevenTrades() { return breakevenTrades; }
    public void setBreakevenTrades(long breakevenTrades) { this.breakevenTrades = breakevenTrades; }

    public long getOpenTrades() { return openTrades; }
    public void setOpenTrades(long openTrades) { this.openTrades = openTrades; }

    public BigDecimal getWinRate() { return winRate; }
    public void setWinRate(BigDecimal winRate) { this.winRate = winRate; }

    public BigDecimal getTotalBrokerage() { return totalBrokerage; }
    public void setTotalBrokerage(BigDecimal totalBrokerage) { this.totalBrokerage = totalBrokerage; }

    public BigDecimal getTotalOtherCharges() { return totalOtherCharges; }
    public void setTotalOtherCharges(BigDecimal otherCharges) { this.totalOtherCharges = otherCharges; }

    public BigDecimal getTotalCharges() { return totalCharges; }
    public void setTotalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; }

    public BigDecimal getAvgProfitWinningTrade() { return avgProfitWinningTrade; }
    public void setAvgProfitWinningTrade(BigDecimal avgProfitWinningTrade) { this.avgProfitWinningTrade = avgProfitWinningTrade; }

    public BigDecimal getAvgLossLosingTrade() { return avgLossLosingTrade; }
    public void setAvgLossLosingTrade(BigDecimal avgLossLosingTrade) { this.avgLossLosingTrade = avgLossLosingTrade; }

    public BigDecimal getProfitFactor() { return profitFactor; }
    public void setProfitFactor(BigDecimal profitFactor) { this.profitFactor = profitFactor; }

    public Trade getBestTrade() { return bestTrade; }
    public void setBestTrade(Trade bestTrade) { this.bestTrade = bestTrade; }

    public Trade getWorstTrade() { return worstTrade; }
    public void setWorstTrade(Trade worstTrade) { this.worstTrade = worstTrade; }

    public List<Map<String, Object>> getDailyPnlList() { return dailyPnlList; }
    public void setDailyPnlList(List<Map<String, Object>> dailyPnlList) { this.dailyPnlList = dailyPnlList; }

    public List<Map<String, Object>> getCumulativePnlList() { return cumulativePnlList; }
    public void setCumulativePnlList(List<Map<String, Object>> cumulativePnlList) { this.cumulativePnlList = cumulativePnlList; }

    public List<Map<String, Object>> getMonthlyPnlList() { return monthlyPnlList; }
    public void setMonthlyPnlList(List<Map<String, Object>> monthlyPnlList) { this.monthlyPnlList = monthlyPnlList; }

    public List<Map<String, Object>> getPnlByInstrument() { return pnlByInstrument; }
    public void setPnlByInstrument(List<Map<String, Object>> pnlByInstrument) { this.pnlByInstrument = pnlByInstrument; }

    public List<Map<String, Object>> getBalanceHistory() { return balanceHistory; }
    public void setBalanceHistory(List<Map<String, Object>> balanceHistory) { this.balanceHistory = balanceHistory; }
}
