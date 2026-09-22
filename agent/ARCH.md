<!-- REGRA DE OURO: este arquivo entra no contexto a CADA iteração.
     Se passar de ~80 linhas, você está pagando por ele o tempo todo. Corte. -->

## Stack

Java 17 + Spring Boot 3 (Maven). Postgres do Supabase via JPA/Hibernate.
Firebase Admin SDK para verificar o token do cliente. Deploy no Render.

## Camadas (respeite a direção)

`controller` → `service` → `repository` → `entity`

- `controller` só valida entrada e mapeia DTO. Zero regra de negócio.
- `service` concentra a regra. É aqui que ficam os testes unitários.
- `repository` são interfaces Spring Data. Query complexa vira `@Query` nomeada.
- Entidade nunca sai do controller. Sempre DTO na fronteira.

## Convenções

- Erro vira exceção de domínio + `@RestControllerAdvice`, nunca `ResponseEntity`
  de erro montado no meio do service.
- Nada de `Optional` como parâmetro. Nada de campo `public`.
- Migração: arquivo novo em `src/main/resources/db/migration/V<n>__<nome>.sql`.

## Comandos

```bash
./gate.sh                       # o portão: compila + testes + verify
./mvnw -q test -Dtest=FooTest   # teste isolado, durante a iteração
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Ambiente local

Perfil `local` aponta para o Postgres do Supabase local (`supabase start`,
porta 54322) e para o emulador do Firebase Auth. Testes de integração sobem
Postgres próprio via Testcontainers — não dependem de nada rodando na máquina
além do Docker. **Nunca** aponte para o banco de produção.

## Definição de pronto

`./gate.sh` verde + teste cobrindo o comportamento novo + nenhum arquivo fora
do escopo da tarefa modificado.
