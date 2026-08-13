# Perguntas de Entrevista — Dev Pleno

Perguntas conceituais e técnicas alinhadas à stack da vaga. Use o projeto CRUD de tarefas como exemplo prático nas respostas.

---

## 1. Java 8

### Conceituais
1. O que são **lambdas** e por que foram introduzidas no Java 8?
2. Explique a diferença entre `Interface` com métodos `default` e classes abstratas.
3. O que é a **Stream API**? Cite operações intermediárias e terminais.
4. Diferença entre `Optional`, `null` e como evitar `NullPointerException`.
5. O que mudou em **java.time** em relação a `Date`/`Calendar`?
6. Explique `CompletableFuture` em alto nível.

### Técnicas
7. Como você converteria uma `List<Tarefa>` em `List<TarefaResponse>` com Stream? (como no `TarefaServiceImpl`)
8. Quando usar `map` vs `flatMap`?
9. O que acontece se chamar `get()` em um `Optional` vazio?
10. Diferença entre `Predicate`, `Function`, `Consumer` e `Supplier`.

**Gabarito rápido:** Lambdas = funções anônimas; Stream = pipeline lazy; Optional = ausência explícita de valor; `LocalDateTime` é imutável e thread-safe.

---

## 2. Spring Boot

### Conceituais
1. O que é **Inversão de Controle (IoC)** e **Injeção de Dependência (DI)**?
2. Diferença entre `@Component`, `@Service`, `@Repository` e `@Controller`/`@RestController`.
3. O que faz `@SpringBootApplication`?
4. Ciclo de vida de um bean: quando usar `@PostConstruct` / `@PreDestroy`?
5. Diferença entre `@RequestParam`, `@PathVariable` e `@RequestBody`.
6. O que é **auto-configuration** do Spring Boot?
7. Para que serve `application.yml` / profiles (`dev`, `prod`)?

### Técnicas
8. Por que o controller depende de `TarefaService` (interface) e não de `TarefaServiceImpl`?
9. Explique `@Transactional` e a diferença de `readOnly = true`.
10. Como o `@RestControllerAdvice` trata erros de validação e 404 neste projeto?
11. Diferença entre `@Autowired` no campo vs construtor (qual é preferível e por quê)?
12. O que é um **starter** (`spring-boot-starter-web`, `data-jpa`)?

**Gabarito rápido:** DI via construtor facilita testes e imutabilidade; `@Transactional` delimita unidade de trabalho no banco; Advice centraliza HTTP status e mensagens.

---

## 3. Banco Relacional — Oracle / JPA

### Conceituais
1. O que é um banco **relacional**? Cite chaves primária, estrangeira e normalização.
2. Diferença entre SQL e PL/SQL.
3. O que são **índices** e quando atrapalham?
4. Explique **ACID** (Atomicidade, Consistência, Isolamento, Durabilidade).
5. Diferença entre `INNER JOIN`, `LEFT JOIN` e `EXISTS`.
6. O que é um **SEQUENCE** no Oracle? Por que usamos no `@GeneratedValue`?

### Técnicas (JPA / Hibernate)
7. Diferença entre entidade JPA e DTO. Por que não expor a entidade na API?
8. O que faz `ddl-auto: update`? Riscos em produção?
9. Diferença entre `FetchType.LAZY` e `EAGER`.
10. O que é o problema **N+1** e como mitigar?
11. Diferença entre `save`, `persist`, `merge` (conceitualmente).
12. Como o Spring Data gera a query de `findByStatus(StatusTarefa status)`?

**Gabarito rápido:** Sequence = gerador de IDs no Oracle; DTO desacopla API do modelo de persistência; `ddl-auto=validate/none` em produção.

---

## 4. Testes Unitários

### Conceituais
1. Diferença entre teste **unitário**, **integração** e **E2E**.
2. O que é um **mock**? E um stub? E um spy?
3. Por que testar o service isolado do banco?
4. O que é cobertura de código (JaCoCo)? Cobertura alta garante qualidade?

### Técnicas
5. Explique o uso de `@ExtendWith(MockitoExtension.class)`, `@Mock` e `@InjectMocks` em `TarefaServiceImplTest`.
6. O que testa o `@WebMvcTest` + `MockMvc` no controller?
7. Como você testaria o cenário “tarefa não encontrada” (404)?
8. Diferença entre `when(...).thenReturn` e `verify(...)`.
9. O que é **AAA** (Arrange, Act, Assert)?

**Gabarito rápido:** Unitário = classe isolada; MockMvc simula HTTP sem subir servidor completo; verify confirma interações com dependências.

---

## 5. SOLID

### Conceituais
1. Explique cada letra do SOLID com um exemplo do projeto.
2. O que acontece se o controller acessar o `TarefaRepository` diretamente? Qual princípio quebra?
3. Como o uso de interface `TarefaService` favorece Open/Closed e Dependency Inversion?
4. Diferença entre SRP e “classe pequena demais”.

