package com.trading.ajay.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@RegisterForReflection
public class TransactionResult {
    public List<ExtractedItem> transactions;
}