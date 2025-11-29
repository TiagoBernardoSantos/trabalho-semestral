# 🛒 Shopping Cart REST API

API REST desenvolvida em **Java puro** para gerenciamento de carrinho de compras, implementando os 4 pilares da Orientação a Objetos.

---

## 📋 Informações do Projeto

- **Disciplina**: Linguagem de Programação 2
- **Aluno**: Tiago Bernardo Santos
- **Linguagem**: Java 21 (OpenJDK)
- **Banco de Dados**: H2 Database (Embedded)
- **Arquitetura**: REST API sem frameworks

---

## 🎯 Conceitos de OOP Implementados

### 1️⃣ **Abstração**
**Onde**: Classes `Order` e `OrderItem`  
**O que**: Representam entidades do mundo real (pedido e item)  
**Arquivo**: `src/main/java/com/projeto/model/Order.java`

```java
public class Order extends BaseEntity {
    private String customerName;
    private Double totalValue;
    private List<OrderItem> items;
    // Representa um pedido real do mundo
}
```

---

### 2️⃣ **Encapsulamento**
**Onde**: Atributos privados com validações  
**O que**: Dados protegidos com getters/setters e validações  
**Arquivo**: `src/main/java/com/projeto/model/OrderItem.java`

```java
public void setQuantity(Integer quantity) {
    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("Quantidade deve ser maior que zero");
    }
    this.quantity = quantity; // Validação protege dados
}
```

---

### 3️⃣ **Herança**
**Onde**: `Order` e `OrderItem` herdam de `BaseEntity`  
**O que**: Classes filhas herdam `id` e `createdAt`  
**Arquivo**: `src/main/java/com/projeto/model/BaseEntity.java`

```java
public abstract class BaseEntity {
    protected Long id;
    protected LocalDateTime createdAt;
}

public class Order extends BaseEntity { /* herda id e createdAt */ }
public class OrderItem extends BaseEntity { /* herda id e createdAt */ }
```

---

### 4️⃣ **Polimorfismo**
**Onde**: Interface `OrderService` e implementação `OrderServiceImpl`  
**O que**: Mesmo método, diferentes implementações possíveis  
**Arquivo**: `src/main/java/com/projeto/service/`

```java
public interface OrderService {
    Order createOrder(String customerName) throws SQLException;
}

public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder(String customerName) throws SQLException {
        // Implementação específica
    }
}
```

---

## 📊 Relacionamento das Entidades (1:N)

```
┌─────────────────────┐           ┌──────────────────────┐
│   Order (Pedido)    │ 1 ─────► N│  OrderItem (Item)    │
├─────────────────────┤           ├──────────────────────┤
│ id (PK)             │           │ id (PK)              │
│ customerName        │           │ orderId (FK) ◄───────┤
│ totalValue          │           │ product              │
│ status              │           │ quantity             │
│ createdAt           │           │ unitPrice            │
│ items: List         │           │ createdAt            │
└─────────────────────┘           └──────────────────────┘
```

**SQL:**
```sql
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
```

**Um pedido pode ter vários itens**  
**Cada item pertence a apenas um pedido**

---

## 🗂️ Estrutura do Projeto

```
shopping-cart-api/
├── pom.xml                                    # Configuração Maven
├── README.md                                  # Este arquivo
└── src/main/java/com/projeto/
    ├── Main.java                              # ⭐ Inicialização do servidor
    │
    ├── model/                                 # 🎯 ABSTRAÇÃO + HERANÇA + ENCAPSULAMENTO
    │   ├── BaseEntity.java                    # Classe PAI
    │   ├── Order.java                         # Entidade Pedido
    │   └── OrderItem.java                     # Entidade Item
    │
    ├── repository/                            # 💾 CRUD - Acesso ao Banco
    │   ├── OrderRepository.java               # CRUD de Orders
    │   └── OrderItemRepository.java           # CRUD de Items
    │
    ├── service/                               # 🧠 LÓGICA DE NEGÓCIO + POLIMORFISMO
    │   ├── OrderService.java                  # Interface (contrato)
    │   └── OrderServiceImpl.java              # Implementação
    │
    ├── controller/                            # 🌐 REST API ENDPOINTS
    │   ├── OrderController.java               # Endpoints de Orders
    │   └── OrderItemController.java           # Endpoints de Items
    │
    └── database/                              # 🗄️ CONFIGURAÇÃO H2
        └── DatabaseConnection.java            # Conexão e criação de tabelas
```

---

## 🔧 Pré-requisitos

Certifique-se de ter instalado:

- **Java JDK 17+** (OpenJDK recomendado)
  - Verificar: `java -version`
- **Maven 3.8+**
  - Verificar: `mvn -version`
- **Git** (para clonar o repositório)
- **Postman ou Insomnia** (opcional, para testar endpoints)

---

## 🚀 Como Compilar e Executar

### 1️⃣ Clonar o Repositório