### Técnicas / discussão
5. Como você evoluirá o sistema para notificar e-mail ao concluir tarefa **sem** modificar o service atual?
6. Se surgirem vários tipos de “tarefa” com comportamentos diferentes, qual princípio ajuda a modelar?

**Gabarito rápido (mapeamento do projeto):**
| Princípio | Onde aparece |
|-----------|--------------|
| S | Controller ≠ Service ≠ Repository |
| O | Nova regra via nova implementação / decorator |
| L | `TarefaServiceImpl` honra o contrato da interface |
| I | Interface só com métodos de tarefa |
| D | Injeção da abstração `TarefaService` |

---

## 6. SonarQube e práticas consolidadas

### Conceituais
1. O que o SonarQube analisa? (bugs, code smells, vulnerabilidades, coverage, duplicação)
2. O que é **Quality Gate**?
3. Diferença entre code smell e bug.
4. Cite boas práticas: complexidade ciclomática, métodos longos, magic numbers, código morto.

### Técnicas
5. Para que serve o plugin **JaCoCo** no `pom.xml`?
6. O que configura o `sonar-project.properties`?
7. Como você reagiria a um alerta de “Cognitive Complexity” no service?
8. Por que evitar `catch (Exception e) { }` vazio?

**Gabarito rápido:** Sonar + CI impede merge se Quality Gate falhar; JaCoCo gera XML/HTML de cobertura consumido pelo Sonar.

---

## 7. Angular / HTML / CSS

### Conceituais
1. O que é o Angular? Diferença para React/Vue (opinião técnica).
2. Explique **Component**, **Template**, **Module**/standalone e **Service**.
3. O que é **data binding**? (`interpolation`, `[]`, `()`, `[(ngModel)]`)
4. O que é o ciclo de vida `ngOnInit`?
5. Para que serve o `HttpClient`? O que é um `Observable` (RxJS)?
6. Diferença entre `subscribe` e `async` pipe.
7. O que é CORS e por que o backend libera `/api/**`?

### Técnicas
8. Como o frontend deste projeto consome a API (`TarefaService`)?
9. Diferença entre environment de desenvolvimento e produção (`apiUrl`).
10. O que o Nginx faz ao fazer proxy de `/api/` para o backend no Docker?
11. Semantic HTML: por que usar `label`, `header`, `section`?
12. Box model CSS: `content`, `padding`, `border`, `margin`. Explique `box-sizing: border-box`.

**Gabarito rápido:** Service Angular centraliza HTTP; `[(ngModel)]` = two-way binding; Nginx unifica origem e evita CORS no browser em produção Docker.

---

## 8. API REST e Swagger

### Conceituais
1. O que é REST? Recursos, verbos HTTP e códigos de status.
2. Quando usar `200`, `201`, `204`, `400`, `404`, `500`?
3. Idempotência: quais métodos são idempotentes?
4. O que é OpenAPI/Swagger e por que documentar a API?

### Técnicas
5. Neste projeto, por que `POST` retorna `201` e `DELETE` retorna `204`?
6. Como o springdoc gera a UI a partir das anotações (`@Operation`, `@Tag`)?
7. O que validam `@NotBlank` e `@Valid` no `TarefaRequest`?

---

## 9. Docker

### Conceituais
1. Diferença entre imagem e container.
2. O que é um `Dockerfile` multi-stage? Benefício neste projeto?
3. O que o `docker-compose.yml` orquestra aqui (Oracle, backend, frontend)?
4. Para que serve `healthcheck` + `depends_on: condition: service_healthy`?
5. Diferença entre volume nomeado e bind mount.

### Técnicas
6. Por que o backend usa variáveis `ORACLE_HOST`, `ORACLE_USER`, etc.?
7. Explique o fluxo: build Maven na imagem → JRE só com o JAR.
8. No frontend, por que build Node e runtime Nginx?

**Gabarito rápido:** Multi-stage reduz tamanho da imagem; healthcheck evita backend subir antes do Oracle aceitar conexões.

---

## 10. Perguntas comportamentais / plenário

1. Como você prioriza débito técnico vs entrega de feature?
2. Descreva um bug difícil que você debugou (método, ferramentas, aprendizado).
3. Como você revisa um Pull Request de um júnior?
4. Já integrou SonarQube no pipeline? Como lidou com Quality Gate quebrando o build?
5. Como você documentaria esta API para outro time consumir?

---

## Roteiro rápido de estudo (1–2 dias)

1. Rodar o projeto no Docker e testar o CRUD + Swagger.  
2. Ler `TarefaServiceImpl` e os testes — explicar SOLID e mocks em voz alta.  
3. Revisar Java 8 (Stream, Optional) e Spring (DI, JPA, `@Transactional`).  
4. Revisar Angular (component + service + HttpClient) e conceitos HTTP/REST.  
5. Responder em voz alta 2–3 perguntas de cada seção acima.

Boa entrevista!
