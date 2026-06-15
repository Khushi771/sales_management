package com.trading.ajay.resource;

import com.trading.ajay.entity.TransactionEntry;
import com.trading.ajay.entity.InventoryItem;
import com.trading.ajay.model.*;
import com.trading.ajay.service.TransactionParserAgent;
import dev.langchain4j.data.image.Image;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Path("/api/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class SalesResource {

    @Transactional
    public void updateInventory(TransactionEntry entry) {
        InventoryItem item = InventoryItem.find("name = ?1 and category = ?2", 
                                                entry.item, 
                                                entry.category).firstResult();
        if (item == null) {
            item = new InventoryItem();
            item.name = entry.item;
            item.category = entry.category;
            item.unit = entry.unit;
            item.currentStock = 0.0;
            item.persist();
        }

        // 2. Adjust the stock based on transaction type
        if (TransactionType.PURCHASE.equals(entry.transactionType)) {
            item.currentStock += entry.quantity;
        } else if (TransactionType.SALE.equals(entry.transactionType)) {
            item.currentStock -= entry.quantity;
        }
        item.persist();
    }

    @Inject
    TransactionParserAgent parserAgent;

    @GET
    public List<TransactionEntry> getAllTransactions() {
        return TransactionEntry.listAll(Sort.by("entryDate").descending());
    }

    @POST
    @Path("/manual")
    @Transactional
    public void manualEntry(TransactionEntry entry) {
        Log.infof("Item: %s, Qty: %s, Unit: %s, Type: %s, Category: %s", 
            entry.item, entry.quantity, entry.unit, entry.transactionType, entry.category);
        entry.persist();
        updateInventory(entry);
    }

    // --- NEW ENDPOINT TO FETCH LIVE INVENTORY ---
    @GET
    @Path("/inventory")
    public List<InventoryItem> getLiveInventory() {
        return InventoryItem.listAll();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response processDocumentUpload(@RestForm("file") FileUpload file) {
        if (file == null || file.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing file").build();
        }

        try {
            byte[] fileBytes = Files.readAllBytes(file.uploadedFile());
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);
            
            String mimeType = file.contentType();
            String fileName = file.fileName() != null ? file.fileName().toLowerCase() : "";
            if (mimeType == null || mimeType.equals("application/octet-stream")) {
                if (fileName.endsWith(".png")) mimeType = "image/png";
                else if (fileName.endsWith(".webp")) mimeType = "image/webp";
                else mimeType = "image/jpeg";
            }

            Image image = Image.builder().base64Data(base64Image).mimeType(mimeType).build();
            TransactionResult result = parserAgent.parseInventoryNote(image);
            List<TransactionEntry> savedTransactions = new ArrayList<>();
            
            if (result != null && result.transactions != null) {
                for (ExtractedItem item : result.transactions) {
                    if (item.itemName != null && item.quantity != null) {
                        TransactionType type = item.type != null ? item.type : TransactionType.SALE;
                        
                        // 1. SAVE THE AUDIT TRAIL (LEDGER)
                        // Inside the for loop in processDocumentUpload:
                        TransactionEntry entity = new TransactionEntry(
                                item.itemName,
                                item.quantity,
                                item.unit,
                                type,
                                item.category != null ? item.category : "General" // Use "General" if null
                        );
                        entity.persist();
                        savedTransactions.add(entity);

                        // 2. UPDATE THE LIVE INVENTORY STOCK
                        InventoryItem stockItem = InventoryItem.find("name = ?1 and category = ?2", 
                                                entity.item, 
                                                entity.category).firstResult();
        
                        if (stockItem == null) {
                            // First time seeing this item: Create new stock record
                            Double initialStock = (type == TransactionType.PURCHASE) ? item.quantity : -item.quantity;
                            InventoryItem newItem = new InventoryItem(item.itemName, initialStock, item.unit, item.category);
                            newItem.persist();
                        } else {
                            // Item exists: Do the Add/Subtract math
                            if (type == TransactionType.PURCHASE) {
                                stockItem.currentStock += item.quantity;
                            } else {
                                stockItem.currentStock -= item.quantity;
                            }
                        }
                    }
                }
            }
            return Response.ok(savedTransactions).build();

        } catch (IOException e) {
            Log.error("Image Processing Failure", e);
            return Response.serverError().entity("Failed to read image").build();
        }
    }
}
