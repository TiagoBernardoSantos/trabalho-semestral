package com.projeto.model;

public class OrderItem extends BaseEntity {
    
    private Long orderId;
    private String product;
    private Integer quantity;
    private Double unitPrice;
    
    public OrderItem() {
        super();
    }
    
    public OrderItem(Long orderId, String product, Integer quantity, Double unitPrice) {
        super();
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        if (product == null || product.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        this.product = product;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantity = quantity;
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        if (unitPrice == null || unitPrice < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.unitPrice = unitPrice;
    }
    
    public Double getSubtotal() {
        return unitPrice * quantity;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + getSubtotal() +
                ", createdAt=" + createdAt +
                '}';
    }
}
