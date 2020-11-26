package com.modularbank.account.report.controller;

import com.modularbank.account.report.entity.Account;
import com.modularbank.account.report.entity.Transaction;
import com.modularbank.account.report.exception.AccountNotFoundException;
import com.modularbank.account.report.repository.AccountRepository;
import com.modularbank.account.report.repository.TransactionRepository;
import com.modularbank.account.report.utils.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/reports/accounts")
public class ReportController {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public ReportController(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping(value = {"", "/"})
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accountRepository.findAll().forEach(accounts::add);
        return accounts;
    }

    @GetMapping(value = {"/{id}"})
    public Account getAccount(@PathVariable @NotEmpty @NotNull @NotBlank String id) {
        Optional<Account> maybeAccount = accountRepository.findById(id);
        if(maybeAccount.isEmpty()) {
            throw new AccountNotFoundException(Messages.ACCOUNT_NOT_FOUND);
        }
        return maybeAccount.get();
    }

    @GetMapping(value= {"/transactions", "//transactions", "/{accountId}/transactions"})
    public List<Transaction> getAccountTransactions(@PathVariable @NotEmpty @NotNull @NotBlank String accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        Collections.sort(transactions,
                Comparator.comparing(Transaction::getDate).reversed());
        return transactions;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public String invalidAccountHandler(HttpServletRequest req, Exception ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public String invalidPathVariableHandler(HttpServletRequest req, MissingPathVariableException ex) {
        return Messages.ACCOUNT_ID_MISSING;
    }
}
