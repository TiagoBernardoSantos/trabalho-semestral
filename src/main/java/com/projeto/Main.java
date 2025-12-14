package com.projeto;

import com.projeto.database.DatabaseConnection;
import com.projeto.routes.Routes;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Iniciando API...");

            DatabaseConnection.getConnection();

            HttpServer server = HttpServer.create(
                    new InetSocketAddress(PORT), 0
            );

            Routes.register(server);

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
            System.out.println("💡 Teste pelo navegador ou terminal (curl)");
            System.out.println("===========================================\n");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🛑 Encerrando servidor...");
                server.stop(0);
                DatabaseConnection.closeConnection();
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
