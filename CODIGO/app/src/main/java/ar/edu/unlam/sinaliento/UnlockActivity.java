package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class UnlockActivity extends AppCompatActivity {

    String finalPattern = "";
    PatternLockView mPatternLockView;
    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        if (sharedPreferences.patternExists()) {
            checkPattern();
        }

        else {
            startPatternActivity();
        }
    }

    private void checkPattern() {
        setContentView(R.layout.activity_unlock);
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {}

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                finalPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);

                if (finalPattern.equals(sharedPreferences.getPattern())) {
                    Toast.makeText(UnlockActivity.this, "Patrón ingresado correctamente", Toast.LENGTH_SHORT).show();
                    startSessionActivity();
                }

                else {
                    Toast.makeText(UnlockActivity.this, "Patrón incorrecto", Toast.LENGTH_SHORT).show();
                }

                pattern.clear();
            }

            @Override
            public void onCleared() {}
        });
    }

    private void startSessionActivity() {
        Intent intent = new Intent(UnlockActivity.this, SessionActivity.class);
        startActivity(intent);
    }

    private void startPatternActivity() {
        Intent intent = new Intent(UnlockActivity.this, PatternActivity.class);
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