```bash
git clone 
cd shopping-cart-api
```

### 2️⃣ Compilar o Projeto

```bash
mvn clean compile
```

**Saída esperada:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: X s
```

### 3️⃣ Executar o Servidor

**Opção A - Via Maven:**
```bash
mvn compile exec:java
```

**Opção B - Gerando JAR executável:**
```bash
mvn clean package
java -jar target/shopping-cart-api-1.0-SNAPSHOT.jar
```

### 4️⃣ Verificar se Está Funcionando

**No navegador, acesse:**
```
http://localhost:8080/health
```

**Resposta esperada:**
```json
{
  "status": "OK",
  "message": "API está funcionando!"
}
```

---

## 📡 Endpoints da API

### **Health Check**

```http
GET http://localhost:8080/health
```

---

### **Orders (Pedidos)**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/orders` | Listar todos os pedidos |
| `GET` | `/orders/{id}` | Buscar pedido por ID |
| `POST` | `/orders` | Criar novo pedido |
| `PUT` | `/orders/{id}` | Atualizar pedido |
| `DELETE` | `/orders/{id}` | Deletar pedido |

---

### **Items (Itens do Pedido)**

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/orders/{orderId}/items` | Listar itens de um pedido |
| `POST` | `/orders/{orderId}/items` | Adicionar item ao pedido |
| `DELETE` | `/items/{id}` | Deletar item |

---

## 📝 Exemplos de Uso

### ✅ 1. Criar um Pedido

**Request:**
```http
POST http://localhost:8080/orders
Content-Type: application/json

{
  "customerName": "João Silva"
}
```

**Response:**
```json
{
  "id": 1,
  "customerName": "João Silva",
  "totalValue": 0.0,
  "status": "PENDING",
  "items": [],
  "createdAt": "2025-11-28T23:30:00"
}
```

---

### ✅ 2. Adicionar Item ao Pedido

**Request:**
```http
POST http://localhost:8080/orders/1/items
Content-Type: application/json

{
  "product": "Notebook Dell",
  "quantity": 1,
  "unitPrice": 3500.00
}
```

**Response:**
```json
{
  "id": 1,
  "orderId": 1,
  "product": "Notebook Dell",
  "quantity": 1,
  "unitPrice": 3500.0,
  "createdAt": "2025-11-28T23:31:00"
}
```

---

### ✅ 3. Listar Todos os Pedidos

**Request:**
```http
GET http://localhost:8080/orders
```

**Response:**
```json
[
  {
    "id": 1,
    "customerName": "João Silva",
    "totalValue": 3500.0,
    "status": "PENDING",
    "items": [
      {
        "id": 1,
        "product": "Notebook Dell",
        "quantity": 1,
        "unitPrice": 3500.0
      }
    ],
    "createdAt": "2025-11-28T23:30:00"
  }
]
```

---

### ✅ 4. Buscar Pedido por ID

**Request:**
```http
GET http://localhost:8080/orders/1
```

---

### ✅ 5. Atualizar Status do Pedido

**Request:**
```http
PUT http://localhost:8080/orders/1
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

---

### ✅ 6. Deletar Pedido

**Request:**
```http
DELETE http://localhost:8080/orders/1
```

**Response:**
```json
{
  "message": "Pedido deletado com sucesso"
}
```

---

## 🗄️ Banco de Dados

### **Tipo**: H2 Database (in-memory)
### **URL**: `jdbc:h2:mem:shopping_cart`
### **Usuário**: `sa`
### **Senha**: *(vazia)*

### **Tabelas Criadas Automaticamente:**

#### `orders`
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    total_value DOUBLE NOT NULL DEFAULT 0.0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL
);
```

#### `order_items`
```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

---

## 🧪 Testando a API

### **Opção 1: Navegador**
Acesse: `http://localhost:8080/health`

### **Opção 2: PowerShell (Windows)**

```powershell
# Health Check
curl http://localhost:8080/health

# Criar pedido
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{\"customerName\": \"Maria Santos\"}'

# Listar pedidos
curl http://localhost:8080/orders
```

### **Opção 3: Postman (Recomendado)**

1. Baixe: https://www.postman.com/downloads/
2. Importe a collection ou crie requests manualmente
3. Configure Base URL: `http://localhost:8080`
4. Teste cada endpoint

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java (OpenJDK) | 21 | Linguagem principal |
| Maven | 3.9+ | Gerenciamento de dependências |
| H2 Database | 2.2.224 | Banco de dados embedded |
| Gson | 2.10.1 | Serialização JSON |
| HttpServer | JDK nativo | Servidor HTTP (sem frameworks) |

---

## 👨‍💻 Autor

**Nome**: Tiago Bernardo Santos  
**Curso**: Banco de Dados
**Instituição**: FATEC São José dos Campos - Prof. Jessen Vidal

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais como parte do Trabalho Semestral da disciplina de Linguagem de Programação 2.

---

**Última atualização**: 28/11/2025