# Chamados

Projeto da disciplina de Design e Arquitetura de Sistemas (UNIVILLE), desenvolvido junto com o professor Sestito em aula. A proposta é simples: uma API para abrir e acompanhar chamados de suporte técnico, do jeito que uma empresa pequena precisaria — cliente abre o chamado, categoriza o problema, um técnico assume, conversa com o cliente pelos comentários e fecha quando resolver.

Fui construindo módulo por módulo, sempre na mesma ordem: entidade → repositório → DTO → serviço → controller → testes. Dá pra ver essa evolução nos commits.

## O que já funciona

- **Categorias** do chamado (ex: Rede, Hardware, Software), sem nomes repetidos.
- **Técnicos**, com perfil de ADMIN ou TECNICO e e-mail único.
- **Clientes**, já existia antes desse módulo.
- **Chamados**, o coração do sistema: nascem abertos, têm prioridade e status, e podem ser atribuídos a um técnico.
- **Comentários** dentro de um chamado, pra registrar o histórico de atendimento.

Uma regra que achei importante: uma vez que o chamado está **FECHADO**, ninguém mexe mais nele (nem reatribuir técnico, nem editar, nem mudar status de novo). Se tentar, a API responde 409.

## Rotas

### Categorias
- `POST /categorias`
- `GET /categorias`
- `GET /categorias/{id}`
- `PUT /categorias/{id}`
- `DELETE /categorias/{id}`

### Técnicos
- `POST /tecnicos`
- `GET /tecnicos`
- `GET /tecnicos/{id}`
- `PUT /tecnicos/{id}`
- `DELETE /tecnicos/{id}`

### Clientes
- `POST /clientes`
- `GET /clientes`
- `GET /clientes/{id}`
- `GET /clientes/{id}/chamados` — todos os chamados daquele cliente
- `PUT /clientes/{id}`
- `DELETE /clientes/{id}`

### Chamados
- `POST /chamados`
- `GET /chamados` — com filtros, veja abaixo
- `GET /chamados/{id}`
- `PUT /chamados/{id}`
- `PATCH /chamados/{id}/atribuir` — atribui um técnico
- `PATCH /chamados/{id}/status` — muda só o status
- `DELETE /chamados/{id}`

### Comentários
- `POST /chamados/{chamadoId}/comentarios`
- `GET /chamados/{chamadoId}/comentarios`

## Como filtrar e paginar os chamados

O `GET /chamados` aceita `status`, `prioridade` e `clienteId` como filtros opcionais — dá pra combinar os três, ou usar só um, ou nenhum. Também aceita os parâmetros de paginação padrão do Spring (`page`, `size`, `sort`).

Alguns exemplos de como consultar:

```
GET /chamados
GET /chamados?status=ABERTO
GET /chamados?prioridade=CRITICA
GET /chamados?clienteId=3
GET /chamados?status=EM_ATENDIMENTO&prioridade=ALTA
GET /chamados?clienteId=3&status=ABERTO&page=0&size=10
GET /chamados?sort=prioridade,desc
```

A resposta vem no formato `Page` do Spring Data, então além da lista (`content`) já vêm `totalElements`, `totalPages`, `number`, `size` etc.

## Testando na mão

Com a aplicação rodando, dá pra testar assim pelo terminal:

```
curl -X POST http://localhost:8080/categorias -H "Content-Type: application/json" -d "{\"nome\":\"Rede\",\"descricao\":\"Problemas de rede\"}"
curl http://localhost:8080/chamados?status=ABERTO&page=0&size=5
curl -X PATCH http://localhost:8080/chamados/1/status -H "Content-Type: application/json" -d "{\"status\":\"EM_ATENDIMENTO\"}"
```

(ou, se preferir algo visual, o H2 console em `/h2-console` e o Postman/Insomnia funcionam tranquilo).

## Rodando os testes

```
mvnw test
```
