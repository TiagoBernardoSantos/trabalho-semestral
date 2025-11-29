package com.projeto.controller;

import com.google.gson.*;
import com.projeto.model.OrderItem;
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

public class OrderItemController implements HttpHandler {

    private final OrderService orderService;
    private final Gson gson;

    public OrderItemController() {
        this.orderService = new OrderServiceImpl();

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
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
                    handlePost(exchange, path);
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

        if (parts.length == 4 && "orders".equals(parts[1]) && "items".equals(parts[3])) {
            try {
                Long orderId = Long.parseLong(parts[2]);
                List<OrderItem> items = orderService.listItems(orderId);
                String json = gson.toJson(items);
                sendResponse(exchange, 200, json);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"ID do pedido inválido\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

private void handlePost(HttpExchange exchange, String path) throws IOException {
    String[] parts = path.split("/");

    if (parts.length == 4 && "orders".equals(parts[1]) && "items".equals(parts[3])) {
        try {
            Long orderId = Long.parseLong(parts[2]);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            System.out.println("📥 JSON recebido: " + body);

            if (body.isBlank()) {
                sendResponse(exchange, 400, "{\"error\": \"Body JSON está vazio\"}");
                return;
            }

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            // Validação dos campos obrigatórios
            if (!jsonObject.has("product") || !jsonObject.has("quantity") || !jsonObject.has("unitPrice")) {
                sendResponse(exchange, 400, "{\"error\": \"Campos obrigatórios: product, quantity, unitPrice\"}");
                return;
            }

            String product = jsonObject.get("product").getAsString();
            int quantity = jsonObject.get("quantity").getAsInt();
            double unitPrice = jsonObject.get("unitPrice").getAsDouble();

            OrderItem savedItem = orderService.addItem(orderId, product, quantity, unitPrice);

            String json = gson.toJson(savedItem);
            sendResponse(exchange, 201, json);

        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"ID do pedido inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Erro interno ao criar item\"}");
        }
    } else {
        sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
    }
}


    private void handleDelete(HttpExchange exchange, String path) throws SQLException, IOException {
        String[] parts = path.split("/");

        if (parts.length == 3 && "items".equals(parts[1])) {
            try {
                Long id = Long.parseLong(parts[2]);
                orderService.deleteItem(id);
                sendResponse(exchange, 200, "{\"message\": \"Item deletado com sucesso\"}");

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
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
