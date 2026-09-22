<!-- Formato: um bloco por tarefa. O loop pega o PRIMEIRO com [ ].
     [ ] pendente · [x] feita · [!] travada, precisa de você.
     Uma tarefa = uma branch = um PR. Se não cabe em ~5 arquivos, quebre em duas. -->

## [ ] BE-001 — Endpoint de listagem de agendamentos com paginação

**Onde:** `controller/AgendamentoController`, `service/AgendamentoService`,
`repository/AgendamentoRepository`

**O que:** `GET /agendamentos?page=&size=&status=` devolvendo `Page<AgendamentoDTO>`.
`size` máximo 50 (acima disso, 400). Só retorna agendamentos da clínica do
usuário autenticado.

**Critério de aceite:**
- [ ] teste unitário do service: filtro por status aplicado ao repository
- [ ] teste `@SpringBootTest` com Testcontainers: usuário da clínica A não vê agendamento da clínica B
- [ ] teste: `size=100` responde 400
- [ ] nenhuma migração nova

---

## [ ] BE-002 — <título curto e verbal>

**Onde:** <classes>

**O que:** <contrato observável: rota, entrada, saída, códigos de erro>

**Critério de aceite:**
- [ ] <asserção testável>
- [ ] <asserção testável>
