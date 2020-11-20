package com.modularbank.account.entity;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Transaction {

    private String id;

    @NotNull
    @NotBlank
    private String accountId;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    @NotBlank
    private String currency;

    @NotNull
    @Valid
    private DIRECTION direction;

    @NotNull
    @NotBlank
    private String description;

    private Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum DIRECTION {
        IN("IN"),
        OUT("OUT");

        String value;

        DIRECTION(final String direction) {
            this.value = direction;
        }

        public String getValue() {
            return value;
        }
    }
}
