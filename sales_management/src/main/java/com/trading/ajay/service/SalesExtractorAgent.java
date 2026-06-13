package com.trading.ajay.service;

import com.trading.ajay.model.ExtractedTransactions;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface SalesExtractorAgent {

    @SystemMessage("""
            You are an expert data entry assistant for a retail shop.
            Your job is to analyze raw text extracted via OCR from handwritten notes of daily sales and purchases.
            Extract each item name, its quantity, and whether it was a SALE or a PURCHASE.

            Rules:
            1. If a section is headed by 'purchase', 'bought', or 'inward', classify subsequent items as PURCHASE until stated otherwise.
            2. If a section is headed by 'sale', 'sold', or 'outward', classify subsequent items as SALE.
            3. Clean up abbreviations (e.g., 'Apls' becomes 'Apples', 'Tom' becomes 'Tomato') if the intent is obvious.
            4. Do not make up items. Only extract what is present in the text.
            """)
    @UserMessage("""
            Extract the transactions from this raw OCR text:
            ---
            {rawText}
            ---
            Return the result as a JSON object with a "transactions" array containing the extracted items.
            """)
    ExtractedTransactions extractTransactions(String rawText);
}