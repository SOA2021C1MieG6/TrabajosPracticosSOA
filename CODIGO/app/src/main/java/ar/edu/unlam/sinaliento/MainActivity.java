package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ar.edu.unlam.sinaliento.dto.Login;
import ar.edu.unlam.sinaliento.dto.Session;

import java.util.ArrayList;
import java.util.regex.Pattern;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        email = findViewById(id.txtEmail);
        password = findViewById(id.txtPassword);
        message = "";
    }

    //Metodo para loguearse en la aplicación
    public void LogIn (View view) {
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        ArrayList<String> errors = new ArrayList<String>();
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        //Validaciones de inputs de login
        if (txtEmail.isEmpty()) {
            errors.add("Debe ingresar un email");
        }
        else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail).find()) {
            errors.add("Debe ingresar un email válido");
        }

        if (txtPassword.isEmpty()) {
            errors.add("Debe ingresar una password");
        }

        if (!errors.isEmpty()) {
            for (String error : errors)  {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        }
        else {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                Login loginRequest = new Login();

                loginRequest.setEmail(email.getText().toString());
                loginRequest.setPassword(password.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://so-unlam.net.ar/api/")
                        .build();

                RegisterApi apiRegister = retrofit.create(RegisterApi.class);
                Call<Session> call = apiRegister.login(loginRequest);
                call.enqueue(new Callback<Session>() {
                                 @Override
                                 public void onResponse(Call<Session> call, Response<Session> response) {

                                     if (response.isSuccessful()) {
                                         Intent app = new Intent(getApplicationContext(), AppActivity.class);
                                         startActivity(app);
                                     } else {
                                         Log.e(null, response.toString());
                                     }
                                 }

                                 @Override
                                 public void onFailure(Call<Session> call, Throwable t) {
                                     Log.e(null, t.getMessage());
                                 }
                             }
                );

            } else {
                Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_LONG).show();
            }

        }
    }

    //creo metodo para el boton registrate
    public void Registrarse(View view){
        Intent registrarse = new Intent(this, RegisterActivity.class);
        startActivity(registrarse);
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
