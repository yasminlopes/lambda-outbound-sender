package tech.buildrun.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Handler implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request,
                                                      Context context) {
        var logger = context.getLogger();

        String method = request.getHttpMethod();
        if (method == null) {
            if (request.getBody() != null && !request.getBody().isBlank()) {
                method = "POST";
            } else {
                return jsonResponse(400, Map.of("error", "Missing httpMethod and empty body"));
            }
        }

        if (method.equalsIgnoreCase("OPTIONS")) {
            return optionsResponse();
        }

        if (!method.equalsIgnoreCase("POST")) {
            return jsonResponse(405, Map.of("error", "Method Not Allowed", "expected", "POST"));
        }

        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (request.getBody() == null || request.getBody().isBlank()) {
            return jsonResponse(400, Map.of("error", "Empty request body"));
        }

        try {
            OutboundRequest outbound = mapper.readValue(request.getBody(), OutboundRequest.class);

            if (outbound.getChannel() == null || outbound.getTo() == null) {
                return jsonResponse(400, Map.of("error", "Missing required fields: channel and to"));
            }

            String messageId = UUID.randomUUID().toString();
            Instant sentAt = Instant.now();

            logger.log("Payload: " + mapper.writeValueAsString(outbound));

        Map<String, String> templates = Map.of(
            "welcome_template", "Olá {{name}}, bem-vindo! Seu horário é {{time}}.",
            "alert_template", "ALERTA: {{level}} - {{detail}}",
            "simple", "{{greeting}} {{name}}",
            "reminder", "Caro {{name}}, este é um lembrete para {{event}} em {{date}}."
        );

        String templateName = outbound.getTemplate() == null ? "welcome_template" : outbound.getTemplate();
        String templateText = templates.getOrDefault(templateName, "{{name}}: template_not_found");

    String rendered = renderTemplate(templateText, outbound.getTemplateVariables());

    Integer sla = outbound.getSlaSeconds();
    Instant slaDeadline = null;
    if (sla != null && sla > 0) {
        slaDeadline = sentAt.plusSeconds(sla);
    }

    String status = "SENT";

    OutboundResponse resp = new OutboundResponse(true, messageId, sentAt, "simulated", rendered,
        status, sla, slaDeadline, templateName, outbound.getTemplateVariables());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody(mapper.writeValueAsString(resp))
                    .withIsBase64Encoded(false);

        } catch (Exception e) {
            logger.log("Error parsing or processing request: " + e.getMessage());
            return jsonResponse(400, Map.of("error", "Invalid request body", "message", e.getMessage()));
        }
    }

    private APIGatewayProxyResponseEvent jsonResponse(int status, Map<String, Object> body) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Map<String, String> headers = Map.of(
            "Content-Type", "application/json",
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Methods", "POST,OPTIONS",
            "Access-Control-Allow-Headers", "Content-Type,Authorization"
        );
        return new APIGatewayProxyResponseEvent()
            .withStatusCode(status)
            .withHeaders(headers)
            .withBody(mapper.writeValueAsString(body))
            .withIsBase64Encoded(false);
        } catch (Exception ex) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
            .withHeaders(Map.of("Access-Control-Allow-Origin", "*"))
            .withBody('{'+"\"error\":\"serialization_failure\""+'}')
            .withIsBase64Encoded(false);
        }
    }

    private APIGatewayProxyResponseEvent optionsResponse() {
    return new APIGatewayProxyResponseEvent()
        .withStatusCode(204)
        .withHeaders(Map.of(
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Methods", "POST,OPTIONS",
            "Access-Control-Allow-Headers", "Content-Type,Authorization"
        ))
        .withIsBase64Encoded(false);
    }

    /**
     * Substitui as ocorrências de {{chave}} pelo valor correspondente em variáveis.
     * se variáveis for nulo ou faltar uma chave, o espaço reservado é mantido como está.
     */
    private String renderTemplate(String template, Map<String, String> variables) {
        if (template == null) return "";
        if (variables == null || variables.isEmpty()) return template;

        String rendered = template;
        for (Map.Entry<String, String> e : variables.entrySet()) {
            String placeholder = "{{" + e.getKey() + "}}";
            String value = e.getValue() == null ? "" : e.getValue();
            rendered = rendered.replace(placeholder, value);
        }
        return rendered;
    }

}
