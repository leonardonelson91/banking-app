package com.modularbank.account.report.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.report.entity.Account;
import com.modularbank.account.report.exception.InvalidMessageFormatException;
import com.modularbank.account.report.repository.AccountRepository;
import com.modularbank.account.report.utils.Messages;
import org.springframework.stereotype.Component;

@Component
public class AccountMessageReceiver {

    private AccountRepository accountRepository;

    public AccountMessageReceiver(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void receiveMessage(String message) {
        Account account = null;
        try {
            account = new ObjectMapper().readValue(message, Account.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new InvalidMessageFormatException(Messages.INVALID_MESSAGE_FORMAT);
        }
        accountRepository.save(account);
    }

}
