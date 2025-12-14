# 🛒 Shopping Cart REST API

API REST desenvolvida em **Java puro (sem frameworks)** para gerenciamento de pedidos e itens de um carrinho de compras, atendendo aos requisitos do **Trabalho Semestral** e aplicando os **4 pilares da Orientação a Objetos**.

---

## 📋 Informações do Projeto

* **Disciplina**: Linguagem de Programação 2
* **Aluno**: Tiago Bernardo Santos
* **Curso**: Banco de Dados
* **Instituição**: FATEC São José dos Campos – Prof. Jessen Vidal
* **Linguagem**: Java 21 (OpenJDK / Temurin)
* **Banco de Dados**: H2 Database (Embedded / In-Memory)
* **Arquitetura**: REST API sem uso de frameworks (Spring, Quarkus, etc.)
* **Build Tool**: Maven

---

## 🎯 Objetivo do Projeto

Desenvolver uma **REST API em Java puro** que implemente:

* CRUD completo de **duas entidades**
* Relacionamento **1:N (One-To-Many)**
* Persistência em **banco de dados embarcado**
* Aplicação clara dos conceitos de **Abstração, Encapsulamento, Herança e Polimorfismo**
* Código organizado, modular e de fácil manutenção

---

## 🧠 Conceitos de Orientação a Objetos Aplicados

### 1️⃣ Abstração

**Onde:** `Order` e `OrderItem`
**O que:** Representam entidades do mundo real (Pedido e Item do Pedido)

```java
public class Order extends BaseEntity {
    private String customerName;
    private Double totalValue;
    private String status;
}
```

---

### 2️⃣ Encapsulamento

**Onde:** Atributos privados + getters/setters com validação

```java
public void setQuantity(Integer quantity) {
    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("Quantidade deve ser maior que zero");
    }
    this.quantity = quantity;
}
```

---

### 3️⃣ Herança

**Onde:** `Order` e `OrderItem` herdam de `BaseEntity`

```java
public abstract class BaseEntity {
    protected Long id;
    protected LocalDateTime createdAt;
}
```

---

### 4️⃣ Polimorfismo

**Onde:** Interface `OrderService` e implementação `OrderServiceImpl`

```java
public interface OrderService {
    Order createOrder(String customerName) throws SQLException;
}

public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder(String customerName) throws SQLException {
        // implementação concreta
    }
}
```

---

## 🔗 Relacionamento entre Entidades (1:N)

```
Order (Pedido) 1 ─────► N OrderItem (Itens)
```

* Um **pedido** pode possuir **vários itens**
* Cada **item pertence a um único pedido**

```sql
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
```

---

## 🗂️ Estrutura do Projeto

```
trabalho-semestral/
├── pom.xml
├── README.md
└── src/main/java/com/projeto/
    ├── Main.java                  # Inicialização da aplicação
    │
    ├── routes/                    # Configuração centralizada de rotas
    │   └── Routes.java
    │
    ├── controller/                # Camada de controle (HTTP)
    │   ├── OrderController.java
    │   └── OrderItemController.java
    │
    ├── service/                   # Regras de negócio
    │   ├── OrderService.java
    │   └── OrderServiceImpl.java
    │
    ├── repository/                # Acesso ao banco (CRUD)
    │   ├── OrderRepository.java
    │   └── OrderItemRepository.java
    │
    ├── model/                     # Entidades do domínio
    │   ├── BaseEntity.java
    │   ├── Order.java
    │   └── OrderItem.java
    │
    └── database/                  # Conexão e estrutura do banco
        └── DatabaseConnection.java
```

---

## 🗄️ Banco de Dados

* **Tipo:** H2 Database (In-Memory)
* **URL:** `jdbc:h2:mem:shopping_cart;DB_CLOSE_DELAY=-1`
* **Usuário:** `sa`
* **Senha:** *(vazia)*

As tabelas são criadas automaticamente ao iniciar a aplicação.

---

## 🚀 Como Executar o Projeto

### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/TiagoBernardoSantos/trabalho-semestral.git
cd trabalho-semestral
```

---

### 2️⃣ Compilar o projeto

```bash
mvn clean compile
```

---

### 3️⃣ Executar a aplicação

```bash
mvn exec:java
```

---

### 4️⃣ Servidor em execução

Ao iniciar, a aplicação exibirá no terminal:

```
===========================================
✅ Servidor iniciado com sucesso!
📡 Rodando em: http://localhost:8080
===========================================
```

---

## 📡 Endpoints Disponíveis

### 🔎 Health Check

```
GET http://localhost:8080/health
```

---

### 📦 Pedidos (Orders)

| Método | Endpoint     | Descrição              |
| ------ | ------------ | ---------------------- |
| GET    | /orders      | Lista todos os pedidos |
| GET    | /orders/{id} | Busca pedido por ID    |
| POST   | /orders      | Cria um novo pedido    |
| PUT    | /orders/{id} | Atualiza um pedido     |
| DELETE | /orders/{id} | Remove um pedido       |

---

### 🧾 Itens do Pedido (Order Items)

| Método | Endpoint                | Descrição               |
| ------ | ----------------------- | ----------------------- |
| GET    | /orders/{orderId}/items | Lista itens do pedido   |
| POST   | /orders/{orderId}/items | Adiciona item ao pedido |
| DELETE | /items/{id}             | Remove um item          |

---

## 🧪 Testando a API (Sem Postman)

### Opção 1️⃣ Navegador

```
http://localhost:8080/health
```

---

### Opção 2️⃣ Terminal (Windows PowerShell)

```powershell
# Health Check
curl http://localhost:8080/health

# Criar pedido
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"customerName":"Maria Santos"}'

# Listar pedidos
curl http://localhost:8080/orders
```

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia  | Descrição                   |
| ----------- | --------------------------- |
| Java 21     | Linguagem principal         |
| Maven       | Build e dependências        |
| H2 Database | Banco de dados embarcado    |
| Gson        | Serialização JSON           |
| HttpServer  | Servidor HTTP nativo do JDK |

---

## 📌 Observações Importantes

* Projeto **não possui frontend** (conforme solicitado)
* Persistência feita apenas via **API REST**
* Código organizado seguindo boas práticas de separação de responsabilidades
* Atende integralmente os requisitos do **Trabalho Semestral**

---

## 👨‍💻 Autor

**Tiago Bernardo Santos**
FATEC São José dos Campos – Prof. Jessen Vidal

---

📅 **Última atualização:** 13/12/2025
