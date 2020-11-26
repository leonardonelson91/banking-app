package com.modularbank.account.report.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.report.entity.Account;
import com.modularbank.account.report.entity.Transaction;
import com.modularbank.account.report.exception.InvalidMessageFormatException;
import com.modularbank.account.report.repository.AccountRepository;
import com.modularbank.account.report.repository.TransactionRepository;
import com.modularbank.account.report.utils.Messages;
import org.springframework.stereotype.Component;

@Component
public class TransactionMessageReceiver {

    private TransactionRepository transactionRepository;

    public TransactionMessageReceiver(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void receiveMessage(String message) {
        Transaction transaction = null;
        try {
            transaction = new ObjectMapper().readValue(message, Transaction.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new InvalidMessageFormatException(Messages.INVALID_MESSAGE_FORMAT);
        }
        transactionRepository.save(transaction);
    }

}
