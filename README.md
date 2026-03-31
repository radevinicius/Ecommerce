Projeto E-Commerce

API RESTful para gestão de e-commerce desenvolvida com Java e Spring Boot. O objetivo deste projeto é demonstrar a aplicação de boas práticas de desenvolvimento backend, arquitetura em camadas, segurança e implementação de regras de negócio de um cenário real.

Tecnologias Utilizadas

- Java 17+
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- PostgreSQL
- Docker
- JWT (JSON Web Token)
- Swagger / OpenAPI
- Hibernate

Arquitetura e Decisões de Negócio

Este projeto foi estruturado para resolver problemas comuns do mercado, focando em integridade e manutenção do código:

- Isolamento de Dados: Uso de DTOs para separar as entidades de banco de dados dos objetos de transferência, evitando vazamento de informações.
- Segurança: Implementação de Spring Security com JWT. O sistema diferencia permissões de Administradores (gestão de produtos) e Usuários comuns (compras e histórico próprio).
- Integridade de Transações: Uso de @Transactional em operações críticas. A criação de um pedido e a baixa no estoque ocorrem na mesma transação de banco de dados para evitar inconsistências.
- Modelagem de E-commerce: Relacionamentos JPA estruturados para isolar o "Pedido" do "Item do Pedido".
- Preservação de Histórico: Implementação de Soft Delete nos produtos para não quebrar o histórico financeiro de pedidos antigos. Os preços dos produtos são "congelados" na tabela de itens no momento exato da compra.

Funcionalidades

Gestão de Usuários
- Cadastro e autenticação com senhas encriptadas (BCrypt).
- Geração e extração de contexto de segurança via tokens JWT.

Catálogo de Produtos
- CRUD completo protegido para acesso exclusivo de administradores.
- Tratamento de valores monetários utilizando BigDecimal para evitar perda de precisão.

Checkout e Pedidos
- Validação sistêmica de quantidade em estoque antes de fechar a compra.
- Cálculo automático de subtotais e valor total do pedido no backend.
- Endpoint isolado para o cliente autenticado consultar seu próprio histórico de compras.

Como Executar

1. Clone o repositório:
   git clone https://github.com/radevinicius/Ecommerce

2. Suba o banco de dados PostgreSQL utilizando o Docker:
   docker-compose up -d

3. Execute a aplicação na raiz do projeto:
   mvn spring-boot:run

Documentação

A API possui documentação interativa gerada pelo Swagger. Com a aplicação em execução localmente, acesse a interface pelo navegador para visualizar os endpoints e testar as requisições:

http://localhost:8080/swagger-ui/index.html