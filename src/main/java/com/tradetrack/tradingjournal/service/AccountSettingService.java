package com.tradetrack.tradingjournal.service;

import com.tradetrack.tradingjournal.entity.AccountSetting;
import com.tradetrack.tradingjournal.entity.AccountTransaction;
import com.tradetrack.tradingjournal.repository.AccountSettingRepository;
import com.tradetrack.tradingjournal.repository.AccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountSettingService {

    private final AccountSettingRepository settingRepository;
    private final AccountTransactionRepository transactionRepository;

    @Autowired
    public AccountSettingService(AccountSettingRepository settingRepository,
                                 AccountTransactionRepository transactionRepository) {
        this.settingRepository = settingRepository;
        this.transactionRepository = transactionRepository;
    }

    public Map<String, String> getAllSettings() {
        List<AccountSetting> list = settingRepository.findAll();
        Map<String, String> map = new HashMap<>();

        // Defaults
        map.put("initialBalance", "100000.00");
        map.put("currency", "INR");
        map.put("currencySymbol", "₹");
        map.put("dateFormat", "YYYY-MM-DD");
        map.put("theme", "light");
        map.put("traderName", "Trader");

        for (AccountSetting s : list) {
            map.put(s.getSettingKey(), s.getSettingValue());
        }
        return map;
    }

    public String getSetting(String key, String defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(AccountSetting::getSettingValue)
                .orElse(defaultValue);
    }

    @Transactional
    public void saveSetting(String key, String value) {
        AccountSetting setting = settingRepository.findBySettingKey(key)
                .orElse(new AccountSetting(key, value));
        setting.setSettingValue(value);
        settingRepository.save(setting);
    }

    @Transactional
    public Map<String, String> updateSettings(Map<String, String> newSettings) {
        for (Map.Entry<String, String> entry : newSettings.entrySet()) {
            saveSetting(entry.getKey(), entry.getValue());
        }
        return getAllSettings();
    }

    public BigDecimal getInitialBalance() {
        String val = getSetting("initialBalance", "100000.00");
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return new BigDecimal("100000.00");
        }
    }

    // Transactions (Deposits / Withdrawals)
    public List<AccountTransaction> getTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateAscCreatedAtAsc();
    }

    @Transactional
    public AccountTransaction addTransaction(String type, BigDecimal amount, LocalDate date, String notes) {
        AccountTransaction tx = new AccountTransaction();
        tx.setType(type.toUpperCase());
        tx.setAmount(amount);
        tx.setTransactionDate(date != null ? date : LocalDate.now());
        tx.setNotes(notes);
        return transactionRepository.save(tx);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
