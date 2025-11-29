package com.projeto.service;

import com.projeto.model.Order;
import com.projeto.model.OrderItem;
import com.projeto.repository.OrderItemRepository;
import com.projeto.repository.OrderRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    
    public OrderServiceImpl() {
        this.orderRepo = new OrderRepository();
        this.itemRepo = new OrderItemRepository();
    }
    
    @Override
    public Order createOrder(String customerName) throws SQLException {
        Order order = new Order(customerName);
        return orderRepo.save(order);
    }
    
    @Override
    public OrderItem addItem(Long orderId, String product, int quantity, double unitPrice) throws SQLException {
        OrderItem item = new OrderItem(orderId, product, quantity, unitPrice);
        OrderItem savedItem = itemRepo.save(item);
        
        updateOrderTotal(orderId);
        
        return savedItem;
    }
    
    @Override
    public List<Order> listOrders() throws SQLException {
        List<Order> orders = orderRepo.findAll();
        
        for (Order order : orders) {
            List<OrderItem> items = itemRepo.findByOrderId(order.getId());
            order.setItems(items);
        }
        
        return orders;
    }
    
    @Override
    public Optional<Order> findOrderById(Long id) throws SQLException {
        Optional<Order> orderOpt = orderRepo.findById(id);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = itemRepo.findByOrderId(id);
            order.setItems(items);
            return Optional.of(order);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<OrderItem> listItems(Long orderId) throws SQLException {
        return itemRepo.findByOrderId(orderId);
    }
    
    @Override
    public Order updateOrderStatus(Long orderId, String newStatus) throws SQLException {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            throw new SQLException("Pedido não encontrado: " + orderId);
        }
        
        Order order = orderOpt.get();
        order.setStatus(newStatus);
        return orderRepo.update(order);
    }
    
    @Override
    public void deleteOrder(Long orderId) throws SQLException {
        itemRepo.deleteByOrderId(orderId);
        orderRepo.deleteById(orderId);
        System.out.println("❌ Pedido " + orderId + " deletado completamente");
    }
    
    @Override
    public void deleteItem(Long itemId) throws SQLException {
        Optional<OrderItem> itemOpt = itemRepo.findById(itemId);
        
        if (itemOpt.isEmpty()) {
            throw new SQLException("Item não encontrado: " + itemId);
        }
        
        OrderItem item = itemOpt.get();
        Long orderId = item.getOrderId();
        
        itemRepo.deleteById(itemId);
        
        updateOrderTotal(orderId);
    }
    
    @Override
    public Order confirmOrder(Long orderId) throws SQLException {
        Optional<Order> orderOpt = findOrderById(orderId);
        
        if (orderOpt.isEmpty()) {
            throw new SQLException("Pedido não encontrado: " + orderId);
        }
        
        Order order = orderOpt.get();
        
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar pedido sem itens");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Apenas pedidos PENDING podem ser confirmados");
        }
        
        order.setStatus("CONFIRMED");
        return orderRepo.update(order);
    }
    
    @Override
    public Order cancelOrder(Long orderId) throws SQLException {
        Optional<Order> orderOpt = findOrderById(orderId);
        
        if (orderOpt.isEmpty()) {
            throw new SQLException("Pedido não encontrado: " + orderId);
        }
        
        Order order = orderOpt.get();
        
        if ("CANCELLED".equals(order.getStatus())) {
            throw new IllegalStateException("Pedido já está cancelado");
        }
        
        order.setStatus("CANCELLED");
        return orderRepo.update(order);
    }
    
    private void updateOrderTotal(Long orderId) throws SQLException {
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = itemRepo.findByOrderId(orderId);
            order.setItems(items);
            order.calculateTotal();
            orderRepo.update(order);
        }
    }
}
