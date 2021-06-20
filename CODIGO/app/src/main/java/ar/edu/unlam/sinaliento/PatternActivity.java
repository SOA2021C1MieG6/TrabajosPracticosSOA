package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class PatternActivity extends AppCompatActivity {

    String finalPattern = "";
    PatternLockView mPatternLockView;
    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        if (sharedPreferences.patternExists()) {
            Log.d("PatternActivity", "UnlockActivity");
            startUnlockActivity();
        }

        else {
            Log.d("PatternActivity", "ListenPattern");
            listenPattern();
            listenButtonToSave();
        }
    }

    private void listenPattern() {
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
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
        Button btnSaveUnlockPattern = (Button) findViewById(R.id.btnSaveUnlockPattern);
        btnSaveUnlockPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.setPattern(finalPattern);
                Toast.makeText(PatternActivity.this, "Patrón guardado con éxito", Toast.LENGTH_SHORT).show();
                startUnlockActivity();
            }
        });
    }

    private void startUnlockActivity() {
        Intent intent = new Intent(PatternActivity.this, UnlockActivity.class);
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
