package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findAllByOrderByCreatedAtDesc();

    List<WatchlistItem> findByStatusOrderByCreatedAtDesc(String status);
}
