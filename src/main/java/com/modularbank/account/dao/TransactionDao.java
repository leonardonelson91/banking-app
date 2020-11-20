package com.modularbank.account.dao;

import com.modularbank.account.entity.Transaction;
import com.modularbank.account.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionDao {

    private TransactionMapper transactionMapper;

    public TransactionDao(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    public String createTransaction(Transaction transaction) {
        return transactionMapper.createTransaction(transaction);
    }

    public List<Transaction> getAccountTransactions(String accountId) {
        return transactionMapper.getAccountTransactions(accountId);
    }
}
