package ar.edu.unlam.sinaliento;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.unlam.sinaliento.dto.Post;
import ar.edu.unlam.sinaliento.dto.Response;

import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText dni;
    private EditText name;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText commission;
    private EditText group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dni = findViewById(R.id.txtDni);
        name = findViewById(R.id.txtName);
        lastName = findViewById(R.id.txtLastName);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        group = findViewById(R.id.txtGroup);
        commission = findViewById(R.id.txtCommision);
    }

    public void Register(View view) {
        ArrayList<String> errors =  inputsValidations();

        if (!errors.isEmpty()) {
            for (String error : errors)  {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        }
        else {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                Post request = new Post();

                request.setEnv("PROD");
                request.setName(name.getText().toString());
                request.setLastName(lastName.getText().toString());
                request.setDni(Long.parseLong(dni.getText().toString()));
                request.setEmail(email.getText().toString());
                request.setPassword(password.getText().toString());
                request.setCommission(Long.parseLong(commission.getText().toString()));
                request.setGroup(Long.parseLong(group.getText().toString()));


                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://so-unlam.net.ar/api/")
                        .build();

                RegisterApi apiRegister = retrofit.create(RegisterApi.class);

                Call<Response> call = apiRegister.register(request);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                        if(response.isSuccessful()) {
                            Intent app = new Intent(getApplicationContext(), AppActivity.class);
                            startActivity(app);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "No anduvo bien", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Log.e(null,t.getMessage());
                    }
                });
            }
            else {
                Toast.makeText(this, "No hay conexion a internet", Toast.LENGTH_LONG).show();
            }


        }

    }


    public ArrayList<String> inputsValidations() {

        //VALIDATION PATTERNS
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Pattern VALID_NAME_REGEX = Pattern.compile("^[A-Za-z\\s]+[\\.]?[A-Za-z\\s]*$");

        ArrayList<String> errors = new ArrayList<String>();
        String txtDni = dni.getText().toString();
        String txtName = name.getText().toString();
        String txtLastName = lastName.getText().toString();
        String txtEmail = email.getText().toString();
        String txtPassword = password.getText().toString();
        String txtGroup = group.getText().toString();
        String txtCommission = commission.getText().toString();

        if(txtDni.isEmpty()){
            errors.add("Debe ingresar un DNI");
        }

        if(txtName.isEmpty()) {
            errors.add("Debe ingresar un nombre");
        }
        else if(!VALID_NAME_REGEX.matcher(txtName).find()) {
            errors.add("Debe ingresar un nombre valido");
        }

        if(txtLastName.isEmpty()) {
            errors.add("Debe ingresar un apellido");
        }
        else if(!VALID_NAME_REGEX.matcher(txtLastName).find()) {
            errors.add("Debe ingresar un apellido valido");
        }

        if (txtEmail.isEmpty()) {
            errors.add("Debe ingresar un email");
        }
        else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail).find()) {
            errors.add("Debe ingresar un email válido");
        }

        if (txtPassword.isEmpty()) {
            errors.add("Debe ingresar una password");
        }

        if(txtGroup.isEmpty()) {
            errors.add("Debe ingresar un numero de grupo");
        }

        if(txtCommission.isEmpty()) {
            errors.add("Debe ingresar un numero de comision");
        }
        else if(!txtCommission.equals("2900")  && !txtCommission.equals("3900")) {
            errors.add("Debe ingresar un numero de comision valido (2900 o 3900)");
        }

        return errors;
    }

    ///metodo para el boton enviar
    public void ToLogin(View view) {
        Intent login = new Intent(this, MainActivity.class);
        startActivity(login);
    }
}
