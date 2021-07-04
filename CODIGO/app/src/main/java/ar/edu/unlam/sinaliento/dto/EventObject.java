package ar.edu.unlam.sinaliento.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventObject {

    @SerializedName("type_events")
    private String typeEvents;

    private Integer dni;

    private String description;

    private Integer id;

    public String getTypeEvents() {
        return typeEvents;
    }

    public void setTypeEvents(String typeEvents) {
        this.typeEvents = typeEvents;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
