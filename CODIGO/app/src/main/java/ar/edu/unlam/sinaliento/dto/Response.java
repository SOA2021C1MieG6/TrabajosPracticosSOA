package ar.edu.unlam.sinaliento.dto;

import com.google.gson.annotations.SerializedName;

public class Response {

    private Boolean success;

    private String env;

    private String token;

    @SerializedName("token_refresh")
    private String tokenRefresh;


    public Boolean getSuccess() {
        return success;
    }

    public String getEnv() {
        return env;
    }

    public String getToken() {
        return token;
    }

    public String getTokenRefresh() {
        return tokenRefresh;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenRefresh(String tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }
}
