package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.ObservationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObservationNoteRepository extends JpaRepository<ObservationNote, Long> {

    List<ObservationNote> findAllByOrderByCreatedAtDesc();

    List<ObservationNote> findByCategoryOrderByCreatedAtDesc(String category);

    List<ObservationNote> findByRelatedDateOrderByCreatedAtDesc(LocalDate date);
}
