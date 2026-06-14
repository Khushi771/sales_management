package com.trading.ajay.service;

import java.util.ArrayList;
import java.util.List;

import com.trading.ajay.entity.TransactionEntry;
import com.trading.ajay.model.ExtractedItem;
import com.trading.ajay.model.ExtractedTransactions;
import com.trading.ajay.model.TransactionType;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ParsingService {

    @Inject
    SalesExtractorAgent salesExtractorAgent;

    public List<TransactionEntry> parseRawText(String rawText) {
        List<TransactionEntry> entries = new ArrayList<>();

        if (rawText == null || rawText.isBlank()) {
            return entries;
        }

        try {
            ExtractedTransactions response = salesExtractorAgent.extractTransactions(rawText);
            List<ExtractedItem> extractedItems = response.transactions;

            if (extractedItems != null) {
                for (ExtractedItem extracted : extractedItems) {
                    if (extracted.itemName != null && extracted.quantity != null) {
                        TransactionEntry entry = new TransactionEntry(
                                extracted.itemName,
                                extracted.quantity,
                                extracted.unit, // Passing the extracted unit here
                                extracted.category,
                                extracted.type != null ? extracted.type : TransactionType.SALE);
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("AI extraction agent failed to parse text layout", e);
        }

        return entries;
    }
}
