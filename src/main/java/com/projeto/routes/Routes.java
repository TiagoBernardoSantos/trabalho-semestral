package com.projeto.routes;

import com.projeto.controller.OrderController;
import com.projeto.controller.OrderItemController;
import com.sun.net.httpserver.HttpServer;

public class Routes {

    public static void register(HttpServer server) {

        server.createContext("/orders", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.matches("^/orders/\\d+/items$")) {
                new OrderItemController().handle(exchange);
                return;
            }

            new OrderController().handle(exchange);
        });

        server.createContext("/items", new OrderItemController());

        server.createContext("/health", exchange -> {
            String response = "{\"status\": \"OK\", \"message\": \"API está funcionando!\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
    }
}
