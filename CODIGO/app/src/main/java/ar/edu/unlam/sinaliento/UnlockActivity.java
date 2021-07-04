package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import ar.edu.unlam.sinaliento.utils.MySharedPreferences;

public class UnlockActivity extends AppCompatActivity {

    String finalPattern = "";
    PatternLockView mPatternLockView;
    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    private TextView batteryTxt;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryTxt.setText(getString(R.string.battery_level_text) + level + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPreferences.patternExists()) {
            checkPattern();

            batteryTxt = this.findViewById(R.id.batteryTxt);
            this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        else {
            startInitialActivity();
        }
    }

    private void checkPattern() {
        setContentView(R.layout.activity_unlock);
        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {}

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                finalPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);

                if (finalPattern.equals(sharedPreferences.getPattern())) {
                    Toast.makeText(UnlockActivity.this, getString(R.string.correct_pattern_text), Toast.LENGTH_SHORT).show();
                    if (changePatternCheckboxIsChecked()) {
                        startChangePatternActivity();
                    }
                    else {
                        startMainActivity();
                    }
                }

                else {
                    Toast.makeText(UnlockActivity.this, getString(R.string.wrong_pattern_text), Toast.LENGTH_SHORT).show();
                }

                pattern.clear();
            }

            @Override
            public void onCleared() {}
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(UnlockActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void startInitialActivity() {
        Intent intent = new Intent(UnlockActivity.this, InitialActivity.class);
        startActivity(intent);
    }

    private boolean changePatternCheckboxIsChecked() {
        CheckBox changePatternCheckbox = findViewById(R.id.changePatternCheckbox);

        return changePatternCheckbox.isChecked();
    }

    private void startChangePatternActivity() {
        Intent intent = new Intent(UnlockActivity.this, ChangePatternActivity.class);
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
