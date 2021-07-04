package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ar.edu.unlam.sinaliento.dto.EventRequest;
import ar.edu.unlam.sinaliento.dto.EventResponse;
import ar.edu.unlam.sinaliento.dto.LoginRequest;
import ar.edu.unlam.sinaliento.dto.LoginResponse;

import java.util.ArrayList;
import java.util.regex.Pattern;

import ar.edu.unlam.sinaliento.utils.MySharedPreferences;
import ar.edu.unlam.sinaliento.utils.SoaApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ar.edu.unlam.sinaliento.R.*;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private String message;

    ProgressDialog progressDialog;

    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        email = findViewById(id.txtEmail);
        password = findViewById(id.txtPassword);
        message = "";
    }

    //Método para loguearse en la aplicación
    public void logIn(View view) {
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        ArrayList<String> errors = new ArrayList<String>();
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        //Validaciones de inputs de login
        if (txtEmail.isEmpty()) {
            errors.add(getString(string.email_error_login_text));
        }
        else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail).find()) {
            errors.add(getString(string.unvalid_email_error_login_text));
        }

        if (txtPassword.isEmpty()) {
            errors.add(getString(string.password_error_email_text));
        }

        if (!errors.isEmpty()) {
            for (String error : errors)  {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        }

        else {
            doLogin();
        }
    }

    private void doLogin() {
        progressDialog = ProgressDialog.show(
                this,
                getString(string.login_dialog_title),
                getString(string.login_dialog_message),
                true
        );

        if (isNetworkAvailable(this)) {

            LoginRequest loginRequest = new LoginRequest();

            loginRequest.setEmail(email.getText().toString());
            loginRequest.setPassword(password.getText().toString());

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(getString(string.apiURL))
                    .build();

            SoaApi apiSOA = retrofit.create(SoaApi.class);
            Call<LoginResponse> call = apiSOA.login(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                             @Override
                             public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                 if (response.isSuccessful()) {
                                     Intent app = new Intent(getApplicationContext(), AppActivity.class);

                                     sharedPreferences.setToken(response.body().getToken());
                                     sharedPreferences.setTokenRefresh(response.body().getTokenRefresh());
                                     sharedPreferences.setEmail(loginRequest.getEmail());

                                     registerLoginEvent(loginRequest.getEmail());

                                     progressDialog.dismiss();

                                     Toast.makeText(MainActivity.this, getString(string.started_session_text), Toast.LENGTH_SHORT).show();

                                     startActivity(app);
                                 } else {
                                     Log.e(null, response.toString());

                                     progressDialog.dismiss();

                                     Toast.makeText(MainActivity.this, getString(string.unstarted_session_text), Toast.LENGTH_SHORT).show();
                                 }
                             }

                             @Override
                             public void onFailure(Call<LoginResponse> call, Throwable t) {
                                 Log.e(null, t.getMessage());

                                 progressDialog.dismiss();
                             }
                         }
            );

        }

        else {
            progressDialog.dismiss();
            Toast.makeText(this, getString(string.no_internet_conection_text), Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null)  return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    }

                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }

                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }

        Log.i("update_status","Network is available : FALSE ");
        return false;
    }

    //Creo método para el botón registrate
    public void register(View view){
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }

    private void registerLoginEvent(String email) {
        EventRequest eventRequest = new EventRequest();

        eventRequest.setEnv(getString(R.string.environment));
        eventRequest.setTypeEvents(getString(string.login_type_event));
        eventRequest.setDescription(getString(string.login_description) + email);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.apiURL))
                .build();

        SoaApi apiRegister = retrofit.create(SoaApi.class);
        Call<EventResponse> call = apiRegister.registerEvent("Bearer " + sharedPreferences.getToken(), eventRequest);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {

                if(response.isSuccessful()) {
                    Log.e("Evento Login", "Evento Registrado");
                }

                else {
                    Log.e("Evento Login", "Evento No Registrado");
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Log.e(null,t.getMessage());
            }
        });
    }


    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }

        else {
            Toast.makeText(getBaseContext(), getString(string.close_application_alert), Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}
