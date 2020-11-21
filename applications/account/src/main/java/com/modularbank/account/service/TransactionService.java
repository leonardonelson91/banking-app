package com.modularbank.account.service;

import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.dao.TransactionDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Transaction;
import com.modularbank.account.exception.*;
import com.modularbank.account.utils.AccountUtils;
import com.modularbank.account.utils.Messages;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.modularbank.account.entity.Transaction.DIRECTION.IN;

@Service
public class TransactionService {

    private TransactionDao transactionDao;
    private AccountDao accountDao;
    private AccountUtils accountUtils;

    public TransactionService(TransactionDao transactionDao, AccountDao accountDao, AccountUtils accountUtils) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.accountUtils = accountUtils;
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

        String transactionId = transactionDao.createTransaction(transaction);
        transaction.setId(transactionId);

        accountDao.updateBalance(balance);

        return transaction;
    }

    public List<Transaction> getAccountTransactions(String accountId) {
        Account account = accountUtils.getAccount(accountId);

        List<Transaction> transactions = transactionDao.getAccountTransactions(accountId);

        return transactions == null ? new ArrayList<>() : transactions;
    }
}
