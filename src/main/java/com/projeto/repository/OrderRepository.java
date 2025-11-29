package com.projeto.repository;

import com.projeto.database.DatabaseConnection;
import com.projeto.model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
    
    public Order save(Order order) throws SQLException {
        String sql = "INSERT INTO orders (customer_name, total_value, status, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, order.getCustomerName());
            stmt.setDouble(2, order.getTotalValue());
            stmt.setString(3, order.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar pedido");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                }
            }
            
            System.out.println("✅ Pedido criado: " + order.getId());
            return order;
        }
    }
    
    public Optional<Order> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                return Optional.of(order);
            }
            
            return Optional.empty();
        }
    }
    
    public List<Order> findAll() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        
        return orders;
    }
    
    public Order update(Order order) throws SQLException {
        String sql = "UPDATE orders SET customer_name = ?, total_value = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getCustomerName());
            stmt.setDouble(2, order.getTotalValue());
            stmt.setString(3, order.getStatus());
            stmt.setLong(4, order.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Pedido não encontrado: " + order.getId());
            }
            
            System.out.println("✅ Pedido atualizado: " + order.getId());
            return order;
        }
    }
    
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Pedido não encontrado: " + id);
            }
            
            System.out.println("❌ Pedido deletado: " + id);
        }
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setTotalValue(rs.getDouble("total_value"));
        order.setStatus(rs.getString("status"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return order;
    }
}
