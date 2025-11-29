package com.projeto.service;

import com.projeto.model.Order;
import com.projeto.model.OrderItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    
    Order createOrder(String customerName) throws SQLException;
    
    OrderItem addItem(Long orderId, String product, int quantity, double unitPrice) throws SQLException;
    
    List<Order> listOrders() throws SQLException;
    
    Optional<Order> findOrderById(Long id) throws SQLException;
    
    List<OrderItem> listItems(Long orderId) throws SQLException;
    
    Order updateOrderStatus(Long orderId, String newStatus) throws SQLException;
    
    void deleteOrder(Long orderId) throws SQLException;
    
    void deleteItem(Long itemId) throws SQLException;
    
    Order confirmOrder(Long orderId) throws SQLException;
    
    Order cancelOrder(Long orderId) throws SQLException;
}
