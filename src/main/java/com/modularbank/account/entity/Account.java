package com.modularbank.account.entity;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String id;
    private String customerId;
    private List<Balance> balances;
    private String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Balance> getBalances() {
        if(this.balances == null) {
            this.balances = new ArrayList<>();
        }
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
