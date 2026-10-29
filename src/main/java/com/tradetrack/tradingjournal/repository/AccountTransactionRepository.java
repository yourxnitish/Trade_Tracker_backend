package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findAllByOrderByTransactionDateAscCreatedAtAsc();
}
