package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.DashboardSummaryResponse;
import com.tradetrack.tradingjournal.entity.AccountTransaction;
import com.tradetrack.tradingjournal.entity.Trade;
import com.tradetrack.tradingjournal.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TradeRepository tradeRepository;
    private final AccountSettingService settingService;

    @Autowired
    public DashboardService(TradeRepository tradeRepository, AccountSettingService settingService) {
        this.tradeRepository = tradeRepository;
        this.settingService = settingService;
    }

    public DashboardSummaryResponse getDashboardSummary(String period, LocalDate customStart, LocalDate customEnd) {
        LocalDate[] range = resolveDateRange(period, customStart, customEnd);
        LocalDate startDate = range[0];
        LocalDate endDate = range[1];

        List<Trade> allTrades = tradeRepository.findAllByOrderByTradeDateDescEntryTimeDesc();
        List<Trade> periodTrades = allTrades.stream()
                .filter(t -> (startDate == null || !t.getTradeDate().isBefore(startDate))
                          && (endDate == null || !t.getTradeDate().isAfter(endDate)))
                .collect(Collectors.toList());

        DashboardSummaryResponse response = new DashboardSummaryResponse();

        BigDecimal initialBalance = settingService.getInitialBalance();
        List<AccountTransaction> allTx = settingService.getTransactions();

        // Calculate starting balance as of startDate
        BigDecimal priorPnl = BigDecimal.ZERO;
        BigDecimal priorDeposits = BigDecimal.ZERO;

        for (Trade t : allTrades) {
            if (startDate != null && t.getTradeDate().isBefore(startDate)) {
                if ("CLOSED".equalsIgnoreCase(t.getStatus()) && t.getNetPnl() != null) {
                    priorPnl = priorPnl.add(t.getNetPnl());
                }
            }
        }
        for (AccountTransaction tx : allTx) {
            if (startDate != null && tx.getTransactionDate().isBefore(startDate)) {
                if ("DEPOSIT".equalsIgnoreCase(tx.getType())) {
                    priorDeposits = priorDeposits.add(tx.getAmount());
                } else if ("WITHDRAWAL".equalsIgnoreCase(tx.getType())) {
                    priorDeposits = priorDeposits.subtract(tx.getAmount());
                }
            }
        }

        BigDecimal startingBalance = initialBalance.add(priorPnl).add(priorDeposits).setScale(2, RoundingMode.HALF_UP);
        response.setStartingBalance(startingBalance);

        // Period metrics
        long totalTrades = periodTrades.size();
        long closedTrades = 0;
        long winningTrades = 0;
        long losingTrades = 0;
        long breakevenTrades = 0;
        long openTrades = 0;

        BigDecimal totalRealizedNetPnl = BigDecimal.ZERO;
        BigDecimal totalGrossProfit = BigDecimal.ZERO;
        BigDecimal totalGrossLoss = BigDecimal.ZERO;
        BigDecimal totalBrokerage = BigDecimal.ZERO;
        BigDecimal totalOtherCharges = BigDecimal.ZERO;

        Trade bestTrade = null;
        Trade worstTrade = null;

        for (Trade t : periodTrades) {
            if (t.getBrokerage() != null) totalBrokerage = totalBrokerage.add(t.getBrokerage());
            if (t.getOtherCharges() != null) totalOtherCharges = totalOtherCharges.add(t.getOtherCharges());

            if ("OPEN".equalsIgnoreCase(t.getStatus())) {
                openTrades++;
            } else if ("CLOSED".equalsIgnoreCase(t.getStatus())) {
                closedTrades++;
                BigDecimal net = t.getNetPnl() != null ? t.getNetPnl() : BigDecimal.ZERO;
                BigDecimal gross = t.getGrossPnl() != null ? t.getGrossPnl() : BigDecimal.ZERO;

                totalRealizedNetPnl = totalRealizedNetPnl.add(net);

                if (gross.compareTo(BigDecimal.ZERO) > 0) {
                    totalGrossProfit = totalGrossProfit.add(gross);
                } else if (gross.compareTo(BigDecimal.ZERO) < 0) {
                    totalGrossLoss = totalGrossLoss.add(gross.abs());
                }

                if (net.compareTo(BigDecimal.ZERO) > 0) {
                    winningTrades++;
                } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                    losingTrades++;
                } else {
                    breakevenTrades++;
                }

                if (bestTrade == null || (bestTrade.getNetPnl() != null && net.compareTo(bestTrade.getNetPnl()) > 0)) {
                    bestTrade = t;
                }
                if (worstTrade == null || (worstTrade.getNetPnl() != null && net.compareTo(worstTrade.getNetPnl()) < 0)) {
                    worstTrade = t;
                }
            }
        }

        response.setTotalTrades(totalTrades);
        response.setClosedTrades(closedTrades);
        response.setWinningTrades(winningTrades);
        response.setLosingTrades(losingTrades);
        response.setBreakevenTrades(breakevenTrades);
        response.setOpenTrades(openTrades);
        response.setTotalRealizedPnl(totalRealizedNetPnl.setScale(2, RoundingMode.HALF_UP));
        response.setTotalBrokerage(totalBrokerage.setScale(2, RoundingMode.HALF_UP));
        response.setTotalOtherCharges(totalOtherCharges.setScale(2, RoundingMode.HALF_UP));
        response.setTotalCharges(totalBrokerage.add(totalOtherCharges).setScale(2, RoundingMode.HALF_UP));
        response.setBestTrade(bestTrade);
        response.setWorstTrade(worstTrade);

        // Win Rate
        BigDecimal winRate = closedTrades > 0
                ? BigDecimal.valueOf(winningTrades).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(closedTrades), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        response.setWinRate(winRate);

        // Averages
        BigDecimal avgProfit = winningTrades > 0
                ? totalGrossProfit.divide(BigDecimal.valueOf(winningTrades), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgLoss = losingTrades > 0
                ? totalGrossLoss.divide(BigDecimal.valueOf(losingTrades), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        response.setAvgProfitWinningTrade(avgProfit);
        response.setAvgLossLosingTrade(avgLoss);

        // Profit Factor
        if (totalGrossLoss.compareTo(BigDecimal.ZERO) > 0) {
            response.setProfitFactor(totalGrossProfit.divide(totalGrossLoss, 2, RoundingMode.HALF_UP));
        } else if (totalGrossProfit.compareTo(BigDecimal.ZERO) > 0) {
            response.setProfitFactor(new BigDecimal("99.99")); // High value when no losses
        } else {
            response.setProfitFactor(BigDecimal.ZERO);
        }

        // Current account balance
        BigDecimal currentBalance = startingBalance.add(totalRealizedNetPnl);
        for (AccountTransaction tx : allTx) {
            if ((startDate == null || !tx.getTransactionDate().isBefore(startDate))
             && (endDate == null || !tx.getTransactionDate().isAfter(endDate))) {
                if ("DEPOSIT".equalsIgnoreCase(tx.getType())) {
                    currentBalance = currentBalance.add(tx.getAmount());
                } else if ("WITHDRAWAL".equalsIgnoreCase(tx.getType())) {
                    currentBalance = currentBalance.subtract(tx.getAmount());
                }
            }
        }
        response.setCurrentBalance(currentBalance.setScale(2, RoundingMode.HALF_UP));

        // Generate Charts
        response.setDailyPnlList(generateDailyPnl(periodTrades));
        response.setCumulativePnlList(generateCumulativePnl(periodTrades, startingBalance));
        response.setMonthlyPnlList(generateMonthlyPnl(periodTrades));
        response.setPnlByInstrument(generatePnlByInstrument(periodTrades));
        response.setBalanceHistory(generateBalanceHistory(periodTrades, startingBalance));

        return response;
    }

    private List<Map<String, Object>> generateDailyPnl(List<Trade> trades) {
        Map<LocalDate, List<Trade>> byDate = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.groupingBy(Trade::getTradeDate, TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Trade>> entry : byDate.entrySet()) {
            BigDecimal dayNet = BigDecimal.ZERO;
            BigDecimal dayGross = BigDecimal.ZERO;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) dayNet = dayNet.add(t.getNetPnl());
                if (t.getGrossPnl() != null) dayGross = dayGross.add(t.getGrossPnl());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey().toString());
            map.put("netPnl", dayNet.setScale(2, RoundingMode.HALF_UP));
            map.put("grossPnl", dayGross.setScale(2, RoundingMode.HALF_UP));
            map.put("tradeCount", entry.getValue().size());
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> generateCumulativePnl(List<Trade> trades, BigDecimal startBal) {
        List<Trade> closedSorted = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                .sorted(Comparator.comparing(Trade::getTradeDate).thenComparing(Trade::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<Map<String, Object>> list = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        BigDecimal runningBalance = startBal;

        for (Trade t : closedSorted) {
            BigDecimal net = t.getNetPnl() != null ? t.getNetPnl() : BigDecimal.ZERO;
            cumulative = cumulative.add(net);
            runningBalance = runningBalance.add(net);

            Map<String, Object> map = new HashMap<>();
            map.put("date", t.getTradeDate().toString());
            map.put("symbol", t.getSymbol());
            map.put("tradePnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("cumulativePnl", cumulative.setScale(2, RoundingMode.HALF_UP));
            map.put("balance", runningBalance.setScale(2, RoundingMode.HALF_UP));
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> generateMonthlyPnl(List<Trade> trades) {
        Map<String, List<Trade>> byMonth = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.groupingBy(t -> t.getTradeDate().getYear() + "-" + String.format("%02d", t.getTradeDate().getMonthValue()), TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byMonth.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long winCount = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) winCount++;
                }
            }
            BigDecimal winRate = entry.getValue().size() > 0
                    ? BigDecimal.valueOf(winCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("month", entry.getKey());
            map.put("pnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("trades", entry.getValue().size());
            map.put("winRate", winRate);
            list.add(map);
        }
        return list;
    }

    private List<Map<String, Object>> generatePnlByInstrument(List<Trade> trades) {
        Map<String, List<Trade>> byInst = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.groupingBy(Trade::getInstrumentName, HashMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, List<Trade>> entry : byInst.entrySet()) {
            BigDecimal net = BigDecimal.ZERO;
            long winCount = 0;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) {
                    net = net.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) winCount++;
                }
            }
            BigDecimal winRate = entry.getValue().size() > 0
                    ? BigDecimal.valueOf(winCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            Map<String, Object> map = new HashMap<>();
            map.put("instrument", entry.getKey());
            map.put("pnl", net.setScale(2, RoundingMode.HALF_UP));
            map.put("count", entry.getValue().size());
            map.put("winRate", winRate);
            list.add(map);
        }
        list.sort((a, b) -> ((BigDecimal) b.get("pnl")).compareTo((BigDecimal) a.get("pnl")));
        return list;
    }

    private List<Map<String, Object>> generateBalanceHistory(List<Trade> trades, BigDecimal startingBalance) {
        Map<LocalDate, List<Trade>> byDate = trades.stream()
                .filter(t -> "CLOSED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.groupingBy(Trade::getTradeDate, TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> list = new ArrayList<>();
        BigDecimal current = startingBalance;

        for (Map.Entry<LocalDate, List<Trade>> entry : byDate.entrySet()) {
            BigDecimal dayNet = BigDecimal.ZERO;
            for (Trade t : entry.getValue()) {
                if (t.getNetPnl() != null) dayNet = dayNet.add(t.getNetPnl());
            }
            current = current.add(dayNet);
            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey().toString());
            map.put("balance", current.setScale(2, RoundingMode.HALF_UP));
            list.add(map);
        }
        return list;
    }

    public LocalDate[] resolveDateRange(String period, LocalDate customStart, LocalDate customEnd) {
        LocalDate now = LocalDate.now();
        if ("TODAY".equalsIgnoreCase(period)) {
            return new LocalDate[]{now, now};
        } else if ("THIS_WEEK".equalsIgnoreCase(period)) {
            LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new LocalDate[]{monday, sunday};
        } else if ("THIS_MONTH".equalsIgnoreCase(period)) {
            LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
            return new LocalDate[]{firstDay, lastDay};
        } else if ("THIS_YEAR".equalsIgnoreCase(period)) {
            LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfYear());
            LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfYear());
            return new LocalDate[]{firstDay, lastDay};
        } else if ("CUSTOM".equalsIgnoreCase(period)) {
            return new LocalDate[]{customStart, customEnd};
        }
        // "ALL" or default
        return new LocalDate[]{null, null};
    }
}
