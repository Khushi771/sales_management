package com.trading.ajay.service;

import com.trading.ajay.model.TransactionResult;
import io.quarkiverse.langchain4j.RegisterAiService;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.data.image.Image;

@RegisterAiService
public interface TransactionParserAgent {

    @UserMessage("""
        Analyze the inventory log.
        1. Identify the 'category' (e.g., Granite, Tiles). If no category header is found, use 'General'.
        2. Extract the 'itemName' (e.g., 'Slab Black').
        3. Extract 'quantity' and 'unit'.
        4. Return the classification as PURCHASE or SALE.
        
        Structure the response to group items under their respective categories.
    """)
    TransactionResult parseInventoryNote(Image image);
}
