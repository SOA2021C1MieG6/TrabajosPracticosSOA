package ar.edu.unlam.sinaliento.dto;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String env;
    private String name;

    @SerializedName("lastname")
    private String lastName;

    private Long dni;
    private String email;
    private String password;
    private Long commission;
    private Long group;

    public void setEnv(String env) {
        this.env = env;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCommission(Long commission) {
        this.commission = commission;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public String getEnv() {
        return env;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getCommission() {
        return commission;
    }

    public Long getGroup() {
        return group;
    }
}
