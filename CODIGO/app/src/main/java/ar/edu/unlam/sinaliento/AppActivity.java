package ar.edu.unlam.sinaliento;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AppActivity extends AppCompatActivity implements SensorEventListener {

    private static final int ROTATION_WAIT_TIME_MS = 500;
    private long mGyroTime = 0;

    private SensorManager mSensorManager;
    private Sensor mSensorGyroscope;
    private Sensor mSensorProximity;



    private TextView txtGyroX;
    private TextView txtGyroY;
    private TextView txtGyroZ;

    private double valor;
    private boolean isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        txtGyroX = findViewById(R.id.tvGyroX);
        txtGyroY = findViewById(R.id.tvGyroY);
        txtGyroZ = findViewById(R.id.tvGyroZ);
        isOn = false;
        valor = 10;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void ToLogin(View view) {
        StopSensors();
        Intent login = new Intent(this, MainActivity.class);
        startActivity(login);
    }

    public void InitApp(View view) {

        if (valor == 0) {
            InitializeSensors();
            isOn = true;
        }
        else {
            Toast.makeText(this, "Debe aproximar el celular", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            Log.d("Sensor", event.sensor.getName());

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                valor = event.values[0];
                Toast.makeText(this, "Proximidad: " + valor, Toast.LENGTH_LONG).show();
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
                            StopSensors();
                            Toast.makeText(this, "Se ha enviado una ambulancia a su ubicacion", Toast.LENGTH_LONG ).show();
                        }
                    }

                    txtGyroX.setText(Float.toString(event.values[0]));
                    txtGyroY.setText(Float.toString(event.values[1]));
                    txtGyroZ.setText(Float.toString(event.values[2]));
                }
            }
            else if(isOn == true){
                txtGyroX.setText("No puede obtenerse el valor");
                txtGyroY.setText("No puede obtenerse el valor");
                txtGyroZ.setText("No puede obtenerse el valor");
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void InitializeSensors () {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void StopSensors()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }
}
