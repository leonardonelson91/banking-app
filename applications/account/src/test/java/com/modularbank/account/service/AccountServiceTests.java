package com.modularbank.account.service;

import com.modularbank.account.dao.AccountDao;
import com.modularbank.account.exception.InvalidCustomerException;
import com.modularbank.account.utils.AccountUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


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

}
