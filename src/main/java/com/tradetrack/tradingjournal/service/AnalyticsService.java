package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.AnalyticsResponse;
import com.tradetrack.tradingjournal.entity.Trade;
import com.tradetrack.tradingjournal.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TradeRepository tradeRepository;

    @Autowired
    public AnalyticsService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public AnalyticsResponse getAnalytics(LocalDate startDate, LocalDate endDate, String instrument, String strategy) {
        List<Trade> trades = tradeRepository.findAllByOrderByTradeDateDescEntryTimeDesc();

        // Apply filters
        trades = trades.stream()
                .filter(t -> (startDate == null || !t.getTradeDate().isBefore(startDate))
                          && (endDate == null || !t.getTradeDate().isAfter(endDate))
                          && (instrument == null || instrument.equalsIgnoreCase("ALL") || t.getInstrumentName().equalsIgnoreCase(instrument))
                          && (strategy == null || strategy.equalsIgnoreCase("ALL") || (t.getStrategy() != null && t.getStrategy().equalsIgnoreCase(strategy))))
                .collect(Collectors.toList());

        AnalyticsResponse res = new AnalyticsResponse();
        res.setTotalTrades(trades.size());

        List<Trade> closedTrades = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()) && t.getNetPnl() != null)
                .sorted(Comparator.comparing(Trade::getTradeDate).thenComparing(Trade::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        res.setClosedTrades(closedTrades.size());

        BigDecimal totalRealizedNetPnl = BigDecimal.ZERO;
        BigDecimal totalCharges = BigDecimal.ZERO;
        BigDecimal totalGrossProfit = BigDecimal.ZERO;
        BigDecimal totalGrossLoss = BigDecimal.ZERO;

        BigDecimal largestWin = BigDecimal.ZERO;
        BigDecimal largestLoss = BigDecimal.ZERO;

        long winCount = 0;
        long lossCount = 0;

        // Streaks calculation
        int currentStreak = 0;
        int maxConsecutiveWins = 0;
        int maxConsecutiveLosses = 0;
        int tempWins = 0;
        int tempLosses = 0;

        // Long vs Short stats
        long longCount = 0;
        long longWins = 0;
        BigDecimal longPnl = BigDecimal.ZERO;

        long shortCount = 0;
        long shortWins = 0;
        BigDecimal shortPnl = BigDecimal.ZERO;

        for (Trade t : closedTrades) {
            BigDecimal net = t.getNetPnl();
            BigDecimal gross = t.getGrossPnl() != null ? t.getGrossPnl() : BigDecimal.ZERO;
            BigDecimal brokerage = t.getBrokerage() != null ? t.getBrokerage() : BigDecimal.ZERO;
            BigDecimal otherCharges = t.getOtherCharges() != null ? t.getOtherCharges() : BigDecimal.ZERO;

            totalRealizedNetPnl = totalRealizedNetPnl.add(net);
            totalCharges = totalCharges.add(brokerage).add(otherCharges);

            if (gross.compareTo(BigDecimal.ZERO) > 0) {
                totalGrossProfit = totalGrossProfit.add(gross);
            } else if (gross.compareTo(BigDecimal.ZERO) < 0) {
                totalGrossLoss = totalGrossLoss.add(gross.abs());
            }

            // Largest win/loss
            if (net.compareTo(largestWin) > 0) {
                largestWin = net;
            }
            if (net.compareTo(largestLoss) < 0) {
                largestLoss = net;
            }

            // Streak & Win/Loss counts
            if (net.compareTo(BigDecimal.ZERO) > 0) {
                winCount++;
                tempWins++;
                tempLosses = 0;
                if (tempWins > maxConsecutiveWins) maxConsecutiveWins = tempWins;
                if (currentStreak >= 0) currentStreak++;
                else currentStreak = 1;
            } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                lossCount++;
                tempLosses++;
                tempWins = 0;
                if (tempLosses > maxConsecutiveLosses) maxConsecutiveLosses = tempLosses;
                if (currentStreak <= 0) currentStreak--;
                else currentStreak = -1;
            } else {
                tempWins = 0;
                tempLosses = 0;
            }

            // Long vs Short
            if ("LONG".equalsIgnoreCase(t.getDirection())) {
                longCount++;
                longPnl = longPnl.add(net);
                if (net.compareTo(BigDecimal.ZERO) > 0) longWins++;
            } else if ("SHORT".equalsIgnoreCase(t.getDirection())) {
                shortCount++;
                shortPnl = shortPnl.add(net);
                if (net.compareTo(BigDecimal.ZERO) > 0) shortWins++;
            }
        }

        res.setTotalRealizedNetPnl(totalRealizedNetPnl.setScale(2, RoundingMode.HALF_UP));
        res.setTotalCharges(totalCharges.setScale(2, RoundingMode.HALF_UP));
        res.setLargestWinningTrade(largestWin.setScale(2, RoundingMode.HALF_UP));
        res.setLargestLosingTrade(largestLoss.setScale(2, RoundingMode.HALF_UP));
        res.setMaxConsecutiveWins(maxConsecutiveWins);
        res.setMaxConsecutiveLosses(maxConsecutiveLosses);
        res.setCurrentStreak(currentStreak);

        // Win rate
        BigDecimal winRate = closedTrades.size() > 0
                ? BigDecimal.valueOf(winCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(closedTrades.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        res.setWinRate(winRate);

        // Profit factor
        if (totalGrossLoss.compareTo(BigDecimal.ZERO) > 0) {
            res.setProfitFactor(totalGrossProfit.divide(totalGrossLoss, 2, RoundingMode.HALF_UP));
        } else if (totalGrossProfit.compareTo(BigDecimal.ZERO) > 0) {
            res.setProfitFactor(new BigDecimal("99.99"));
        } else {
            res.setProfitFactor(BigDecimal.ZERO);
        }

        // Averages
        BigDecimal avgWin = winCount > 0 ? totalGrossProfit.divide(BigDecimal.valueOf(winCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgLoss = lossCount > 0 ? totalGrossLoss.divide(BigDecimal.valueOf(lossCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        res.setAvgWinningTrade(avgWin);
        res.setAvgLosingTrade(avgLoss);

        // Long vs Short
        res.setLongTradesCount(longCount);
        res.setLongTradesPnl(longPnl.setScale(2, RoundingMode.HALF_UP));
        res.setLongWinRate(longCount > 0 ? BigDecimal.valueOf(longWins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(longCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        res.setShortTradesCount(shortCount);
        res.setShortTradesPnl(shortPnl.setScale(2, RoundingMode.HALF_UP));
        res.setShortWinRate(shortCount > 0 ? BigDecimal.valueOf(shortWins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(shortCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // Aggregations
        res.setStrategyPerformance(calculateStrategyPerformance(closedTrades));
        res.setInstrumentPerformance(calculateInstrumentPerformance(closedTrades));
        res.setExchangePerformance(calculateExchangePerformance(closedTrades));
        res.setCalendarHeatmap(calculateCalendarHeatmap(closedTrades));
        res.setWeeklyPerformance(calculateWeeklyPerformance(closedTrades));
        res.setMonthlyPerformance(calculateMonthlyPerformance(closedTrades));
        res.setCumulativeCurve(calculateCumulativeCurve(closedTrades));

        return res;
    }

    private List<Map<String, Object>> calculateStrategyPerformance(List<Trade> trades) {
        Map<String, List<Trade>> byStrategy = trades.stream()
                .collect(Collectors.groupingBy(t -> (t.getStrategy() != null && !t.getStrategy().trim().isEmpty()) ? t.getStrategy() : "Unspecified"));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byStrategy.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long wins = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) wins++;
                }
            }
            int count = entry.getValue().size();
            BigDecimal winRate = count > 0 ? BigDecimal.valueOf(wins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal avgPnl = count > 0 ? net.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("strategy", entry.getKey());
            map.put("count", count);
            map.put("winRate", winRate);
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("avgPnl", avgPnl);
            list.add(map);
        }
        list.sort((a, b) -> ((BigDecimal) b.get("netPnl")).compareTo((BigDecimal) a.get("netPnl")));
        return list;
    }

    private List<Map<String, Object>> calculateInstrumentPerformance(List<Trade> trades) {
        Map<String, List<Trade>> byInst = trades.stream()
                .collect(Collectors.groupingBy(Trade::getInstrumentName));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byInst.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long wins = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) wins++;
                }
            }
            int count = entry.getValue().size();
            BigDecimal winRate = count > 0 ? BigDecimal.valueOf(wins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("instrument", entry.getKey());
            map.put("count", count);
            map.put("winRate", winRate);
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            list.add(map);
        }
        list.sort((a, b) -> ((BigDecimal) b.get("netPnl")).compareTo((BigDecimal) a.get("netPnl")));
        return list;
    }

    private List<Map<String, Object>> calculateExchangePerformance(List<Trade> trades) {
        Map<String, List<Trade>> byEx = trades.stream()
                .collect(Collectors.groupingBy(t -> t.getExchange() != null ? t.getExchange() : "OTHER"));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byEx.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long wins = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) wins++;
                }
            }
            int count = entry.getValue().size();
            BigDecimal winRate = count > 0 ? BigDecimal.valueOf(wins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("exchange", entry.getKey());
            map.put("count", count);
            map.put("winRate", winRate);
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> calculateCalendarHeatmap(List<Trade> trades) {
        Map<LocalDate, List<Trade>> byDate = trades.stream()
                .collect(Collectors.groupingBy(Trade::getTradeDate, TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Trade>> entry : byDate.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            int count = entry.getValue().size();
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) net = net.add(t.getNetPnl());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey().toString());
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("tradeCount", count);
            map.put("status", net.compareTo(BigDecimal.ZERO) > 0 ? "PROFIT" : (net.compareTo(BigDecimal.ZERO) < 0 ? "LOSS" : "NEUTRAL"));
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> calculateWeeklyPerformance(List<Trade> trades) {
        Map<String, List<Trade>> byWeek = trades.stream()
                .collect(Collectors.groupingBy(t -> t.getTradeDate().getYear() + "-W" + String.format("%02d", t.getTradeDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)), TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byWeek.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) net = net.add(t.getNetPnl());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("week", entry.getKey());
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("tradeCount", entry.getValue().size());
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> calculateMonthlyPerformance(List<Trade> trades) {
        Map<String, List<Trade>> byMonth = trades.stream()
                .collect(Collectors.groupingBy(t -> t.getTradeDate().getYear() + "-" + String.format("%02d", t.getTradeDate().getMonthValue()), TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byMonth.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long wins = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) wins++;
                }
            }
            int count = entry.getValue().size();
            BigDecimal winRate = count > 0 ? BigDecimal.valueOf(wins).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("month", entry.getKey());
            map.put("netPnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("tradeCount", count);
            map.put("winRate", winRate);
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> calculateCumulativeCurve(List<Trade> trades) {
        List<Map<String, Object>> list = new ArrayList<>();
        BigDecimal cum = BigDecimal.ZERO;
        for (Trade t : trades) {
            if (t.getNetPnl() != null) {
                cum = cum.add(t.getNetPnl());
                Map<String, Object> map = new HashMap<>();
                map.put("date", t.getTradeDate().toString());
                map.put("symbol", t.getSymbol());
                map.put("cumulativePnl", cum.setScale(2, RoundingMode.HALF_UP));
                list.add(map);
            }
        }
        return list;
    }
}
