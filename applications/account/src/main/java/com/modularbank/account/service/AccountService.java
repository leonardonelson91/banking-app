package com.modularbank.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.exception.InvalidCustomerException;
import com.modularbank.account.exception.ServiceUnavailableException;
import com.modularbank.account.utils.AccountUtils;
import com.modularbank.account.utils.Messages;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    private static final String EXCHANGE_NAME = "account-exchange";

    private AccountDao accountDao;
    private AccountUtils accountUtils;
    private RabbitTemplate rabbitTemplate;

    public AccountService(AccountDao accountDao, AccountUtils accountUtils, RabbitTemplate rabbitTemplate) {
        this.accountDao = accountDao;
        this.accountUtils = accountUtils;
        this.rabbitTemplate = rabbitTemplate;
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

        Arrays.stream(currencies).distinct().forEach(currency -> {
            Balance balance = new Balance();
            balance.setAccountId(accountId);
            balance.setAmount(0.0);
            balance.setCurrency(currency);
            accountDao.createBalance(balance);
            account.getBalances().add(balance);
        });
        account.setId(accountId);

        try {
            notifyQueue(account);
        } catch (JsonProcessingException e) {
           throw new ServiceUnavailableException(Messages.SERVICE_UNAVAILABLE);
        }

        return account;
    }

    public List<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    public Account getAccount(String id) {
        return accountUtils.getAccount(id);
    }

    protected void notifyQueue(Account account) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(account);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "account.create", json);
    }
}
