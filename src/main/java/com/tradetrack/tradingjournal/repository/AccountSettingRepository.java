package com.tradetrack.tradingjournal.repository;

import com.tradetrack.tradingjournal.entity.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSettingRepository extends JpaRepository<AccountSetting, Long> {

    Optional<AccountSetting> findBySettingKey(String settingKey);
}
