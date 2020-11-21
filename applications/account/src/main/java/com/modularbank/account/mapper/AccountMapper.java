package com.modularbank.account.mapper;

import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Balance;
import com.modularbank.account.entity.Currency;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccountMapper {

    @Select("SELECT * FROM account")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "balances",
                    javaType = List.class,
                    column = "id",
                    many = @Many(select = "getAccountBalances"))
    })
    List<Account> getAllAccounts();

    @Select("SELECT * FROM currency")
    List<Currency> getCurrencies();

    @Select("SELECT id FROM customer WHERE id = #{customerId}")
    String getCustomer(String customerId);

    @Select("INSERT INTO account (customer_id, country) VALUES (#{customerId}, #{country}) RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    String createAccount(Account account);

    @Insert("INSERT INTO account_balance (account_id, currency, amount) VALUES (#{accountId}, #{currency}, #{amount})")
    Integer createBalance(Balance balance);

    @Select("SELECT * from account_balance WHERE account_id = #{accountId}")
    @Results(value = {
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "currency", column = "currency"),
            @Result(property = "amount", column = "amount")
    })
    List<Balance> getAccountBalances(String accountId);

    @Select("SELECT * from account WHERE id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "balances",
                    javaType = List.class,
                    column = "id",
                    many = @Many(select = "getAccountBalances"))
    })
    Account getAccount(String id);

    @Select("UPDATE account_balance SET amount = #{amount} WHERE account_id = #{accountId} RETURNING amount")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    Double updateBalance(Balance balance);
}
