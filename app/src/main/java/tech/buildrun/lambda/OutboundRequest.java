package tech.buildrun.lambda;

import java.time.Instant;
import java.util.Map;

public class OutboundRequest {

    private String channel;
    private String to;
    private String template;
    private Map<String, String> templateVariables;
    private Instant data; 
    private Integer slaSeconds; 

    public OutboundRequest() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(Map<String, String> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public Instant getData() {
        return data;
    }

    public void setData(Instant data) {
        this.data = data;
    }

    public Integer getSlaSeconds() {
        return slaSeconds;
    }

    public void setSlaSeconds(Integer slaSeconds) {
        this.slaSeconds = slaSeconds;
    }

}
