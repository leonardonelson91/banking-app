package com.modularbank.account.service;

import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.exception.AccountNotFoundException;
import com.modularbank.account.exception.InvalidCurrencyException;
import com.modularbank.account.exception.InvalidCustomerException;
import com.modularbank.account.utils.AccountUtils;
import com.modularbank.account.utils.Messages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private AccountDao accountDao;
    private AccountUtils accountUtils;

    public AccountService(AccountDao accountDao, AccountUtils accountUtils) {
        this.accountDao = accountDao;
        this.accountUtils = accountUtils;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Account createAccount(String customerId, String country, String[] currencies) {
        accountUtils.validateCurrency(currencies);

        if(accountDao.getCustomer(customerId) == null) {
            throw new InvalidCustomerException(Messages.INVALID_CUSTOMER);
        }

        Account account = new Account();
        account.setCustomerId(customerId);
        account.setCountry(country);

        String accountId = accountDao.createAccount(account);

        Arrays.stream(currencies).forEach(currency -> {
            Balance balance = new Balance();
            balance.setAccountId(accountId);
            balance.setAmount(0.0);
            balance.setCurrency(currency);
            accountDao.createBalance(balance);
            account.getBalances().add(balance);
        });
        account.setId(accountId);

        return account;
    }

    public List<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    public Account getAccount(String id) {
        return accountUtils.getAccount(id);
    }
}
