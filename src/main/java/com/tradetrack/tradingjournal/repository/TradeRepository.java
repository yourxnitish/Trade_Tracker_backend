package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long>, JpaSpecificationExecutor<Trade> {

    List<Trade> findByTradeDate(LocalDate tradeDate);

    List<Trade> findByTradeDateBetweenOrderByTradeDateAscEntryTimeAsc(LocalDate startDate, LocalDate endDate);

    List<Trade> findAllByOrderByTradeDateDescEntryTimeDesc();

    @Query("SELECT DISTINCT t.instrumentName FROM Trade t WHERE t.instrumentName IS NOT NULL ORDER BY t.instrumentName ASC")
    List<String> findDistinctInstrumentNames();

    @Query("SELECT DISTINCT t.strategy FROM Trade t WHERE t.strategy IS NOT NULL AND t.strategy <> '' ORDER BY t.strategy ASC")
    List<String> findDistinctStrategies();

    @Query("SELECT DISTINCT t.exchange FROM Trade t WHERE t.exchange IS NOT NULL AND t.exchange <> '' ORDER BY t.exchange ASC")
    List<String> findDistinctExchanges();
}
