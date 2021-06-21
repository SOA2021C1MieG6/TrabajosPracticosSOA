package ar.edu.unlam.sinaliento;

import ar.edu.unlam.sinaliento.dto.Login;
import ar.edu.unlam.sinaliento.dto.Post;
import ar.edu.unlam.sinaliento.dto.Response;
import ar.edu.unlam.sinaliento.dto.Session;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterApi {

    @POST("api/register")
    Call<Response> register(@Body Post request);
    @POST("api/login")
    Call<Session> login(@Body Login request);
}
