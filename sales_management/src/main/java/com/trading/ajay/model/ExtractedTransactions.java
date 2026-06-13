package com.trading.ajay.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response wrapper for extracted transactions from OCR text
 */
public class ExtractedTransactions {
    /**
     * List of extracted items with their transaction types
     */
    @JsonProperty("transactions")
    public List<ExtractedItem> transactions;

    // No-argument constructor required by LangChain4j
    public ExtractedTransactions() {
    }

    // Constructor with parameter
    public ExtractedTransactions(List<ExtractedItem> transactions) {
        this.transactions = transactions;
    }

    // Getter and setter
    public List<ExtractedItem> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ExtractedItem> transactions) {
        this.transactions = transactions;
    }
}
