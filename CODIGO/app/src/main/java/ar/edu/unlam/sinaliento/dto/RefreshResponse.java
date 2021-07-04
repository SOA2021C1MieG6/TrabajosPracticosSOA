package ar.edu.unlam.sinaliento.dto;

import com.google.gson.annotations.SerializedName;

public class RefreshResponse {

    private Boolean success;

    private String token;

    @SerializedName("token_refresh")
    private String tokenRefresh;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenRefresh() {
        return tokenRefresh;
    }

    public void setTokenRefresh(String tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }
}
