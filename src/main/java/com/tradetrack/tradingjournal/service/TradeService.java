package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.dto.TradeRequest;
import com.tradetrack.tradingjournal.entity.Trade;
import com.tradetrack.tradingjournal.exception.ResourceNotFoundException;
import com.tradetrack.tradingjournal.repository.TradeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAllByOrderByTradeDateDescEntryTimeDesc();
    }

    public Trade getTradeById(Long id) {
        return tradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trade not found with id: " + id));
    }

    public Page<Trade> searchAndFilterTrades(
            String search,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String outcome, // WIN, LOSS, BREAKEVEN
            String direction,
            String instrument,
            String exchange,
            String strategy,
            Pageable pageable) {

        Specification<Trade> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("symbol")), term),
                        cb.like(cb.lower(root.get("instrumentName")), term),
                        cb.like(cb.lower(root.get("strategy")), term)
                ));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tradeDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tradeDate"), endDate));
            }

            if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }

            if (direction != null && !direction.trim().isEmpty() && !direction.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.upper(root.get("direction")), direction.trim().toUpperCase()));
            }

            if (instrument != null && !instrument.trim().isEmpty() && !instrument.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.lower(root.get("instrumentName")), instrument.trim().toLowerCase()));
            }

            if (exchange != null && !exchange.trim().isEmpty() && !exchange.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.upper(root.get("exchange")), exchange.trim().toUpperCase()));
            }

            if (strategy != null && !strategy.trim().isEmpty() && !strategy.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.lower(root.get("strategy")), strategy.trim().toLowerCase()));
            }

            if (outcome != null && !outcome.trim().isEmpty() && !outcome.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(cb.upper(root.get("status")), "CLOSED"));
                if ("WIN".equalsIgnoreCase(outcome)) {
                    predicates.add(cb.greaterThan(root.get("netPnl"), BigDecimal.ZERO));
                } else if ("LOSS".equalsIgnoreCase(outcome)) {
                    predicates.add(cb.lessThan(root.get("netPnl"), BigDecimal.ZERO));
                } else if ("BREAKEVEN".equalsIgnoreCase(outcome)) {
                    predicates.add(cb.equal(root.get("netPnl"), BigDecimal.ZERO));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tradeRepository.findAll(spec, pageable);
    }

    public List<Trade> getTradesForDate(LocalDate date) {
        return tradeRepository.findByTradeDate(date);
    }

    @Transactional
    public Trade createTrade(TradeRequest request) {
        Trade trade = new Trade();
        copyProperties(request, trade);
        calculateTradeMetrics(trade);
        return tradeRepository.save(trade);
    }

    @Transactional
    public Trade updateTrade(Long id, TradeRequest request) {
        Trade trade = getTradeById(id);
        copyProperties(request, trade);
        calculateTradeMetrics(trade);
        return tradeRepository.save(trade);
    }

    @Transactional
    public void deleteTrade(Long id) {
        Trade trade = getTradeById(id);
        tradeRepository.delete(trade);
    }

    public List<String> getDistinctInstruments() {
        return tradeRepository.findDistinctInstrumentNames();
    }

    public List<String> getDistinctStrategies() {
        return tradeRepository.findDistinctStrategies();
    }

    public List<String> getDistinctExchanges() {
        return tradeRepository.findDistinctExchanges();
    }

    private void copyProperties(TradeRequest request, Trade trade) {
        trade.setTradeDate(request.getTradeDate());
        trade.setEntryTime(request.getEntryTime());
        trade.setExitTime(request.getExitTime());
        trade.setInstrumentName(request.getInstrumentName().trim());
        trade.setSymbol(request.getSymbol().trim().toUpperCase());
        trade.setExchange(request.getExchange() != null ? request.getExchange().trim().toUpperCase() : "NSE");
        trade.setSegment(request.getSegment() != null ? request.getSegment().trim() : "Equity");
        trade.setDirection(request.getDirection().trim().toUpperCase());
        trade.setStatus(request.getStatus().trim().toUpperCase());
        trade.setEntryPrice(request.getEntryPrice());
        trade.setExitPrice(request.getExitPrice());
        trade.setQuantity(request.getQuantity());
        trade.setTargetPrice(request.getTargetPrice());
        trade.setStopLoss(request.getStopLoss());
        trade.setBrokerage(request.getBrokerage() != null ? request.getBrokerage() : BigDecimal.ZERO);
        trade.setOtherCharges(request.getOtherCharges() != null ? request.getOtherCharges() : BigDecimal.ZERO);
        trade.setStrategy(request.getStrategy());
        trade.setEntryReason(request.getEntryReason());
        trade.setExitReason(request.getExitReason());
        trade.setMarketConditions(request.getMarketConditions());
        trade.setEmotionalStateBefore(request.getEmotionalStateBefore());
        trade.setEmotionalStateAfter(request.getEmotionalStateAfter());
        trade.setMistakes(request.getMistakes());
        trade.setLessonsLearned(request.getLessonsLearned());
        trade.setNotes(request.getNotes());
    }

    public void calculateTradeMetrics(Trade trade) {
        BigDecimal entryPrice = trade.getEntryPrice() != null ? trade.getEntryPrice() : BigDecimal.ZERO;
        BigDecimal exitPrice = trade.getExitPrice();
        BigDecimal quantity = trade.getQuantity() != null ? trade.getQuantity() : BigDecimal.ZERO;
        BigDecimal brokerage = trade.getBrokerage() != null ? trade.getBrokerage() : BigDecimal.ZERO;
        BigDecimal otherCharges = trade.getOtherCharges() != null ? trade.getOtherCharges() : BigDecimal.ZERO;
        boolean isClosed = "CLOSED".equalsIgnoreCase(trade.getStatus()) && exitPrice != null;
        boolean isLong = "LONG".equalsIgnoreCase(trade.getDirection());

        // Realized PnL calculation
        if (isClosed) {
            BigDecimal priceDiff;
            if (isLong) {
                priceDiff = exitPrice.subtract(entryPrice);
            } else {
                priceDiff = entryPrice.subtract(exitPrice);
            }
            BigDecimal grossPnl = priceDiff.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netPnl = grossPnl.subtract(brokerage).subtract(otherCharges).setScale(2, RoundingMode.HALF_UP);
            trade.setGrossPnl(grossPnl);
            trade.setNetPnl(netPnl);
        } else {
            trade.setGrossPnl(BigDecimal.ZERO);
            trade.setNetPnl(BigDecimal.ZERO);
        }

        // Potential Risk and Reward calculation
        BigDecimal targetPrice = trade.getTargetPrice();
        BigDecimal stopLoss = trade.getStopLoss();

        if (targetPrice != null && stopLoss != null && entryPrice.compareTo(BigDecimal.ZERO) > 0 && quantity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal riskPerUnit;
            BigDecimal rewardPerUnit;

            if (isLong) {
                riskPerUnit = entryPrice.subtract(stopLoss).abs();
                rewardPerUnit = targetPrice.subtract(entryPrice).abs();
            } else {
                riskPerUnit = stopLoss.subtract(entryPrice).abs();
                rewardPerUnit = entryPrice.subtract(targetPrice).abs();
            }

            BigDecimal riskAmount = riskPerUnit.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            BigDecimal rewardAmount = rewardPerUnit.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
            trade.setRiskAmount(riskAmount);
            trade.setRewardAmount(rewardAmount);

            if (riskAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rrRatio = rewardAmount.divide(riskAmount, 2, RoundingMode.HALF_UP);
                trade.setRiskRewardRatio(rrRatio);
            } else {
                trade.setRiskRewardRatio(BigDecimal.ZERO);
            }
        } else {
            trade.setRiskAmount(BigDecimal.ZERO);
            trade.setRewardAmount(BigDecimal.ZERO);
            trade.setRiskRewardRatio(BigDecimal.ZERO);
        }
    }
}
