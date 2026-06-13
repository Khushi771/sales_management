package com.trading.ajay.repository;

import com.trading.ajay.entity.InventoryItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InventoryRepository implements PanacheRepository<InventoryItem> {
    
    public InventoryItem findByNameIgnoreCase(String name) {
        if (name == null) return null;
        return find("lower(trim(name))", name.trim().toLowerCase()).firstResult();
    }
}