package ar.edu.unlam.sinaliento.dto;

public class EventResponse {

    private Boolean success;

    private String env;

    private EventObject event;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public EventObject getEvent() {
        return event;
    }

    public void setEvent(EventObject event) {
        this.event = event;
    }
}
