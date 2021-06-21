package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

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
                Toast.makeText(CreatePatternActivity.this, "Patrón guardado con éxito", Toast.LENGTH_SHORT).show();
                startUnlockActivity();
            }
        });
    }

    private void startUnlockActivity() {
        Intent intent = new Intent(CreatePatternActivity.this, UnlockActivity.class);
        startActivity(intent);
    }

}
