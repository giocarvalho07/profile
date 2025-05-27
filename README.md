# Profile API

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)
![Java](https://img.shields.io/badge/Java-17-blue)
![Maven](https://img.shields.io/badge/Maven-3.9.9-red)
![JWT](https://img.shields.io/badge/JWT-Authentication-orange)
![Swagger](https://img.shields.io/badge/Swagger%20UI-Enabled-brightgreen)
![H2 Database](https://img.shields.io/badge/H2%20Database-Embedded-lightgrey)

Uma API RESTful para gerenciamento de perfis de usuário, incluindo operações CRUD (Create, Read, Update, Delete) para contas e perfis, autenticação via JSON Web Tokens (JWT) e integração com Spring Security. Utiliza o banco de dados em memória H2 para desenvolvimento e facilita a interação com a API através do Swagger UI.

## 🚀 Tecnologias Utilizadas

* **Spring Boot:** Framework para construção de aplicações Spring.
* **Spring Security:** Framework robusto para segurança e autenticação/autorização.
* **JSON Web Token (JWT):** Padrão para criação de tokens de acesso seguro.
* **H2 Database:** Banco de dados relacional em memória (ideal para desenvolvimento e testes).
* **Spring Data JPA:** Abstração para persistência de dados no banco de dados.
* **Lombok:** Ferramenta para reduzir código boilerplate em Java.
* **SpringDoc OpenAPI (Swagger UI):** Geração automática de documentação da API e interface interativa para testes.
* **Maven:** Ferramenta de automação de build e gerenciamento de dependências.
* **Java 17:** Linguagem de programação.

## ✨ Funcionalidades

* **Autenticação de Usuário:** Login via email para obter um token JWT.
* **Gerenciamento de Contas:**
    * Criação de novas contas (`/api/accounts`).
    * Listagem de todas as contas (requer autenticação JWT).
    * Listagem de conta por ID (requer autenticação JWT).
    * Atualização de conta por ID (requer autenticação JWT).
    * Deleção de conta por ID (requer autenticação JWT).
* **Gerenciamento de Perfis (Ainda a ser implementado/detalhado, presumido):**
    * Associar perfil a uma conta.
    * Operações CRUD para perfis.
* **Controle de Acesso:** Proteção de endpoints com Spring Security e JWT.
* **CORS (Cross-Origin Resource Sharing):** Configurado para permitir requisições de diferentes origens (ex: frontend em `localhost:3000`).
* **Documentação Interativa:** Swagger UI para explorar e testar os endpoints da API.

## ⚙️ Configuração e Execução (Ambiente de Desenvolvimento)

### Pré-requisitos

* Java Development Kit (JDK) 17 ou superior
* Apache Maven 3.6+
* IDE (IntelliJ IDEA, VS Code, Eclipse)

### Passos para Rodar a Aplicação

1.  **Clone o Repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd profile
    ```

2.  **Configuração do `application.properties`:**
    O arquivo `src/main/resources/application.properties` contém as configurações da aplicação.

    * **Banco de Dados H2:**
        ```properties
        spring.datasource.url=jdbc:h2:mem:profiledb
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.h2.console.enabled=true
        spring.h2.console.path=/h2-console
        spring.jpa.hibernate.ddl-auto=update # Ou create-drop para reiniciar o DB a cada vez
        ```
    * **Configuração JWT (para desenvolvimento):**
        A chave secreta JWT é **gerada dinamicamente a cada inicialização da aplicação** para fins de desenvolvimento.
        Você verá a chave impressa no console ao iniciar.
        ```properties
        jwt.expiration=3600000 # 1 hora em milissegundos (ou o valor desejado)
        ```
    * **Configuração do Swagger UI:**
        ```properties
        springdoc.swagger-ui.path=/swagger-ui.html
        springdoc.api-docs.path=/v3/api-docs
        springdoc.info.title=Profile API
        springdoc.info.description=API para gerenciamento de perfis de usuário, incluindo CRUD e autenticação JWT.
        springdoc.info.version=1.0.0
        springdoc.swagger-ui.enabled=true
        springdoc.api-docs.enabled=true
        ```

3.  **Compilar e Instalar Dependências:**
    No diretório raiz do projeto (`profile`), execute:
    ```bash
    mvn clean install
    ```
    Isso baixará todas as dependências e compilará o projeto.

4.  **Executar a Aplicação:**
    Após o `mvn clean install` ser bem-sucedido, você pode iniciar a aplicação Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
    Ou, através da sua IDE, execute a classe principal `ProfileApplication`.

    A aplicação estará disponível em `http://localhost:8080`.

## 🧪 Testando a API

### 1. Documentação Interativa (Swagger UI)

Acesse a interface do Swagger UI para explorar os endpoints, visualizar os modelos de dados e fazer requisições diretamente do navegador:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 2. Console do H2 Database

Para visualizar e gerenciar o banco de dados em memória, acesse o console H2:
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)
Use as credenciais configuradas no `application.properties` (`username=sa`, `password=`).

### 3. Fluxo de Teste via Postman/Insomnia

Siga este fluxo para testar os endpoints protegidos:

1.  **Criar uma Conta (Público):**
    * **Método:** `POST`
    * **URL:** `http://localhost:8080/api/accounts`
    * **Body (raw, JSON):**
        ```json
        {
            "name": "Nome do Usuário",
            "age": 30,
            "email": "usuario.teste@example.com"
        }
        ```
    * **Resposta:** `201 Created`

2.  **Autenticar/Logar (Público):**
    * **Método:** `POST`
    * **URL:** `http://localhost:8080/api/auth/login-by-email`
    * **Body (raw, JSON):**
        ```json
        {
            "email": "usuario.teste@example.com"
        }
        ```
    * **Resposta:** `200 OK` com um corpo JSON contendo o `accessToken`. **Copie este `accessToken`!** (Lembre-se que ele é válido apenas para esta sessão da aplicação devido à chave dinâmica).

3.  **Acessar Endpoint Protegido (Requer JWT):**
    * **Método:** `GET`
    * **URL:** `http://localhost:8080/api/accounts`
    * **Headers:**
        * `Authorization`: `Bearer SEU_TOKEN_AQUI` (Substitua `SEU_TOKEN_AQUI` pelo token copiado no passo anterior).
    * **Resposta:** `200 OK` com a lista de contas.

## 🤝 Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

## 📄 Licença

Este projeto está licenciado sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.

---
