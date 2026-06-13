package com.trading.ajay.service;

import com.trading.ajay.model.TransactionResult;
import io.quarkiverse.langchain4j.RegisterAiService;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.data.image.Image;

@RegisterAiService
public interface TransactionParserAgent {

    @UserMessage("""
        Analyze the provided image of an inventory log with high precision.
        
        For each line item:
        1. Extract the name of the item.
        2. Extract the exact numeric quantity into the 'quantity' field (as a double).
        3. Extract the unit written next to the number (e.g., 'boxes', 'packets') into the 'unit' field. If no unit is specified, leave it blank.
        4. Classify the row as a PURCHASE or a SALE.
    """)
    TransactionResult parseInventoryNote(Image image);
}