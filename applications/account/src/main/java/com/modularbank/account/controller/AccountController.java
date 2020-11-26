package com.modularbank.account.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.modularbank.account.entity.Account;
import com.modularbank.account.entity.Currency;
import com.modularbank.account.entity.Transaction;
import com.modularbank.account.exception.*;
import com.modularbank.account.request.AccountRequest;
import com.modularbank.account.service.AccountService;
import com.modularbank.account.service.TransactionService;
import com.modularbank.account.utils.AccountUtils;
import com.modularbank.account.utils.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
public class AccountController {

    private AccountService accountService;
    private AccountUtils accountUtils;
    private TransactionService transactionService;

    public AccountController(AccountService accountService, AccountUtils accountUtils, TransactionService transactionService) {
        this.accountService = accountService;
        this.accountUtils = accountUtils;
        this.transactionService = transactionService;
    }

    @GetMapping(value = {"", "/"})
    public List<Account> getAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/currencies")
    public List<Currency> getCurrencies() {
        return accountUtils.getAllowedCurrencies();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable @NotEmpty @NotNull @NotBlank String id) {
        return accountService.getAccount(id);
    }

    @PostMapping
    public Account createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        return accountService.createAccount(accountRequest.getCustomerId(), accountRequest.getCountry(), accountRequest.getCurrencies());
    }

    @PostMapping("/{accountId}/transaction")
    public Transaction createTransaction(@PathVariable @NotEmpty @NotNull @NotBlank String accountId, @Valid @RequestBody Transaction transaction) {
        transaction.setAccountId(accountId);
        return transactionService.createTransaction(transaction);
    }

    @GetMapping(value= {"/transactions", "//transactions", "/{accountId}/transactions"})
    public List<Transaction> getAccountTransactions(@PathVariable @NotEmpty @NotNull @NotBlank String accountId) {
        return transactionService.getAccountTransactions(accountId);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidCustomerException.class, InvalidCurrencyException.class, CurrencyNotAvailableException.class})
    public String invalidAccountCreationRequestHandler(HttpServletRequest req, Exception ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public String invalidAccountHandler(HttpServletRequest req, Exception ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException.class)
    public String invalidParametersFormatHandler(HttpServletRequest req, InvalidFormatException ex) {
        return String.format(Messages.INVALID_FIELD, ex.getPath().get(0).getFieldName());
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(InsufficientFundsException.class)
    public String insufficientFundsHandler(HttpServletRequest req, InsufficientFundsException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String invalidParametersHandler(HttpServletRequest req, MethodArgumentNotValidException ex) {
        return String.format(Messages.INVALID_FIELD, ex.getFieldError().getField());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public String invalidPathVariableHandler(HttpServletRequest req, MissingPathVariableException ex) {
        return Messages.ACCOUNT_ID_MISSING;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String emptyPathVariableHandler(HttpServletRequest req, ConstraintViolationException ex) {
        return Messages.ACCOUNT_ID_MISSING;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MismatchedInputException.class)
    public String invalidPathVariableHandler(HttpServletRequest req, MismatchedInputException ex) {
        return String.format(Messages.INVALID_FIELD, ex.getPath().get(0).getFieldName());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceUnavailableException.class)
    public String serviceUnavailableHandler(HttpServletRequest req, ServiceUnavailableException ex) {
        return ex.getMessage();
    }
}
