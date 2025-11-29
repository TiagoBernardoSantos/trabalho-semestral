package com.projeto.controller;

import com.google.gson.*;
import com.projeto.model.Order;
import com.projeto.service.OrderService;
import com.projeto.service.OrderServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


public class OrderController implements HttpHandler {
    
    private final OrderService orderService;
    private final Gson gson;
    
    public OrderController() {
        this.orderService = new OrderServiceImpl();
        
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws SQLException, IOException {
        String[] parts = path.split("/");
        
        if (parts.length == 2) {
            List<Order> orders = orderService.listOrders();
            String json = gson.toJson(orders);
            sendResponse(exchange, 200, json);
            
        } else if (parts.length == 3) {
            try {
                Long id = Long.parseLong(parts[2]);
                Optional<Order> order = orderService.findOrderById(id);
                
                if (order.isPresent()) {
                    String json = gson.toJson(order.get());
                    sendResponse(exchange, 200, json);
                } else {
                    sendResponse(exchange, 404, "{\"error\": \"Pedido não encontrado\"}");
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
            }
        }
    }
    
private void handlePost(HttpExchange exchange) throws IOException, SQLException {

    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    System.out.println("📥 JSON RECEBIDO: " + body);

    if (body == null || body.isBlank()) {
        sendResponse(exchange, 400, "{\"error\": \"Body JSON está vazio\"}");
        return;
    }

    JsonObject jsonObject;

    try {
        jsonObject = JsonParser.parseString(body).getAsJsonObject();
    } catch (Exception e) {
        sendResponse(exchange, 400, "{\"error\": \"Formato JSON inválido\"}");
        return;
    }

    if (!jsonObject.has("customerName")) {
        sendResponse(exchange, 400, "{\"error\": \"Campo obrigatório 'customerName' não enviado\"}");
        return;
    }

    String customerName = jsonObject.get("customerName").getAsString();

    Order savedOrder = orderService.createOrder(customerName);
    String json = gson.toJson(savedOrder);

    sendResponse(exchange, 201, json);
}
    
    private void handlePut(HttpExchange exchange, String path) throws SQLException, IOException {
        String[] parts = path.split("/");
        
        if (parts.length != 3) {
            sendResponse(exchange, 400, "{\"error\": \"ID não fornecido\"}");
            return;
        }
        
        try {
            Long id = Long.parseLong(parts[2]);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String status = jsonObject.get("status").getAsString();
            
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            String json = gson.toJson(updatedOrder);
            sendResponse(exchange, 200, json);
            
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
        }
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws SQLException, IOException {
        String[] parts = path.split("/");
        
        if (parts.length != 3) {
            sendResponse(exchange, 400, "{\"error\": \"ID não fornecido\"}");
            return;
        }
        
        try {
            Long id = Long.parseLong(parts[2]);
            orderService.deleteOrder(id);
            sendResponse(exchange, 200, "{\"message\": \"Pedido deletado com sucesso\"}");
            
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
        }
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}
