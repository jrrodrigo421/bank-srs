# CRUD Lista de Tarefas

Projeto simples para preparação de entrevista **Dev Pleno**.


| Camada    | Stack                                                                       |
| --------- | --------------------------------------------------------------------------- |
| Backend   | Java 8, Spring Boot 2.7, Oracle, JPA, Swagger (springdoc), testes unitários |
| Frontend  | Angular 17, HTML, CSS                                                       |
| Qualidade | SOLID, JaCoCo, SonarQube (`sonar-project.properties`)                       |
| Infra     | Docker + Docker Compose                                                     |


---

## Estrutura

```
crudJava/
├── backend/                 # API Spring Boot
│   ├── src/main/java/...
│   ├── src/test/java/...    # Testes unitários (Service + Controller)
│   ├── Dockerfile
│   ├── pom.xml
│   └── sonar-project.properties
├── frontend/                # SPA Angular
│   ├── src/app/
│   ├── Dockerfile
│   └── nginx.conf           # Proxy /api → backend
├── docker-compose.yml
├── README.md
└── PERGUNTAS_ENTREVISTA.md
```

### Arquitetura do backend (SOLID)


| Camada                       | Responsabilidade                      |
| ---------------------------- | ------------------------------------- |
| `controller`                 | HTTP / contratos da API               |
| `service` (interface + impl) | Regras de negócio                     |
| `repository`                 | Acesso a dados (JPA)                  |
| `domain`                     | Entidade e enum                       |
| `dto`                        | Request/Response (não expõe entidade) |
| `exception`                  | Tratamento centralizado de erros      |


Princípios aplicados:

- **S** — cada classe com uma responsabilidade
- **O** — extensão via interface `TarefaService`
- **L** — implementação substitui a interface sem quebrar o contrato
- **I** — interface de serviço focada no domínio de tarefas
- **D** — controller depende de `TarefaService`, não da implementação

---

## API REST

Base: `http://localhost:8080/api/tarefas`


| Método | Endpoint            | Descrição                         |
| ------ | ------------------- | --------------------------------- |
| POST   | `/api/tarefas`      | Criar                             |
| GET    | `/api/tarefas`      | Listar (query `?status=PENDENTE`) |
| GET    | `/api/tarefas/{id}` | Buscar por ID                     |
| PUT    | `/api/tarefas/{id}` | Atualizar                         |
| DELETE | `/api/tarefas/{id}` | Excluir                           |


Status possíveis: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`

### Exemplo de body

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar CRUD e SOLID",
  "status": "PENDENTE"
}
```

### Swagger

- UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- OpenAPI: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Com Docker (via Nginx do frontend): [http://localhost:4200/swagger-ui.html](http://localhost:4200/swagger-ui.html)

---

## Subir com Docker (recomendado)

Pré-requisito: Docker Desktop.

```bash
docker compose up --build
```

Aguarde o Oracle ficar healthy (pode levar 1–2 minutos na primeira vez).


| Serviço           | URL                                                                            |
| ----------------- | ------------------------------------------------------------------------------ |
| Frontend          | [http://localhost:4200](http://localhost:4200)                                 |
| Backend / Swagger | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Oracle            | `localhost:1521` / service `XEPDB1`                                            |


Usuário Oracle da aplicação: `tarefas` / `tarefas`

Parar:

```bash
docker compose down
```

---

## Rodar local (sem Docker da app)

### Backend

1. Java 8+ e Maven instalados
2. Oracle acessível (ou use o container só do banco):

```bash
docker compose up oracle -d
```

1. Na pasta `backend`:

```bash
mvn spring-boot:run
```

Testes + cobertura JaCoCo:

```bash
mvn test
```

Relatório: `backend/target/site/jacoco/index.html`

SonarQube (com servidor Sonar local):

```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000
```

### Frontend

```bash
cd frontend
pnpm install
pnpm start
```

App em [http://localhost:4200](http://localhost:4200) (API em `http://localhost:8080`).

---

## Endpoints rápidos (curl)

```bash
# Criar
curl -X POST http://localhost:8080/api/tarefas \
  -H "Content-Type: application/json" \
  -d "{\"titulo\":\"Estudar Angular\",\"descricao\":\"Componentes\",\"status\":\"PENDENTE\"}"

# Listar
curl http://localhost:8080/api/tarefas

# Atualizar
curl -X PUT http://localhost:8080/api/tarefas/1 \
  -H "Content-Type: application/json" \
  -d "{\"titulo\":\"Estudar Angular\",\"descricao\":\"OK\",\"status\":\"CONCLUIDA\"}"

# Excluir
curl -X DELETE http://localhost:8080/api/tarefas/1
```

---

## Preparação para entrevista

Veja o arquivo **[PERGUNTAS_ENTREVISTA.md](./PERGUNTAS_ENTREVISTA.md)** com perguntas conceituais e técnicas sobre a stack da vaga.