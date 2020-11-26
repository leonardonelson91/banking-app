package com.modularbank.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.dao.TransactionDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Transaction;
import com.modularbank.account.exception.*;
import com.modularbank.account.utils.AccountUtils;
import com.modularbank.account.utils.Messages;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.modularbank.account.entity.Transaction.DIRECTION.IN;

@Service
public class TransactionService {

    private static final String EXCHANGE_NAME = "account-exchange";

    private TransactionDao transactionDao;
    private AccountDao accountDao;
    private AccountUtils accountUtils;
    private RabbitTemplate rabbitTemplate;

    public TransactionService(TransactionDao transactionDao, AccountDao accountDao, AccountUtils accountUtils, RabbitTemplate rabbitTemplate) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.accountUtils = accountUtils;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Transaction createTransaction(Transaction transaction) {
        if(StringUtils.isEmpty(transaction.getDescription())) {
            throw new MissingTransactionDescriptionException(Messages.MISSING_DESCRIPTION);
        }

        Account account = accountUtils.getAccount(transaction.getAccountId());

        accountUtils.validateCurrency(new String[] { transaction.getCurrency() });

        Balance balance = account.getBalances().stream()
                .filter(b -> b.getCurrency().equals(transaction.getCurrency()))
                .findFirst()
                .orElse(null);

        if(balance == null) {
            throw new CurrencyNotAvailableException(Messages.CURRENCY_NOT_AVAILABLE);
        }

        Double newBalance;
        if(transaction.getDirection().equals(IN)) {
            newBalance = balance.getAmount() + transaction.getAmount();
        } else {
            newBalance = balance.getAmount() - transaction.getAmount();
            if(newBalance < 0.0) {
                throw new InsufficientFundsException(Messages.INSUFFICIENT_FUNDS);
            }
        }
        transaction.setBalance(newBalance);
        balance.setAmount(newBalance);

        transaction.setDate(new Date());

        String transactionId = transactionDao.createTransaction(transaction);
        transaction.setId(transactionId);

        Integer balanceIndex = account.getBalances().indexOf(balance);
        account.getBalances().set(balanceIndex, balance);

        accountDao.updateBalance(balance);

        try {
            notifyQueue(transaction);
            notifyQueue(account);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException(Messages.SERVICE_UNAVAILABLE);
        }

        return transaction;
    }

    public List<Transaction> getAccountTransactions(String accountId) {
        Account account = accountUtils.getAccount(accountId);

        List<Transaction> transactions = transactionDao.getAccountTransactions(accountId);

        return transactions == null ? new ArrayList<>() : transactions;
    }

    protected void notifyQueue(Transaction transaction) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(transaction);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "transaction.create", json);
    }

    protected void notifyQueue(Account account) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(account);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "account.update", json);
    }
}
