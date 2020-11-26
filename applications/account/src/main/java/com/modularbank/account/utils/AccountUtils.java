package com.modularbank.account.utils;

import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.exception.AccountNotFoundException;
import com.modularbank.account.exception.InvalidCurrencyException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountUtils {

    private AccountDao accountDao;

    public AccountUtils(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Currency> getAllowedCurrencies() {
        return accountDao.getCurrencies();
    }

    public void validateCurrency(String[] currencies) {
        List<Currency> availableCurrencies = getAllowedCurrencies();

        List<String> dirtyCurrencies = Arrays.asList(currencies);
        List<String> allowedCurrencies = availableCurrencies.stream().map(Currency::getCode).collect(Collectors.toList());
        if(!allowedCurrencies.containsAll(dirtyCurrencies)) {
            throw new InvalidCurrencyException(Messages.INVALID_CURRENCIES);
        }
    }

    public Account getAccount(String id) {
        Account account = accountDao.getAccount(id);

        if(account == null) {
            throw new AccountNotFoundException(Messages.ACCOUNT_NOT_FOUND);
        }

        return account;
    }
}
