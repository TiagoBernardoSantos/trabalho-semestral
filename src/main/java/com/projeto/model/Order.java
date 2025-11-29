package com.projeto.model;

import java.util.ArrayList;
import java.util.List;

public class Order extends BaseEntity {
    
    private String customerName;
    private Double totalValue;
    private String status;
    private List<OrderItem> items;
    
    public Order() {
        super();
        this.items = new ArrayList<>();
        this.totalValue = 0.0;
        this.status = "PENDING";
    }
    
    public Order(String customerName) {
        this();
        this.customerName = customerName;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Double getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotal();
    }
    
    public void removeItem(OrderItem item) {
        this.items.remove(item);
        calculateTotal();
    }
    
    public void calculateTotal() {
        this.totalValue = items.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", totalValue=" + totalValue +
                ", status='" + status + '\'' +
                ", itemsCount=" + items.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}
