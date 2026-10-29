package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.DailyJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyJournalRepository extends JpaRepository<DailyJournal, Long> {

    Optional<DailyJournal> findByJournalDate(LocalDate journalDate);

    boolean existsByJournalDate(LocalDate journalDate);

    List<DailyJournal> findByJournalDateBetweenOrderByJournalDateAsc(LocalDate startDate, LocalDate endDate);

    List<DailyJournal> findAllByOrderByJournalDateDesc();

    Optional<DailyJournal> findFirstByJournalDateLessThanOrderByJournalDateDesc(LocalDate journalDate);

    Optional<DailyJournal> findFirstByJournalDateGreaterThanOrderByJournalDateAsc(LocalDate journalDate);
}
