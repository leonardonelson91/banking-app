package com.modularbank.account.mapper;

import com.modularbank.account.entity.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Select("INSERT INTO transaction (account_id, amount, currency, direction, description, balance)" +
            " VALUES (#{accountId}, #{amount}, #{currency}, #{direction.value}, #{description}, #{balance}) RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    String createTransaction(Transaction transaction);

    @Select("SELECT * from transaction WHERE account_id = #{accountId}")
    @Results(value = {
            @Result(property = "accountId", column = "account_id")
    })
    List<Transaction> getAccountTransactions(String accountId);
}
