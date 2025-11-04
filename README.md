<p align="center" width="100%">
    <img width="30%" src="https://github.com/buildrun-tech/buildrun-aws-lambda-java-starter-example/blob/main/images/lambda.png"> 
</p>


<h3 align="center">
  Exemplo de AWS Lambda em Java — Outbound Sender (mock)
</h3>

<p align="center">

  <img alt="License: MIT" src="https://img.shields.io/badge/license-MIT-%2304D361">
  <img alt="Language: Java" src="https://img.shields.io/badge/language-java-green">
  <img alt="Version: 1.0" src="https://img.shields.io/badge/version-1.0-yellowgreen">

</p>

Este repositório contém um exemplo didático de uma AWS Lambda em Java que simula o comportamento de um outbound sender.

Você envia um JSON com um template e variáveis; a função renderiza a mensagem a partir de um registry mock e devolve uma resposta simulada (incluindo messageId, timestamps, SLA e o texto renderizado).

## Endpoint de exemplo

https://ghqa5befokev4yqoxfkbmethk40wndic.lambda-url.sa-east-1.on.aws/

OBS: substitua pela sua Function URL quando estiver testando localmente.

## Resumo do comportamento

- Aceita requisições HTTP POST com `Content-Type: application/json`.
- Responde `OPTIONS` para preflight CORS com cabeçalhos apropriados.
- Em invocações de teste (Console AWS) onde `httpMethod` pode ser nulo, o handler trata o caso como POST se houver body.
- O payload deve conter o `template` e `templateVariables` (mapa), a função renderiza o template substituindo `{{key}}` pelos valores informados.

## Payload 

Campos principais:

- `channel` (string) — ex: `whatsapp`.
- `to` (string) — número em formato internacional: `+5511999999999`.
- `template` (string) — nome do template. Se ausente, `welcome_template` é usado como padrão.
- `templateVariables` (object) — mapa chave->valor usadas no template.
- `data` (string) — timestamp ISO-8601 (opcional).
- `slaSeconds` (integer) — opcional; quando informado, a resposta inclui `slaDeadline`.

Exemplo:

```json
{
  "channel": "whatsapp",
  "to": "+5511999999999",
  "template": "welcome_template",
  "templateVariables": { "name": "Teste Name", "time": "07:30" },
  "data": "2025-11-04T21:30:00Z",
  "slaSeconds": 300
}
```

## Exemplo de chamada (curl)

Comando pronto (use a URL do seu Function):

```bash
curl --request POST \
  --url https://ghqa5befokev4yqoxfkbmethk40wndic.lambda-url.sa-east-1.on.aws/ \
  --header 'content-type: application/json' \
  --data '{
  "channel": "whatsapp",
  "to": "+5511999999999",
  "template": "welcome_template",
  "templateVariables": {
    "name": "Teste Name",
    "time": "07:30"
  },
  "data": "2025-11-04T21:30:00Z",
  "slaSeconds": 300
}'
```

Para ver headers e status, adicione `-i` (ex: `curl -i ...`). Se preferir visualizar o JSON bonito, encadeie `| jq .` (se tiver `jq` instalado).

## Resposta 

Exemplo de payload de resposta (HTTP 200):

```json
{
  "sucesso": true,
  "messageId": "be3a1f42-aa58-46f8-a806-a6bf960d0562",
  "sentAt": 1759884687.249964,
  "mode": "simulated",
  "renderedMessage": "Olá Teste Name, bem-vindo! Seu horário é 07:30.",
  "status": "SENT",
  "slaSeconds": 300,
  "slaDeadline": 1759884987.249964,
  "templateName": "welcome_template",
  "templateVariables": { "name": "Teste Name", "time": "07:30" }
}
```

Notas:
- `renderedMessage` é o template com placeholders substituídos por `templateVariables` (placeholders no formato `{{key}}`).
- Atualmente `sentAt` e `slaDeadline` são serializados como números (epoch seconds com fração). Se preferir ISO-8601, é possível alterar o `ObjectMapper`.

## Templates disponíveis

- `welcome_template`: `Olá {{name}}, bem-vindo! Seu horário é {{time}}.`
- `alert_template`: `ALERTA: {{level}} - {{detail}}`
- `simple`: `{{greeting}} {{name}}`

Se o template não existir, a mensagem retornará um fallback `"{{name}}: template_not_found"`.

## Como compilar / executar

Pré-requisitos:

- JDK 21
- Maven 3.x

Compilar e empacotar:

```bash
mvn -f app/pom.xml -DskipTests package
```

Se você não estiver com JDK 21 localmente e só quiser validar a compilação, rode:

```bash
mvn -f app/pom.xml -DskipTests -Denforcer.skip=true package
```


Desenvolvido por Yasmin Lopes
