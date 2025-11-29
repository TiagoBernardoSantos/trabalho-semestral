package com.projeto;

import com.projeto.controller.OrderController;
import com.projeto.controller.OrderItemController;
import com.projeto.database.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            System.out.println("===========================================");
            System.out.println("🚀 INICIALIZANDO SERVIDOR REST API");
            System.out.println("===========================================");
            DatabaseConnection.testConnection();

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // ======== ROUTING INTELIGENTE ========
            server.createContext("/orders", exchange -> {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();

                System.out.println("➡ [ROUTER] Method: " + method + " | Path: " + path);

                // Se for rota de itens: /orders/{id}/items
                if (path.matches("^/orders/\\d+/items$")) {
                    new OrderItemController().handle(exchange);
                    return;
                }

                // Caso contrário, pertence ao OrderController
                new OrderController().handle(exchange);
            });

            // Deletar item isolado
            server.createContext("/items", new OrderItemController());
            // ======================================


            // Health Check
            server.createContext("/health", exchange -> {
                String response = "{\"status\": \"OK\", \"message\": \"API está funcionando!\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            });

            server.setExecutor(null);
            server.start();

            System.out.println("===========================================");
            System.out.println("✅ Servidor iniciado com sucesso!");
            System.out.println("📡 Rodando em: http://localhost:" + PORT);
            System.out.println("===========================================");
            System.out.println("\n📚 ENDPOINTS DISPONÍVEIS:\n");
            System.out.println("Health Check:");
            System.out.println("  GET    http://localhost:8080/health");
            System.out.println("\nPedidos (Orders):");
            System.out.println("  GET    http://localhost:8080/orders");
            System.out.println("  GET    http://localhost:8080/orders/{id}");
            System.out.println("  POST   http://localhost:8080/orders");
            System.out.println("  PUT    http://localhost:8080/orders/{id}");
            System.out.println("  DELETE http://localhost:8080/orders/{id}");
            System.out.println("\nItens (OrderItems):");
            System.out.println("  GET    http://localhost:8080/orders/{orderId}/items");
            System.out.println("  POST   http://localhost:8080/orders/{orderId}/items");
            System.out.println("  DELETE http://localhost:8080/items/{id}");
            System.out.println("\n===========================================");
            System.out.println("💡 Use Postman ou Insomnia para testar!");
            System.out.println("💡 Ou acesse: http://localhost:8080/health");
            System.out.println("===========================================\n");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Encerrando servidor...");
                server.stop(0);
                DatabaseConnection.closeConnection();
                System.out.println("✅ Servidor encerrado!");
            }));

        } catch (IOException e) {
            System.err.println("❌ Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
