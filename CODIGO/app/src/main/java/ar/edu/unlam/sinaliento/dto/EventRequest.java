package ar.edu.unlam.sinaliento.dto;

import com.google.gson.annotations.SerializedName;

public class EventRequest {
    private String env;

    @SerializedName("type_events")
    private String typeEvents;

    private String description;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTypeEvents() {
        return typeEvents;
    }

    public void setTypeEvents(String typeEvents) {
        this.typeEvents = typeEvents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
