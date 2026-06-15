package com.trading.ajay.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_stock")
public class InventoryItem extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Double currentStock;

    public String unit;

    public String category;

    public InventoryItem() {}

    public InventoryItem(String name, Double currentStock, String unit, String category) {
        this.name = name;
        this.currentStock = currentStock;
        this.unit = unit;
        this.category = category;
    }
}
