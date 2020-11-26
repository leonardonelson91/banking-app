package com.modularbank.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.exception.CurrencyNotAvailableException;
import com.modularbank.account.exception.InvalidCurrencyException;
import com.modularbank.account.exception.InvalidCustomerException;
import com.modularbank.account.utils.AccountUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests {

    @Mock
    AccountDao accountDao;

    @Mock
    AccountUtils accountUtils;

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    AccountService accountService;

    @Test(expected = InvalidCustomerException.class)
    public void createAccount_must_throw_exception_when_customer_is_not_found() {
        when(accountDao.getCustomer(any())).thenReturn(null);

        accountService.createAccount("123", "Brazil", new String [] {"EUR"});
    }

    @Test(expected = InvalidCurrencyException.class)
    public void createAccount_must_throw_exception_when_currency_is_not_valid() {
        lenient().when(accountDao.getCurrencies()).thenReturn(getCurrencies());
        lenient().when(accountDao.getCustomer(anyString())).thenReturn("1234");
        doCallRealMethod().when(accountUtils).validateCurrency(any());

        accountService.createAccount("123", "Brazil", new String[] {"BRL"});
    }

    @Test
    public void createAccount_must_create_empty_balances() {
        String accountId = "123";
        String customerId = "1234";
        when(accountDao.getCustomer(anyString())).thenReturn("1234");
        when(accountDao.createAccount(any())).thenReturn(accountId);

        Account account = accountService.createAccount(customerId, "Brazil", new String[] {"EUR", "USD"});

        assertTrue(account.getBalances().size() == 2);
        assertEquals("EUR", account.getBalances().get(0).getCurrency());
        assertEquals("USD", account.getBalances().get(1).getCurrency());
        assertEquals(0.0, account.getBalances().get(0).getAmount(), 0.0);
        assertEquals(0.0, account.getBalances().get(1).getAmount(), 0.0);
        assertEquals(accountId, account.getBalances().get(0).getAccountId());
        assertEquals(accountId, account.getBalances().get(1).getAccountId());
        verify(accountDao, atLeast(2)).createBalance(any());
    }

    @Test
    public void createAccount_must_return_correct_id_country_and_customerid() {
        String accountId = "123";
        String customerId = "1234";
        String country = "Brazil";
        when(accountDao.getCustomer(anyString())).thenReturn(customerId);
        when(accountDao.createAccount(any())).thenReturn(accountId);

        Account account = accountService.createAccount(customerId, country, new String[] {"EUR", "USD"});

        assertEquals(accountId, account.getId());
        assertEquals(customerId, account.getCustomerId());
        assertEquals(country, account.getCountry());
    }

    @Test
    public void createAccount_must_notify_queue() throws JsonProcessingException {
        String accountId = "123";
        String customerId = "1234";
        String country = "Brazil";
        when(accountDao.getCustomer(anyString())).thenReturn(customerId);
        when(accountDao.createAccount(any())).thenReturn(accountId);
        lenient().doNothing().when(rabbitTemplate).convertAndSend(any());

        Account account = accountService.createAccount(customerId, country, new String[] {"EUR", "USD"});

        verify(rabbitTemplate, atLeastOnce()).convertAndSend(
                AccountService.EXCHANGE_NAME,
                AccountService.ACCOUNT_CREATE_KEY,
                new ObjectMapper().writeValueAsString((account)));
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
