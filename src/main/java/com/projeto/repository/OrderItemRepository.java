package com.projeto.repository;

import com.projeto.database.DatabaseConnection;
import com.projeto.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderItemRepository {
    
    public OrderItem save(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product, quantity, unit_price, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, item.getOrderId());
            stmt.setString(2, item.getProduct());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getUnitPrice());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar item");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
            
            System.out.println("✅ Item criado: " + item.getId());
            return item;
        }
    }
    
    public Optional<OrderItem> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                OrderItem item = mapResultSetToOrderItem(rs);
                return Optional.of(item);
            }
            
            return Optional.empty();
        }
    }
    
    public List<OrderItem> findByOrderId(Long orderId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ? ORDER BY created_at";
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
        }
        
        return items;
    }
    
    public List<OrderItem> findAll() throws SQLException {
        String sql = "SELECT * FROM order_items ORDER BY created_at DESC";
        List<OrderItem> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(mapResultSetToOrderItem(rs));
            }
        }
        
        return items;
    }
    
    public OrderItem update(OrderItem item) throws SQLException {
        String sql = "UPDATE order_items SET product = ?, quantity = ?, unit_price = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getProduct());
            stmt.setInt(2, item.getQuantity());
            stmt.setDouble(3, item.getUnitPrice());
            stmt.setLong(4, item.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Item não encontrado: " + item.getId());
            }
            
            System.out.println("✅ Item atualizado: " + item.getId());
            return item;
        }
    }
    
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM order_items WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Item não encontrado: " + id);
            }
            
            System.out.println("❌ Item deletado: " + id);
        }
    }
    
    public void deleteByOrderId(Long orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderId);
            stmt.executeUpdate();
            System.out.println("❌ Todos os itens do pedido " + orderId + " foram deletados");
        }
    }
    
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProduct(rs.getString("product"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return item;
    }
}
