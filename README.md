# clob-matching-engine

## 📘 Sobre o Projeto

Este projeto é um motor simplificado de **livro de ordens limitado (CLOB)**, desenvolvido como parte de um exercício técnico. Ele simula a lógica básica de uma exchange, permitindo que usuários criem e cancelem ordens de compra e venda de ativos (ex: BTC/BRL), com correspondência automática quando os preços forem compatíveis.

Principais funcionalidades:
- Criação de ordens limitadas (compra/venda)
- Cancelamento de ordens
- Execução de trades com atualização dos saldos das contas envolvidas
- Filtros por status, tipo de ordem e par de moedas

## 🛠️ Tecnologias Utilizadas

O projeto foi desenvolvido utilizando as seguintes tecnologias:

- **Java 21**  
  Linguagem principal utilizada para implementar a lógica do mecanismo de correspondência (matching engine), as regras de negócio e a API RESTful.

- **Spring Boot**  
  Framework utilizado para facilitar a criação da aplicação Java, com suporte a injeção de dependência, JPA, e estrutura modular para os serviços.

- **PostgreSQL**  
  Banco de dados relacional utilizado para armazenar informações de usuários, ordens, carteiras e transações.

- **Spring Data JPA**  
  Facilita o acesso e manipulação dos dados persistidos no banco de dados através de repositórios.

- **Docker**  
  Utilizado para containerizar a aplicação e o banco de dados, permitindo que o sistema seja facilmente executado em qualquer ambiente sem necessidade de configuração manual.

## ▶️ Como Executar o Projeto

O projeto pode ser executado facilmente utilizando **Docker** e **Spring Boot**. Siga os passos abaixo para subir toda a estrutura (banco de dados e aplicação):

### Pré-requisitos

- [Java 21+](https://adoptium.net/pt-BR/temurin/releases)
- [Docker](https://www.docker.com/)
- Git Bash ou terminal compatível

### 1. Subindo o Banco de Dados com Docker Compose

Na raiz do projeto, há uma pasta chamada `docker`. Para subir os containers existe um arquivo com o nome docker-postgres.bat, basta executar o batch e escolher a opção 1

### 2. Executando o Projeto Spring Boot

Você pode executar o projeto diretamente pela sua IDE (como IntelliJ ou Eclipse), ou usando o terminal com o comando mvn spring-boot:run

## 🧪 Testando o Projeto

Após subir o projeto, acesse a documentação Swagger: http://localhost:8080/swagger-ui/index.html

## 📖 Documentação da API

A documentação completa dos endpoints está disponível aqui:  
👉 [API Endpoints](./api-endpoints.md)
