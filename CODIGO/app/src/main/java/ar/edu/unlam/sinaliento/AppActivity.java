package ar.edu.unlam.sinaliento;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import ar.edu.unlam.sinaliento.dto.EventRequest;
import ar.edu.unlam.sinaliento.dto.EventResponse;
import ar.edu.unlam.sinaliento.dto.RefreshResponse;


import ar.edu.unlam.sinaliento.utils.MySharedPreferences;
import ar.edu.unlam.sinaliento.utils.SoaApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppActivity extends AppCompatActivity implements SensorEventListener {

    private static final int ROTATION_WAIT_TIME_MS = 500;
    private long mGyroTime = 0;

    private SensorManager mSensorManager;
    private Sensor mSensorGyroscope;
    private Sensor mSensorProximity;

    private Switch mSensorEventRegisterSwitch;

    private TextView txtProximity;
    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;

    private double valor;
    private boolean isOn;

    private MediaPlayer mp;
    private final int SMS_EXECUTED_AND_SEND_EMAIL = 800;
    private final int EMAIL_EXECUTED = 801;

    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        refreshToken();

        txtProximity = findViewById(R.id.tvProximity);
        txtGyroX = findViewById(R.id.tvGyroX);
        txtGyroY = findViewById(R.id.tvGyroY);
        txtGyroZ = findViewById(R.id.tvGyroZ);

        mSensorEventRegisterSwitch = findViewById(R.id.sensorEventRegisterSwitch);

        isOn = false;
        valor = 10;

        mp = MediaPlayer.create(this, R.raw.beep_alert);

        initializeProximitySensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void logOut(View view) {
        stopSensors();
        mp.stop();
        sharedPreferences.setToken("");
        sharedPreferences.setTokenRefresh("");
        Intent login = new Intent(this, MainActivity.class);

        Toast.makeText(this, getString(R.string.finished_session_text), Toast.LENGTH_SHORT).show();

        startActivity(login);
    }

    public void initApp(View view) {

        if (valor == 0) {
            initializeGyroscope();
            isOn = true;
        }
        else {
            Toast.makeText(this, getString(R.string.phone_should_be_closer_text), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            Log.d("Sensor", event.sensor.getName());

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                valor = event.values[0];
                txtProximity.setText(getString(R.string.proximity_value_text) + valor);

                if (mSensorEventRegisterSwitch.isChecked()) {
                    registerProximityEvent(valor);
                }
            }

            if(valor == 0 && isOn == true) {
                if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                    long now = System.currentTimeMillis();

                    long totalGyro = (long)(event.values[0] + event.values[1] + event.values[2]);

                    if(totalGyro == 0) {
                        mGyroTime = System.currentTimeMillis();
                        if ((now - mGyroTime) < ROTATION_WAIT_TIME_MS) {
                            mGyroTime = System.currentTimeMillis();
                        }
                        else {
                            stopSensors();
                            generateAlert(null);
                            Toast.makeText(this, getString(R.string.ambulance_alert_text), Toast.LENGTH_LONG ).show();
                        }
                    }

                    if (mSensorEventRegisterSwitch.isChecked()) {
                        registerGyroscopeEvent(event.values[0], event.values[1], event.values[2]);
                    }

                    txtGyroX.setText(String.format("%.4f", event.values[0]));
                    txtGyroY.setText(String.format("%.4f", event.values[1]));
                    txtGyroZ.setText(String.format("%.4f", event.values[2]));
                }
            }
            else if(isOn == true){
                txtGyroX.setText(getString(R.string.unobtained_x_value_text));
                txtGyroY.setText(getString(R.string.unobtained_y_value_text));
                txtGyroZ.setText(getString(R.string.unobtained_z_value_text));
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void initializeSensor(Sensor sensor) {
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initializeProximitySensor() {
        initializeSensor(mSensorProximity);
    }

    private void initializeGyroscope() {
        initializeSensor(mSensorGyroscope);
    }

    private void stopSensor(Sensor sensor) {
        mSensorManager.unregisterListener(this, sensor);
    }

    private void stopSensors()
    {
        stopSensor(mSensorProximity);
        stopSensor(mSensorGyroscope);
    }

    public void refreshToken() {
        int minutesToRefreshToken = 30;
        int millisecondsToRefreshToken = minutesToRefreshToken * 60 * 1000;

        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            public void run() {
                try {
                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(getString(R.string.apiURL))
                            .build();

                    SoaApi apiSOA = retrofit.create(SoaApi.class);

                    Call<RefreshResponse> call = apiSOA.refreshToken("Bearer " + sharedPreferences.getTokenRefresh());
                    call.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> call, Response<RefreshResponse> response) {

                            if (response.isSuccessful()) {
                                sharedPreferences.setToken(response.body().getToken());
                                sharedPreferences.setTokenRefresh(response.body().getTokenRefresh());
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.unsuccessful_refresh_token_text), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> call, Throwable t) {
                            Log.e(null, t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(doAsynchronousTask, 0, millisecondsToRefreshToken);
    }

    public void configureAlert(View view) {
        Intent intent = new Intent(this, ConfigureAlertActivity.class);
        startActivity(intent);
    }

    private void activateBeep(){
        if (sharedPreferences.isEnableBeep()) {
            mp.start();
        }

        else {
            Toast.makeText(this, getString(R.string.not_enabled_beep_toast_text), Toast.LENGTH_SHORT).show();
        }
    }

    private Intent getIntentSMS() {
        if (!sharedPreferences.isEnablePhone()) return null;

        String message = getString(R.string.phone_alert_text) + sharedPreferences.getEmail();
        String phone = sharedPreferences.getPhone();

        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

        intent.putExtra("address", phone);
        intent.putExtra("sms_body", message);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Getting the default sms app.
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);

            // Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            if (defaultSmsPackageName != null) intent.setPackage(defaultSmsPackageName);
        }

        return intent;
    }

    private Intent getIntentEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String[] emails;

        intent.setData(Uri.parse("mailto:"));

        if (sharedPreferences.isEnableEmail() && sharedPreferences.isEnableAdditionalEmail()) {
            emails = new String[]{sharedPreferences.getEmail(), sharedPreferences.getAdditionalEmail()};
        }

        else if (sharedPreferences.isEnableEmail()) {
            emails = new String[]{sharedPreferences.getEmail()};
        }

        else if (sharedPreferences.isEnableAdditionalEmail()) {
            emails = new String[]{sharedPreferences.getAdditionalEmail()};
        }

        else {
            return null;
        }

        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_alert_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_alert_text) + sharedPreferences.getEmail());

        return intent;
    }

    private void sendEmail() {
        Intent intentEmail = getIntentEmail();

        if (intentEmail == null) return;

        try {
            startActivityForResult(Intent.createChooser(intentEmail, getString(R.string.email_chooser_title)), EMAIL_EXECUTED);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.email_chooser_not_install_exception), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SMS_EXECUTED_AND_SEND_EMAIL) {
            Toast.makeText(this, getString(R.string.finished_sms_execution_toast_text), Toast.LENGTH_SHORT).show();
            sendEmail();
        }

        else if (requestCode == EMAIL_EXECUTED) {
            Toast.makeText(this, getString(R.string.finished_email_execution_toast_text), Toast.LENGTH_SHORT).show();
        }
    }

    public void generateAlert(View view) {
        activateBeep();

        Intent intentSMS = getIntentSMS();

        if (intentSMS == null) {
            if (sharedPreferences.isEnableEmail() || sharedPreferences.isEnableAdditionalEmail()) {
                sendEmail();
            }

            else {
                Toast.makeText(this, getString(R.string.not_enabled_alert_toast_text), Toast.LENGTH_SHORT).show();
            }
        }

        else {
            startActivityForResult(intentSMS, SMS_EXECUTED_AND_SEND_EMAIL);
        }
    }

    public void help(View view) {
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);
    }

    private void registerProximityEvent(double value) {
        EventRequest eventRequest = new EventRequest();

        eventRequest.setEnv(getString(R.string.environment));
        eventRequest.setTypeEvents(getString(R.string.proximity_type_event));
        eventRequest.setDescription(getString(R.string.value_description) + value);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.apiURL))
                .build();

        SoaApi apiRegister = retrofit.create(SoaApi.class);
        Call<EventResponse> call = apiRegister.registerEvent("Bearer " + sharedPreferences.getToken(), eventRequest);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, retrofit2.Response<EventResponse> response) {

                if(response.isSuccessful()) {
                    Log.e("Evento Proximidad", "Evento Registrado");
                }

                else {
                    Log.e("Evento Proximidad", "Evento No Registrado");
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Log.e(null,t.getMessage());
            }
        });
    }

    private void registerGyroscopeEvent(float valueX, float valueY, float valueZ) {
        EventRequest eventRequest = new EventRequest();

        eventRequest.setEnv(getString(R.string.environment));
        eventRequest.setTypeEvents(getString(R.string.gyroscope_type_event));
        eventRequest.setDescription(
                getString(R.string.x_value_description) + valueX +
                getString(R.string.y_value_description) + valueY +
                getString(R.string.z_value_description) + valueZ
        );

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
                    Log.e("Evento Giroscopio", "Evento Registrado");
                }

                else {
                    Log.e("Evento Giroscopio", "Evento No Registrado");
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
            logOut(null);
            return;
        }

        else {
            Toast.makeText(getBaseContext(), getString(R.string.close_session_alert), Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}
