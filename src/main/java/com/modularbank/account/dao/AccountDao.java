package com.modularbank.account.dao;

import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.mapper.AccountMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDao {
    private AccountMapper accountMapper;

    public AccountDao(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public List<Currency> getCurrencies() {
        return accountMapper.getCurrencies();
    }

    public String getCustomer(String customerId) {
        return accountMapper.getCustomer(customerId);
    }

    public String createAccount(Account account) {
        return accountMapper.createAccount(account);
    }

    public List<Account> getAllAccounts() {
        return accountMapper.getAllAccounts();
    }

    public Integer createBalance(Balance balance) {
        return accountMapper.createBalance(balance);
    }

    public Account getAccount(String id) {
        return accountMapper.getAccount(id);
    }

    public Double updateBalance(Balance balance) {
        return accountMapper.updateBalance(balance);
    }
}
