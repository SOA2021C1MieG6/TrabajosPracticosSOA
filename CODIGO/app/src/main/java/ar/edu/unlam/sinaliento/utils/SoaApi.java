package ar.edu.unlam.sinaliento.utils;

import ar.edu.unlam.sinaliento.dto.EventRequest;
import ar.edu.unlam.sinaliento.dto.EventResponse;
import ar.edu.unlam.sinaliento.dto.LoginRequest;
import ar.edu.unlam.sinaliento.dto.RegisterRequest;
import ar.edu.unlam.sinaliento.dto.RefreshResponse;
import ar.edu.unlam.sinaliento.dto.RegisterResponse;
import ar.edu.unlam.sinaliento.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface SoaApi {

    @POST("api/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Headers({"Content-Type: application/json"})
    @POST("api/event")
    Call<EventResponse> registerEvent(@Header("Authorization") String token, @Body EventRequest request);

    @PUT("api/refresh")
    Call<RefreshResponse> refreshToken(@Header("Authorization") String token_refresh);
}
