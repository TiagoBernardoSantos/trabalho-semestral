package com.projeto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:mem:shopping_cart;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexão com H2 estabelecida!");
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver H2 não encontrado", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    customer_name VARCHAR(100) NOT NULL,
                    total_value DOUBLE NOT NULL DEFAULT 0.0,
                    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_id BIGINT NOT NULL,
                    product VARCHAR(255) NOT NULL,
                    quantity INT NOT NULL,
                    unit_price DOUBLE NOT NULL DEFAULT 0.0,
                    created_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
                )
            """);

            System.out.println("✅ Tabelas criadas/verificadas!");
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("❌ Conexão fechada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
