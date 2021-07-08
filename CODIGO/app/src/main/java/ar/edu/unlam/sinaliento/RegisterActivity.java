package ar.edu.unlam.sinaliento;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ar.edu.unlam.sinaliento.dto.RegisterRequest;
import ar.edu.unlam.sinaliento.dto.RegisterResponse;

import java.util.ArrayList;
import java.util.regex.Pattern;

import ar.edu.unlam.sinaliento.utils.SoaApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ar.edu.unlam.sinaliento.R.string.register_completed_toast_text;

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

    public void register(View view) {
        ArrayList<String> errors =  inputsValidations();

        if (!errors.isEmpty()) {
            for (String error : errors)  {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (MainActivity.isNetworkAvailable(this)) {

                RegisterRequest request = new RegisterRequest();

                request.setEnv(getString(R.string.environment));
                request.setName(name.getText().toString());
                request.setLastName(lastName.getText().toString());
                request.setDni(Long.parseLong(dni.getText().toString()));
                request.setEmail(email.getText().toString());
                request.setPassword(password.getText().toString());
                request.setCommission(Long.parseLong(commission.getText().toString()));
                request.setGroup(Long.parseLong(group.getText().toString()));


                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(getString(R.string.apiURL))
                        .build();

                SoaApi apiSOA = retrofit.create(SoaApi.class);

                Call<RegisterResponse> call = apiSOA.register(request);
                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {

                        if(response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, getString(register_completed_toast_text), Toast.LENGTH_LONG).show();
                            backToLogin(null);
                        }

                        else {
                            Toast.makeText(getApplicationContext(), getString(R.string.unregister_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Log.e(null,t.getMessage());
                    }
                });
            }
            else {
                Toast.makeText(this, getString(R.string.no_internet_conection_text), Toast.LENGTH_LONG).show();
            }


        }

    }

    public ArrayList<String> inputsValidations() {

        //Validation Patterns
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
            errors.add(getString(R.string.empty_dni_register_error));
        }

        if(txtName.isEmpty()) {
            errors.add(getString(R.string.empty_name_register_error));
        }
        else if(!VALID_NAME_REGEX.matcher(txtName).find()) {
            errors.add(getString(R.string.unvalid_name_register_error));
        }

        if(txtLastName.isEmpty()) {
            errors.add(getString(R.string.empty_last_name_register_error));
        }
        else if(!VALID_NAME_REGEX.matcher(txtLastName).find()) {
            errors.add(getString(R.string.unvalid_last_name_register_error));
        }

        if (txtEmail.isEmpty()) {
            errors.add(getString(R.string.empty_email_register_error));
        }
        else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail).find()) {
            errors.add(getString(R.string.unvalid_email_register_error));
        }

        if (txtPassword.isEmpty()) {
            errors.add(getString(R.string.empty_password_register_error));
        }

        if(txtGroup.isEmpty()) {
            errors.add(getString(R.string.empty_group_number_register_error));
        }

        if(txtCommission.isEmpty()) {
            errors.add(getString(R.string.empty_commission_number_register_error));
        }
        else if(!txtCommission.equals("2900")  && !txtCommission.equals("3900")) {
            errors.add(getString(R.string.unvalid_commission_number_register_error));
        }

        return errors;
    }

    public void backToLogin(View view) {
        finish();
    }
}
