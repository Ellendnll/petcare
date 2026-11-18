# Pet Care

Aplicação web (Spring Boot + frontend estático) para tutores acompanharem vacinas e consultas dos seus animais.

## Tecnologias
Java 17+, Spring Boot (Web, Data JPA, Validation), Lombok, BCrypt (spring-security-crypto), HTML/CSS/JS puro.

## Como rodar
1. Configure o banco em `src/main/resources/application.properties` (`ddl-auto=update` cria as tabelas).
2. `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
3. Acesse `http://localhost:8080/login.html`
4. Documentação da API: `http://localhost:8080/swagger-ui.html`

## Histórias de usuário
1. **Cadastro de animais**: nome, espécie, raça, nascimento, peso e foto.
2. **Registro de vacinas**: nome, data de aplicação, próxima dose e observações.
3. **Agendamento de consultas**: clínica, data/hora, motivo e animal.
4. **Histórico de cuidados**: linha do tempo de vacinas e consultas por animal.

## Endpoints
| Recurso | Método e rota |
|---|---|
| Usuários | `POST /api/usuarios`, `POST /api/usuarios/login`, `GET/PUT /api/usuarios/{id}` |
| Animais | `GET /api/pets?usuarioId=`, `GET/PUT/DELETE /api/pets/{id}`, `POST /api/pets` |
| Vacinas | `GET /api/vacinas?petId=`, `POST /api/vacinas`, `GET/PUT/DELETE /api/vacinas/{id}` |
| Consultas | `GET /api/consultas?petId=&data=`, `POST /api/consultas`, `GET/PUT/DELETE /api/consultas/{id}` |
| Histórico | `GET /api/pets/{id}/historico` |

## Regras de negócio
- E-mail de usuário único; senha armazenada com hash BCrypt.
- Consulta deve ser futura e um animal não pode ter duas consultas no mesmo horário.
- Data de aplicação da vacina não pode ser futura; a próxima dose deve ser posterior à aplicação.

## Estrutura
`usuario`, `pet`, `vacina`, `consulta`, `historico` (camadas controller, service, repository e DTOs) e `common/exception` (tratamento global de erros).

## Equipe
Ellen - pacote de consulta e historico
Thai - pacote pet
Rogerio - pacote Usuario
Carol - pacote vacina
