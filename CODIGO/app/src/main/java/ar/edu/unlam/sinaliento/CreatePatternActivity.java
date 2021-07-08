package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import ar.edu.unlam.sinaliento.utils.MySharedPreferences;

public class CreatePatternActivity extends AppCompatActivity {

    String finalPattern = "";
    PatternLockView mPatternLockView;
    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pattern);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.patternExists()) {
            startUnlockActivity();
        }

        else {
            listenPattern();
            listenButtonToSave();
        }
    }

    private void listenPattern() {
        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {}

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                finalPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
            }

            @Override
            public void onCleared() {}
        });
    }

    private void listenButtonToSave() {
        Button btnSaveUnlockPattern = findViewById(R.id.btnSaveUnlockPattern);
        btnSaveUnlockPattern.setOnClickListener(v -> {
            sharedPreferences.setPattern(finalPattern);
            Toast.makeText(CreatePatternActivity.this, getString(R.string.created_pattern_text), Toast.LENGTH_SHORT).show();
            startUnlockActivity();
        });
    }

    private void startUnlockActivity() {
        finish();
    }

}
