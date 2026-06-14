package com.trading.ajay.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ExtractedItem {
    public String itemName;
    public Double quantity;
    public String unit; 
    public String category;
    public TransactionType type;
}
