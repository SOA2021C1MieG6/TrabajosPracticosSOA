package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

import ar.edu.unlam.sinaliento.utils.MySharedPreferences;

public class InitialActivity extends AppCompatActivity {

    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.patternExists()) {
            startUnlockActivity();
        }

        else {
            listenButtonToAccept();
        }
    }

    private void listenButtonToAccept() {

        Button btnSaveUnlockPattern = findViewById(R.id.btnAcceptCreatePattern);
        btnSaveUnlockPattern.setOnClickListener(v -> startPatternActivity());
    }

    private void startUnlockActivity() {
        Intent intent = new Intent(InitialActivity.this, UnlockActivity.class);
        startActivity(intent);
    }

    private void startPatternActivity() {
        Intent intent = new Intent(InitialActivity.this, CreatePatternActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            ActivityCompat.finishAffinity(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
