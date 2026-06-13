package com.trading.ajay.entity;

import com.trading.ajay.model.TransactionType;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "transaction_entry")
public class TransactionEntry extends PanacheEntity {

    public String item;
    public Double quantity;
    public String unit;
    public TransactionType transactionType;
    public LocalDate entryDate;

    public TransactionEntry() {}

    public TransactionEntry(String item, Double quantity, String unit, TransactionType transactionType) {
        this.item = item;
        this.quantity = quantity;
        this.unit = unit;
        this.transactionType = transactionType;
        this.entryDate = LocalDate.now();
    }
}