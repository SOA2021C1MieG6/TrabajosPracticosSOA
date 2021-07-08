package ar.edu.unlam.sinaliento;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
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

    SimpleDateFormat formatter;

    private SensorManager mSensorManager;
    private Sensor mSensorGyroscope;
    private Sensor mSensorProximity;

    private Switch mSensorEventRegisterSwitch;

    private TextView txtProximity;
    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;

    private double proximityValue;
    private boolean isOn;

    private MediaPlayer mp;

    private final int WAIT_TIME_MS = 10000;

    private int PROXIMITY_CLOSER = 0;

    private int X_EVENT_GYRO_KEY = 0;
    private int Y_EVENT_GYRO_KEY = 1;
    private int Z_EVENT_GYRO_KEY = 2;

    private int EVENT_PROXIMITY_KEY = 0;

    private final long TIME_NOT_DEFINED = -1;
    private long initialTime = TIME_NOT_DEFINED;
    private long differenceTime;

    private final float MINIMUM_TO_BREATHE = 0.05f;

    private final int EMAIL_EXECUTED = 801;
    private final int PERMISSION_REQUEST_SEND_SMS = 1;

    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);
    SharedPreferences eventSharedPreferences;
    SharedPreferences.Editor eventEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        eventSharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.event_shared_preferences_name),
                Context.MODE_PRIVATE
        );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        refreshToken();

        txtProximity = findViewById(R.id.tvProximity);
        txtGyroX = findViewById(R.id.tvGyroX);
        txtGyroY = findViewById(R.id.tvGyroY);
        txtGyroZ = findViewById(R.id.tvGyroZ);

        mSensorEventRegisterSwitch = findViewById(R.id.sensorEventRegisterSwitch);

        isOn = false;

        mp = MediaPlayer.create(this, R.raw.beep_alert);

        initializeProximitySensor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtGyroX.setText(getString(R.string.x_respiration_hint));
        txtGyroY.setText(getString(R.string.y_respiration_hint));
        txtGyroZ.setText(getString(R.string.z_respiration_hint));
    }

    public void logOut(View view) {
        stopSensors();
        mp.stop();
        sharedPreferences.setToken(null);
        sharedPreferences.setTokenRefresh(null);
        sharedPreferences.setEmail(null);

        Toast.makeText(this, getString(R.string.finished_session_text), Toast.LENGTH_SHORT).show();

        finish();
    }

    public void controlRespiration(View view) {

        if (proximityValue != 0) {
            Toast.makeText(this, getString(R.string.phone_should_be_closer_text), Toast.LENGTH_SHORT).show();
        }

        if (!isOn) {
            initializeGyroscope();
            isOn = true;
        }
    }

    private synchronized void checkProximity(float proximity) {
        proximityValue = proximity;

        txtProximity.setText(getString(R.string.proximity_value_text) + proximityValue);

        eventEditor = eventSharedPreferences.edit();
        eventEditor.putString(
                formatter.format(System.currentTimeMillis()),
                getString(R.string.proximity_value_text) + proximityValue
        );
        eventEditor.apply();

        if (mSensorEventRegisterSwitch.isChecked()) {
            registerProximityEvent(proximityValue);
        }
    }

    private synchronized void checkRespiration(float xValue, float yValue, float zValue) {
            float totalGyro = Math.abs(xValue) + Math.abs(yValue) + Math.abs(zValue);
            Log.e("totalGyro", Float.toString(totalGyro));

            // Giroscopio inferior al umbral (no respira)
            if(totalGyro <= MINIMUM_TO_BREATHE) {
                if (initialTime == TIME_NOT_DEFINED) {
                    initialTime = System.currentTimeMillis();
                }

                Log.e("initialTime", Long.toString(initialTime));
                differenceTime = System.currentTimeMillis() - initialTime;
                Log.e("differenceTime", Long.toString(differenceTime));

                // Tiempo que pasó sin respirar supera al umbral
                if (differenceTime >= WAIT_TIME_MS) {

                    stopSensor(mSensorGyroscope);
                    generateAlert(null);
                    Toast.makeText(this, getString(R.string.ambulance_alert_text), Toast.LENGTH_LONG).show();

                    isOn = false;
                    initialTime = TIME_NOT_DEFINED;
                    txtGyroX.setText(getString(R.string.x_respiration_hint));
                    txtGyroY.setText(getString(R.string.y_respiration_hint));
                    txtGyroZ.setText(getString(R.string.z_respiration_hint));

                }
            }

            else {
                initialTime = TIME_NOT_DEFINED;
            }

            if (mSensorEventRegisterSwitch.isChecked()) {
                registerGyroscopeEvent(xValue, yValue, zValue);
            }

            txtGyroX.setText(String.format("%.2f", xValue));
            txtGyroY.setText(String.format("%.2f", yValue));
            txtGyroZ.setText(String.format("%.2f", zValue));

            eventEditor = eventSharedPreferences.edit();
            eventEditor.putString(
                    formatter.format(System.currentTimeMillis()),
                    getString(R.string.gyroscope_values_text) +
                            getString(R.string.gyroscope_x_value_text) + xValue +
                            getString(R.string.gyroscope_y_value_text) + yValue +
                            getString(R.string.gyroscope_z_value_text) + zValue
            );
            eventEditor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            Log.d("Sensor", event.sensor.getName());

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                checkProximity(event.values[EVENT_PROXIMITY_KEY]);
            }

            if(proximityValue == PROXIMITY_CLOSER && isOn == true) {

                if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    checkRespiration(
                            event.values[X_EVENT_GYRO_KEY],
                            event.values[Y_EVENT_GYRO_KEY],
                            event.values[Z_EVENT_GYRO_KEY]
                    );
                }

                else {
                    initializeSensor(mSensorGyroscope);
                }
            }
            else if(isOn == true){
                stopSensor(mSensorGyroscope);
                initialTime = TIME_NOT_DEFINED;
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

    public void goToEventList(View view) {
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
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

        if (requestCode == EMAIL_EXECUTED) {
            Toast.makeText(this, getString(R.string.finished_email_execution_toast_text), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void sendSMS() {
        if (!checkSMSPermission()) return;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = getString(R.string.phone_alert_text) + sharedPreferences.getEmail();
            String phone = sharedPreferences.getPhone();
            smsManager.sendTextMessage(
                    phone,
                    null,
                    message,
                    null,
                    null
            );
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.finished_sms_execution_toast_text),
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception ex) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.failed_sms_execution_toast_text),
                    Toast.LENGTH_SHORT
            ).show();
            ex.printStackTrace();
        }
    }

    public void generateAlert(View view) {
        activateBeep();

        if (sharedPreferences.isEnablePhone()) {
            sendSMS();
        }

        sendEmail();
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
