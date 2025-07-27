# 👤 User Management Endpoints

## 🔐 POST `/api/users/signup`
**Descrição:** Registra um novo usuário no sistema.  
**❗ Não requer autenticação (Bearer Token não é necessário).**

### 📥 Request Body:
Todos os campos são obrigatórios, **exceto** `phone` (opcional):
```json
{
  "username": "lucasdev",
  "password": "SenhaForte123!",
  "name": "Lucas Lias",
  "email": "lucas@example.com",
  "phone": "11999998888",
  "gender": "M"
}
```

### 📥 Response Body:
```json
{
  "success": true,
  "code": "CREATED",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNzUzNjU3MjUyLCJleHAiOjE3NTM3NDM2NTJ9.OK6zw5OFx0gv26KRmhRbqbROU_EVhUc92E52eb_0GWM"
}
```

## 🔐 POST `/api/users/login`
**Descrição:** Descrição: Realiza a autenticação do usuário e retorna o token JWT.  
**❗ Não requer autenticação (Bearer Token não é necessário).**

### 📥 Request Body:
Todos os campos são obrigatórios,:
```json
{
  "username": "lucasdev",
  "password": "SenhaForte123!"
}
```

### 📥 Response Body:
```json
{
  "success": true,
  "code": "OK",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNzUzNjU3NDIwLCJleHAiOjE3NTM3NDM4MjB9.2o0r4ca7xdP8FkJHhvBzRvrN0G3BbkKnjhu6SPrVE6Y"
}
```

## 👤 GET `/api/users/me`
**Descrição:** Descrição: Retorna os dados do usuário atualmente autenticado.  
**✅ Requer autenticação via Bearer Token.**

### 📥 Response Body:
```json
{
  "success": true,
  "code": "OK",
  "data": {
    "name": "Lucas Lias",
    "email": "lucas@example.com",
    "phone": "11999998888",
    "gender": "M",
    "createdAt": "2025-07-27T20:00:52.295376",
    "wallet": [
      {
        "currency": "BRL",
        "balance": 500000
      },
      {
        "currency": "BTC",
        "balance": 1
      }
    ]
  }
```

# 💼 Wallet Endpoints

> ℹ️ **Moedas suportadas:**  
> Os seguintes valores são aceitos no campo `currency`:
>
> - `BTC` – Bitcoin  
> - `BRL` – Real Brasileiro  
> - `ETH` – Ethereum  
> - `USD` – Dólar Americano

## 🔼 POST `/api/wallet/credit`
**Descrição:** Descrição: Adiciona saldo na carteira do usuário autenticado.  
- 🔐 **Requer token Bearer de autenticação**
- 📌 **Todos os campos são obrigatórios**
- ❗ **Não é possível creditar valores negativos ou zerados.**

### 📥 Request Body:
```json
{
  "currency": "BTC",
  "balance": 1
}
```

### 📥 Response Body:
```json
{
  "code": 200,
  "message": "Saldo creditado com sucesso.",
  "data": {
    "name": "Lucas",
    "email": "lucas@example.com",
    "phone": "11999999999",
    "gender": "M",
    "createdAt": "2025-07-27T18:32:45.441352",
    "wallet": [
      {
        "currency": "BRL",
        "balance": 250.00
      },
      {
        "currency": "BTC",
        "balance": 0.1
      }
    ]
  }
}
```

## 🔽 POST `/api/wallet/debit`
**Descrição:** Descrição: Remove saldo da carteira do usuário autenticado.  
- 🔐 **Requer token Bearer de autenticação**
- 📌 **Todos os campos são obrigatórios**
- ❗ **Não é possível debitar mais do que o saldo atual.**

### 📥 Request Body:
```json
{
  "currency": "BTC",
  "balance": 1
}
```

### 📥 Response Body:
```json
{
  "code": 200,
  "message": "Saldo creditado com sucesso.",
  "data": {
    "name": "Lucas",
    "email": "lucas@example.com",
    "phone": "11999999999",
    "gender": "M",
    "createdAt": "2025-07-27T18:32:45.441352",
    "wallet": [
      {
        "currency": "BRL",
        "balance": 250.00
      },
      {
        "currency": "BTC",
        "balance": 0.1
      }
    ]
  }
}
```

# 📦 OrderController

> ℹ️ **Type suportados:**  
> Os seguintes valores são aceitos no campo `type`:
>
> - `BUY`  
> - `SELL`

> ℹ️ **Status suportados:**  
> Os seguintes valores são aceitos no campo `status`:
>
> - `OPEN` 
> - `EXECUTED`
> - `CANCELLED`

## 🔸 `POST /api/orders`
**Descrição:** Descrição: Submete uma nova ordem de compra ou venda ao livro de ordens..  
- 🔐 **Requer token Bearer de autenticação**
- 📌 **Todos os campos são obrigatórios**

### 📥 Request Body Buy:
```json
{
  "type": "BUY",
  "baseCurrency": "BTC",
  "quoteCurrency": "BRL",
  "amount": 1,
  "price": 100000
}
```

### 📥 Request Body Sell:
```json
{
  "type": "SELL",
  "baseCurrency": "BTC",
  "quoteCurrency": "BRL",
  "amount": 1,
  "price": 100000
}
```

### 📥 Response Body:
```json
{
  "success": true,
  "code": "CREATED",
  "data": "Order created successfully"
}
```
OR
```json
{
  "success": true,
  "code": "CREATED",
  "data": "Order created and matched successfully"
}
```

## 🔸 `DELETE /api/orders/{id}`
**Descrição:** Descrição: Cancela uma ordem existente e reembolsa os valores para a carteira do usuário.  
- 🔐 **Requer token Bearer de autenticação**


### 📥 Response Body:
```json
{
  "success": true,
  "code": "OK",
  "data": "Order cancelled successfully and funds refunded."
}
```
## 🔸 `GET /api/orders/user`
**Descrição:** Descrição: Retorna todas as ordens do usuário logado, com filtros opcionais por status e tipo.  
- 🔐 **Requer token Bearer de autenticação**
- 📌 **Query Params (opcional): status e type**

### 📥 Response Body:
```json
{
  "success": true,
  "code": "OK",
  "data": {
    "buyOrders": [
      {
        "id": 21,
        "type": "BUY",
        "baseCurrency": "BTC",
        "quoteCurrency": "BRL",
        "amount": 1,
        "price": 100000,
        "status": "CANCELLED",
        "createdAt": "2025-07-27T20:25:55.982853"
      }
    ],
    "sellOrders": []
  }
}
```

## 🔸 `GET /api/orders`
**Descrição:** Descrição:etorna todas as ordens no sistema, com filtros opcionais.  
- 🔐 **Requer token Bearer de autenticação**
- 📌 **Query Params (opcional): status, type, baseCurrency (BTC, BRL, ETH, USD) e quoteCurrency (BTC, BRL, ETH, USD)**

### 📥 Response Body:
```json
{
  "success": true,
  "code": "OK",
  "data": {
    "buyOrders": [
      {
        "id": 21,
        "type": "BUY",
        "baseCurrency": "BTC",
        "quoteCurrency": "BRL",
        "amount": 1,
        "price": 100000,
        "status": "CANCELLED",
        "createdAt": "2025-07-27T20:25:55.982853"
      }
    ],
    "sellOrders": []
  }
}
```
