package com.modularbank.account.report.repository;

import com.modularbank.account.report.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);
}
