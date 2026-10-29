package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.DailyJournalRequest;
import com.tradetrack.tradingjournal.dto.DailyJournalSummaryResponse;
import com.tradetrack.tradingjournal.entity.AccountTransaction;
import com.tradetrack.tradingjournal.entity.DailyJournal;
import com.tradetrack.tradingjournal.entity.Trade;
import com.tradetrack.tradingjournal.exception.ResourceNotFoundException;
import com.tradetrack.tradingjournal.repository.DailyJournalRepository;
import com.tradetrack.tradingjournal.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class DailyJournalService {

    private final DailyJournalRepository journalRepository;
    private final TradeRepository tradeRepository;
    private final AccountSettingService settingService;

    @Autowired
    public DailyJournalService(DailyJournalRepository journalRepository,
                               TradeRepository tradeRepository,
                               AccountSettingService settingService) {
        this.journalRepository = journalRepository;
        this.tradeRepository = tradeRepository;
        this.settingService = settingService;
    }

    public List<DailyJournal> getAllJournals() {
        return journalRepository.findAllByOrderByJournalDateDesc();
    }

    public DailyJournal getJournalById(Long id) {
        return journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with id: " + id));
    }

    public DailyJournalSummaryResponse getJournalSummaryForDate(LocalDate date) {
        DailyJournalSummaryResponse response = new DailyJournalSummaryResponse();
        response.setJournalDate(date);
        response.setDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        // Get trades for this date
        List<Trade> trades = tradeRepository.findByTradeDate(date);
        response.setTrades(trades);

        // Calculate trade metrics for the day
        BigDecimal closedPnl = BigDecimal.ZERO;
        BigDecimal totalBrokerage = BigDecimal.ZERO;
        BigDecimal otherCharges = BigDecimal.ZERO;
        int totalClosedTrades = 0;
        int winningTrades = 0;

        for (Trade t : trades) {
            if (t.getBrokerage() != null) totalBrokerage = totalBrokerage.add(t.getBrokerage());
            if (t.getOtherCharges() != null) otherCharges = otherCharges.add(t.getOtherCharges());

            if ("CLOSED".equalsIgnoreCase(t.getStatus())) {
                totalClosedTrades++;
                if (t.getNetPnl() != null) {
                    closedPnl = closedPnl.add(t.getNetPnl());
                    if (t.getNetPnl().compareTo(BigDecimal.ZERO) > 0) {
                        winningTrades++;
                    }
                }
            }
        }

        BigDecimal winRate = totalClosedTrades > 0
                ? BigDecimal.valueOf(winningTrades)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalClosedTrades), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        response.setClosedPnl(closedPnl.setScale(2, RoundingMode.HALF_UP));
        response.setTotalBrokerage(totalBrokerage.setScale(2, RoundingMode.HALF_UP));
        response.setOtherCharges(otherCharges.setScale(2, RoundingMode.HALF_UP));
        response.setTradeCount(trades.size());
        response.setDailyWinRate(winRate);

        // Calculate balances dynamically
        BigDecimal initialBalance = settingService.getInitialBalance();
        BigDecimal priorRealizedPnl = calculatePriorPnl(date);
        BigDecimal priorNetDeposits = calculatePriorDeposits(date);

        BigDecimal startingBalance = initialBalance.add(priorRealizedPnl).add(priorNetDeposits).setScale(2, RoundingMode.HALF_UP);
        BigDecimal closingBalance = startingBalance.add(closedPnl).setScale(2, RoundingMode.HALF_UP);

        response.setStartingBalance(startingBalance);
        response.setClosingBalance(closingBalance);

        // Check if journal entry exists in DB
        Optional<DailyJournal> existing = journalRepository.findByJournalDate(date);
        existing.ifPresent(response::setJournal);

        // Previous and Next date navigation
        response.setPrevDate(date.minusDays(1));
        response.setNextDate(date.plusDays(1));

        return response;
    }

    private BigDecimal calculatePriorPnl(LocalDate date) {
        List<Trade> allTrades = tradeRepository.findAll();
        BigDecimal pnl = BigDecimal.ZERO;
        for (Trade t : allTrades) {
            if (t.getTradeDate().isBefore(date) && "CLOSED".equalsIgnoreCase(t.getStatus()) && t.getNetPnl() != null) {
                pnl = pnl.add(t.getNetPnl());
            }
        }
        return pnl;
    }

    private BigDecimal calculatePriorDeposits(LocalDate date) {
        List<AccountTransaction> txList = settingService.getTransactions();
        BigDecimal total = BigDecimal.ZERO;
        for (AccountTransaction tx : txList) {
            if (tx.getTransactionDate().isBefore(date)) {
                if ("DEPOSIT".equalsIgnoreCase(tx.getType())) {
                    total = total.add(tx.getAmount());
                } else if ("WITHDRAWAL".equalsIgnoreCase(tx.getType())) {
                    total = total.subtract(tx.getAmount());
                }
            }
        }
        return total;
    }

    @Transactional
    public DailyJournal saveOrUpdateJournal(DailyJournalRequest request) {
        LocalDate date = request.getJournalDate();
        DailyJournal journal = journalRepository.findByJournalDate(date)
                .orElse(new DailyJournal());

        journal.setJournalDate(date);
        journal.setDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

        // Always calculate real metrics for consistency
        DailyJournalSummaryResponse summary = getJournalSummaryForDate(date);
        journal.setStartingBalance(summary.getStartingBalance());
        journal.setClosingBalance(summary.getClosingBalance());
        journal.setClosedPnl(summary.getClosedPnl());
        journal.setTotalBrokerage(summary.getTotalBrokerage());
        journal.setOtherCharges(summary.getOtherCharges());
        journal.setTradeCount(summary.getTradeCount());
        journal.setDailyWinRate(summary.getDailyWinRate());

        // Text observations and reflections
        journal.setProfitReflection(request.getProfitReflection());
        journal.setLossReflection(request.getLossReflection());
        journal.setWhatWentWell(request.getWhatWentWell());
        journal.setWhatWentWrong(request.getWhatWentWrong());
        journal.setMarketMovement(request.getMarketMovement());
        journal.setMistakes(request.getMistakes());
        journal.setRiskRewardObservations(request.getRiskRewardObservations());
        journal.setMarketOutlook(request.getMarketOutlook());
        journal.setEmotionalControl(request.getEmotionalControl());
        journal.setLessonsLearned(request.getLessonsLearned());
        journal.setNotes(request.getNotes());

        return journalRepository.save(journal);
    }

    @Transactional
    public void deleteJournal(Long id) {
        DailyJournal journal = getJournalById(id);
        journalRepository.delete(journal);
    }
}
