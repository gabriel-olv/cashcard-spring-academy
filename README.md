# 💳 Cash Cards - Projeto Spring Academy

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

Este é um projeto desenvolvido como parte do curso da [Spring Academy](https://spring.academy), com o objetivo de explorar os conceitos de desenvolvimento de APIs RESTful usando **Spring Boot** com autenticação, persistência e testes automatizados.

---

## 📌 Objetivos do Projeto

- Criar uma API para gerenciamento de cartões (Cash Cards)
- Utilizar Spring Boot com Spring Web, Spring Security e Spring Data JDBC
- Implementar autenticação HTTP Basic
- Utilizar banco em memória H2
- Realizar testes unitários e de integração com JUnit

---

## 🛠️ Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- Gradle
- JUnit 5

---

## ▶️ Como Executar Localmente

### Pré-requisitos

- Java 17 ou superior
- Gradle 7+ (ou use o wrapper `./gradlew`)

### Passos

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/cashcards.git
cd cashcards

# Execute o projeto com o Gradle Wrapper
./gradlew bootRun
```


### Autenticação
A API utiliza autenticação HTTP Basic.

Usuário padrão, com perfil CARD-OWNER:

- Usuário: ```sarah1```
- Senha: ```abc123```

Usuário com perfil NON-OWNER:
- Usuário: ```hank-owns-no-cards```
- Senha: ```qrs456```

### Exemplos de Requisições
#### Criar um cartão do usuário autenticado:
```http
POST /cashcards HTTP/1.1
Authorization: Basic c2FyYWgxOmFiYzEyMw==
Content-Type: application/json
{
  "amount": 250.00
}
```
#### Buscar todos cartões do usuário autenticado:
```http
GET /cashcards HTTP/1.1
Authorization: Basic c2FyYWgxOmFiYzEyMw==
```
#### Buscar cartão de ID = 1 do usuário autenticado:
```http
GET http://localhost:8080/cashcards/1
Authorization: Basic c2FyYWgxOmFiYzEyMw==
```
#### Atualizar cartão de ID = 1 do usuário autenticado:
```http
PUT http://localhost:8080/cashcards/1
Authorization: Basic c2FyYWgxOmFiYzEyMw==
Content-Type: application/json
{
  "amount": 300.00
}
```
Desenvolvido por [Gabriel de Oliveira](https://github.com/gabriel-olv), com base no conteúdo educacional da equipe da Spring.
