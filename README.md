# SoulUp API (Java + Spring Boot)

API RESTful do **SoulUp**, plataforma de gamificação sustentável: usuários registram ações sustentáveis, ganham pontos, sobem de nível e aparecem num ranking.
Challenge FIAP 2026 · Sprint 4 · Domain Driven Design Using Java · Equipe **SoulPoints**.

| Integrante | RM |
|---|---|
| Cauã De Souza Vasconcellos | RM573349 |
| Gabriel Tavares | RM571113 |
| Leonardo De Souza Bernard | RM570951 |
| Luigi Tormim Carqueijeiro | RM572424 |

**Deploy:** https://soulup-api-g73n.onrender.com (ex.: [/ranking](https://soulup-api-g73n.onrender.com/ranking))

> ⚠️ O Render grátis "dorme" depois de 15 min sem uso. A **primeira requisição pode levar até 1 minuto**; as próximas são rápidas.

## Tecnologias
- Java 21
- Spring Boot 3 (`spring-boot-starter-web`)
- JDBC puro com `ojdbc11` (sem JPA/Hibernate), sempre com `PreparedStatement`
- Oracle Database (servidor da FIAP)
- Maven · Docker · Render

## Pacotes (`br.com.soulup`)
| Pacote | Conteúdo |
|---|---|
| `model` | `Usuario`, `Acao`, `RegistroAcao`: classes que espelham as tabelas |
| `dao` | `ConnectionFactory`, `UsuarioDAO`, `AcaoDAO`, `RegistroAcaoDAO`: SQL e CRUD |
| `bo` | `UsuarioBO`, `AcaoBO`, `RegistroAcaoBO`: validações e regras de negócio |
| `resource` | `UsuarioResource`, `AcaoResource`, `RegistroResource`, `RankingResource`: endpoints REST |
| `exception` | exceções próprias (404/400/409), `ErroResposta` e `GlobalExceptionHandler` (`@RestControllerAdvice`) |
| `config` | `CorsFilter`: libera o front (localhost:5173 e `FRONT_URL`) |

Fluxo: **Resource → BO → DAO → Oracle**.

## Regras de negócio
- A pontuação **não fica salva**: é a soma dos pontos das ações registradas (`LEFT JOIN` + `SUM`).
- Nível: ≥80 Guardião da Terra · ≥60 Eco Herói · ≥40 Consciência Verde · ≥20 Aprendiz Sustentável · senão Iniciante.
- A mesma ação não pode ser registrada duas vezes pelo mesmo usuário (409).
- Nome do usuário: 3 a 80 letras/espaços, único. E-mail válido, até 120 caracteres, único.
- Ação: nome de 3 a 60 caracteres, único; pontos de 1 a 100; categoria `MOBILIDADE`, `RESIDUOS`, `ENERGIA`, `AGUA`, `CONSUMO`, `NATUREZA` ou `ENGAJAMENTO`.
- Não exclui usuário com registros nem ação já registrada (409).

## Variáveis de ambiente
As credenciais **não ficam no código**. Configure antes de rodar:

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:oracle:thin:@oracle.fiap.com.br:1521/ORCL` |
| `DB_USER` | `rm000000` |
| `DB_PASSWORD` | sua senha do Oracle FIAP |
| `FRONT_URL` | `https://seu-front.vercel.app` (pode ter várias, separadas por vírgula) |
| `PORT` | definida pelo Render; local usa `8080` |

## Banco
Tabelas `SOULUP_USUARIO`, `SOULUP_ACAO` e `SOULUP_REGISTRO_ACAO`. O script de criação (com dados de exemplo) está em [`banco/criar_tabelas.sql`](banco/criar_tabelas.sql).

## Como rodar
**IntelliJ (JDK 21):**
1. `File > Open` e escolha a pasta do projeto (ele reconhece o `pom.xml`).
2. `File > Project Structure > SDK`: escolha o JDK 21.
3. `Run > Edit Configurations > SoulupApiApplication > Environment variables`: coloque `DB_URL`, `DB_USER` e `DB_PASSWORD`.
4. Rode a classe `SoulupApiApplication`. A API sobe em `http://localhost:8080`.
5. Teste com o arquivo [`requests.http`](requests.http).

**Terminal (com Maven):**
```bash
mvn package -DskipTests
java -jar target/soulup-api.jar
```

**Docker:**
```bash
docker build -t soulup-api .
docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... soulup-api
```

## Deploy (Render)
1. New > Web Service > conectar este repositório > Runtime **Docker** > plano Free.
2. Em Environment, criar `DB_URL`, `DB_USER`, `DB_PASSWORD` e `FRONT_URL`.
3. Deploy. A URL pública fica em `https://<nome>.onrender.com`.

## Endpoints
JSON em camelCase, datas no formato `2026-10-29`. Erro sempre no formato `{ "status": 404, "mensagem": "Usuário não encontrado" }`.

| Verbo | URI | Corpo | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/usuarios` | - | 200 | 500 |
| GET | `/usuarios/{id}` | - | 200 | 404 |
| POST | `/usuarios` | `{nome, email}` | 201 | 400, 409 |
| PUT | `/usuarios/{id}` | `{nome, email}` | 200 | 400, 404, 409 |
| DELETE | `/usuarios/{id}` | - | 204 | 404, 409 (tem registros) |
| GET | `/usuarios/{id}/registros` | - | 200 | 404 |
| GET | `/acoes` (`?categoria=MOBILIDADE`) | - | 200 | 400 |
| GET | `/acoes/{id}` | - | 200 | 404 |
| POST | `/acoes` | `{nome, pontos, categoria}` | 201 | 400, 409 |
| PUT | `/acoes/{id}` | `{nome, pontos, categoria}` | 200 | 400, 404, 409 |
| DELETE | `/acoes/{id}` | - | 204 | 404, 409 |
| POST | `/registros` | `{idUsuario, idAcao}` | 201 | 400, 404, 409 (já registrada) |
| DELETE | `/registros/{id}` | - | 204 | 404 |
| GET | `/ranking` | - | 200 | 500 |

**Respostas:**
- Usuário: `{ "id": 1, "nome": "Ana Silva", "email": "ana@email.com", "dataCadastro": "2026-10-29", "totalAcoes": 3, "pontos": 45, "nivel": "Consciência Verde" }`
- Ação: `{ "id": 1, "nome": "Usar transporte publico", "pontos": 15, "categoria": "MOBILIDADE" }`
- Registro: `{ "id": 1, "idUsuario": 1, "idAcao": 1, "nomeAcao": "Usar transporte publico", "pontos": 15, "dataRegistro": "2026-10-29" }`

## Documentação
A documentação completa (MER, diagrama de classes, uso de IA) está em [`docs/documentacao.md`](docs/documentacao.md).
