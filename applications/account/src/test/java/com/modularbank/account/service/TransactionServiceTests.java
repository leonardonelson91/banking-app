package com.modularbank.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.dao.TransactionDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.entity.Transaction;
import com.modularbank.account.exception.CurrencyNotAvailableException;
import com.modularbank.account.exception.InsufficientFundsException;
import com.modularbank.account.exception.InvalidCurrencyException;
import com.modularbank.account.exception.MissingTransactionDescriptionException;
import com.modularbank.account.utils.AccountUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTests {

    @Mock
    TransactionDao transactionDao;

    @Mock
    AccountDao accountDao;

    @Mock
    AccountUtils accountUtils;

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    TransactionService transactionService;

    @Test(expected = MissingTransactionDescriptionException.class)
    public void createTransaction_must_throw_exception_when_description_not_provided() {
        transactionService.createTransaction(new Transaction());
    }

    @Test(expected = InvalidCurrencyException.class)
    public void createTransaction_must_throw_exception_when_currency_is_not_valid() {
        Transaction transaction = new Transaction();
        transaction.setCurrency("BRL");
        transaction.setDescription("Test transaction");
        doCallRealMethod().when(accountUtils).validateCurrency(any());

        transactionService.createTransaction(transaction);
    }

    @Test(expected = CurrencyNotAvailableException.class)
    public void createTransaction_must_throw_exception_when_transaction_currency_is_not_available_in_account() {
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setCurrency("USD");
        transaction.setDescription("Test transaction");

        Account account = new Account();
        Balance balance = new Balance();
        balance.setCurrency("EUR");
        account.getBalances().add(balance);
        when(accountUtils.getAccount(anyString())).thenReturn(account);

        transactionService.createTransaction(transaction);
    }

    @Test(expected = InsufficientFundsException.class)
    public void createTransaction_must_throw_exception_when_account_has_no_funds() {
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setCurrency("EUR");
        transaction.setDirection(Transaction.DIRECTION.OUT);
        transaction.setDescription("Test transaction");
        transaction.setAmount(10.0);

        Account account = new Account();
        Balance balance = new Balance();
        balance.setCurrency("EUR");
        balance.setAmount(0.0);
        account.getBalances().add(balance);
        when(accountUtils.getAccount(anyString())).thenReturn(account);

        transactionService.createTransaction(transaction);
    }

    @Test
    public void createTransaction_must_increase_balance() {
        String transactionId = "1234567";
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setCurrency("EUR");
        transaction.setDirection(Transaction.DIRECTION.IN);
        transaction.setDescription("Sallary");
        transaction.setAmount(10.0);

        Account account = new Account();
        Balance balance = new Balance();
        balance.setCurrency("EUR");
        balance.setAmount(0.0);
        account.getBalances().add(balance);
        when(accountUtils.getAccount(anyString())).thenReturn(account);
        when(transactionDao.createTransaction(transaction)).thenReturn(transactionId);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        transaction = transactionService.createTransaction(transaction);

        assertEquals(10.0, transaction.getBalance(), 0.0);
        assertNotNull(transaction.getDate());
        assertEquals(transactionId, transaction.getId());

        verify(accountDao).updateBalance(balanceCaptor.capture());
        Balance updatedBalance = balanceCaptor.getValue();

        assertEquals(10.0, updatedBalance.getAmount(), 0.0);
    }

    @Test
    public void createTransaction_must_decrease_balance() {
        String transactionId = "1234567";
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setCurrency("EUR");
        transaction.setDirection(Transaction.DIRECTION.OUT);
        transaction.setDescription("Chocolate bar");
        transaction.setAmount(10.0);

        Account account = new Account();
        Balance balance = new Balance();
        balance.setCurrency("EUR");
        balance.setAmount(30.0);
        account.getBalances().add(balance);
        when(accountUtils.getAccount(anyString())).thenReturn(account);
        when(transactionDao.createTransaction(transaction)).thenReturn(transactionId);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        transaction = transactionService.createTransaction(transaction);

        assertEquals(20.0, transaction.getBalance(), 0.0);
        assertNotNull(transaction.getDate());
        assertEquals(transactionId, transaction.getId());

        verify(accountDao).updateBalance(balanceCaptor.capture());
        Balance updatedBalance = balanceCaptor.getValue();

        assertEquals(20.0, updatedBalance.getAmount(), 0.0);
    }

    @Test
    public void createTransaction_must_notify_transaction_and_account_queues() throws JsonProcessingException {
        String transactionId = "1234567";
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setCurrency("EUR");
        transaction.setDirection(Transaction.DIRECTION.OUT);
        transaction.setDescription("Chocolate bar");
        transaction.setAmount(10.0);

        Account account = new Account();
        Balance balance = new Balance();
        balance.setCurrency("EUR");
        balance.setAmount(30.0);
        account.getBalances().add(balance);
        when(accountUtils.getAccount(anyString())).thenReturn(account);
        when(transactionDao.createTransaction(transaction)).thenReturn(transactionId);

        transaction = transactionService.createTransaction(transaction);

        verify(rabbitTemplate,atLeastOnce()).convertAndSend(
                TransactionService.EXCHANGE_NAME,
                TransactionService.TRANSACTION_CREATE_KEY,
                new ObjectMapper().writeValueAsString(transaction)
        );

        verify(rabbitTemplate,atLeastOnce()).convertAndSend(
                TransactionService.EXCHANGE_NAME,
                TransactionService.ACCOUNT_UPDATE_KEY,
                new ObjectMapper().writeValueAsString(account)
        );
    }

    private List<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("EUR"));
        currencies.add(new Currency("USD"));
        currencies.add(new Currency("GBP"));
        currencies.add(new Currency("SEK"));
        return currencies;
    }
}
