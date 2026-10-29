package com.tradetrack.tradingjournal.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {

    private BigDecimal totalRealizedNetPnl;
    private BigDecimal totalCharges;
    private long totalTrades;
    private long closedTrades;
    private BigDecimal winRate;
    private BigDecimal profitFactor;
    private BigDecimal avgWinningTrade;
    private BigDecimal avgLosingTrade;
    private BigDecimal largestWinningTrade;
    private BigDecimal largestLosingTrade;
    private int maxConsecutiveWins;
    private int maxConsecutiveLosses;
    private int currentStreak; // Positive for wins, negative for losses

    // Direction breakdown
    private long longTradesCount;
    private BigDecimal longTradesPnl;
    private BigDecimal longWinRate;

    private long shortTradesCount;
    private BigDecimal shortTradesPnl;
    private BigDecimal shortWinRate;

    // Detailed lists
    private List<Map<String, Object>> strategyPerformance;
    private List<Map<String, Object>> instrumentPerformance;
    private List<Map<String, Object>> exchangePerformance;
    private List<Map<String, Object>> calendarHeatmap;
    private List<Map<String, Object>> weeklyPerformance;
    private List<Map<String, Object>> monthlyPerformance;
    private List<Map<String, Object>> cumulativeCurve;

    public AnalyticsResponse() {
    }

    public BigDecimal getTotalRealizedNetPnl() { return totalRealizedNetPnl; }
    public void setTotalRealizedNetPnl(BigDecimal totalRealizedNetPnl) { this.totalRealizedNetPnl = totalRealizedNetPnl; }

    public BigDecimal getTotalCharges() { return totalCharges; }
    public void setTotalCharges(BigDecimal totalCharges) { this.totalCharges = totalCharges; }

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }

    public long getClosedTrades() { return closedTrades; }
    public void setClosedTrades(long closedTrades) { this.closedTrades = closedTrades; }

    public BigDecimal getWinRate() { return winRate; }
    public void setWinRate(BigDecimal winRate) { this.winRate = winRate; }

    public BigDecimal getProfitFactor() { return profitFactor; }
    public void setProfitFactor(BigDecimal profitFactor) { this.profitFactor = profitFactor; }

    public BigDecimal getAvgWinningTrade() { return avgWinningTrade; }
    public void setAvgWinningTrade(BigDecimal avgWinningTrade) { this.avgWinningTrade = avgWinningTrade; }

    public BigDecimal getAvgLosingTrade() { return avgLosingTrade; }
    public void setAvgLosingTrade(BigDecimal avgLosingTrade) { this.avgLosingTrade = avgLosingTrade; }

    public BigDecimal getLargestWinningTrade() { return largestWinningTrade; }
    public void setLargestWinningTrade(BigDecimal largestWinningTrade) { this.largestWinningTrade = largestWinningTrade; }

    public BigDecimal getLargestLosingTrade() { return largestLosingTrade; }
    public void setLargestLosingTrade(BigDecimal largestLosingTrade) { this.largestLosingTrade = largestLosingTrade; }

    public int getMaxConsecutiveWins() { return maxConsecutiveWins; }
    public void setMaxConsecutiveWins(int maxConsecutiveWins) { this.maxConsecutiveWins = maxConsecutiveWins; }

    public int getMaxConsecutiveLosses() { return maxConsecutiveLosses; }
    public void setMaxConsecutiveLosses(int maxConsecutiveLosses) { this.maxConsecutiveLosses = maxConsecutiveLosses; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public long getLongTradesCount() { return longTradesCount; }
    public void setLongTradesCount(long longTradesCount) { this.longTradesCount = longTradesCount; }

    public BigDecimal getLongTradesPnl() { return longTradesPnl; }
    public void setLongTradesPnl(BigDecimal longTradesPnl) { this.longTradesPnl = longTradesPnl; }

    public BigDecimal getLongWinRate() { return longWinRate; }
    public void setLongWinRate(BigDecimal longWinRate) { this.longWinRate = longWinRate; }

    public long getShortTradesCount() { return shortTradesCount; }
    public void setShortTradesCount(long shortTradesCount) { this.shortTradesCount = shortTradesCount; }

    public BigDecimal getShortTradesPnl() { return shortTradesPnl; }
    public void setShortTradesPnl(BigDecimal shortTradesPnl) { this.shortTradesPnl = shortTradesPnl; }

    public BigDecimal getShortWinRate() { return shortWinRate; }
    public void setShortWinRate(BigDecimal shortWinRate) { this.shortWinRate = shortWinRate; }

    public List<Map<String, Object>> getStrategyPerformance() { return strategyPerformance; }
    public void setStrategyPerformance(List<Map<String, Object>> strategyPerformance) { this.strategyPerformance = strategyPerformance; }

    public List<Map<String, Object>> getInstrumentPerformance() { return instrumentPerformance; }
    public void setInstrumentPerformance(List<Map<String, Object>> instrumentPerformance) { this.instrumentPerformance = instrumentPerformance; }

    public List<Map<String, Object>> getExchangePerformance() { return exchangePerformance; }
    public void setExchangePerformance(List<Map<String, Object>> exchangePerformance) { this.exchangePerformance = exchangePerformance; }

    public List<Map<String, Object>> getCalendarHeatmap() { return calendarHeatmap; }
    public void setCalendarHeatmap(List<Map<String, Object>> calendarHeatmap) { this.calendarHeatmap = calendarHeatmap; }

    public List<Map<String, Object>> getWeeklyPerformance() { return weeklyPerformance; }
    public void setWeeklyPerformance(List<Map<String, Object>> weeklyPerformance) { this.weeklyPerformance = weeklyPerformance; }

    public List<Map<String, Object>> getMonthlyPerformance() { return monthlyPerformance; }
    public void setMonthlyPerformance(List<Map<String, Object>> monthlyPerformance) { this.monthlyPerformance = monthlyPerformance; }

    public List<Map<String, Object>> getCumulativeCurve() { return cumulativeCurve; }
    public void setCumulativeCurve(List<Map<String, Object>> cumulativeCurve) { this.cumulativeCurve = cumulativeCurve; }
}
