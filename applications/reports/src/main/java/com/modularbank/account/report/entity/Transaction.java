package com.modularbank.account.report.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

@RedisHash("Transaction")
public class Transaction implements Serializable {
    @Id
    private String id;

    @Indexed
    private String accountId;
    private Double amount;
    private String currency;
    private DIRECTION direction;
    private String description;
    private Double balance;

    @Indexed
    private Date date;

    public Transaction(String id, String accountId, Double amount, String currency, DIRECTION direction,
                       String description, Double balance, Date date) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.direction = direction;
        this.description = description;
        this.balance = balance;
        this.date = date;
    }

    public Transaction() {
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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
