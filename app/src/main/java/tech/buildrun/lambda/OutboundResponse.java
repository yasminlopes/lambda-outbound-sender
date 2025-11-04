package tech.buildrun.lambda;

import java.time.Instant;

public class OutboundResponse {

    private boolean sucesso;
    private String messageId;
    private Instant sentAt;
    private String mode;
    private String renderedMessage;
    private String status; //  SENT, QUEUED, FAILED
    private Integer slaSeconds;
    private Instant slaDeadline;
    private String templateName;
    private java.util.Map<String, String> templateVariables;

    public OutboundResponse() {
    }

    public OutboundResponse(boolean sucesso, String messageId, Instant sentAt, String mode) {
        this.sucesso = sucesso;
        this.messageId = messageId;
        this.sentAt = sentAt;
        this.mode = mode;
    }

    public OutboundResponse(boolean sucesso, String messageId, Instant sentAt, String mode, String renderedMessage) {
        this.sucesso = sucesso;
        this.messageId = messageId;
        this.sentAt = sentAt;
        this.mode = mode;
        this.renderedMessage = renderedMessage;
    }

    public OutboundResponse(boolean sucesso, String messageId, Instant sentAt, String mode, String renderedMessage,
                            String status, Integer slaSeconds, Instant slaDeadline, String templateName,
                            java.util.Map<String, String> templateVariables) {
        this.sucesso = sucesso;
        this.messageId = messageId;
        this.sentAt = sentAt;
        this.mode = mode;
        this.renderedMessage = renderedMessage;
        this.status = status;
        this.slaSeconds = slaSeconds;
        this.slaDeadline = slaDeadline;
        this.templateName = templateName;
        this.templateVariables = templateVariables;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRenderedMessage() {
        return renderedMessage;
    }

    public void setRenderedMessage(String renderedMessage) {
        this.renderedMessage = renderedMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSlaSeconds() {
        return slaSeconds;
    }

    public void setSlaSeconds(Integer slaSeconds) {
        this.slaSeconds = slaSeconds;
    }

    public Instant getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(Instant slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public java.util.Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(java.util.Map<String, String> templateVariables) {
        this.templateVariables = templateVariables;
    }
}
