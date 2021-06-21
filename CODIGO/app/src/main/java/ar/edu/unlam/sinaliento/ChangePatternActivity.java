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

public class ChangePatternActivity extends AppCompatActivity {

    MySharedPreferences sharedPreferences = MySharedPreferences.getSharedPreferences(this);
    String finalPattern = "";
    PatternLockView mPatternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pattern);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listenPattern();
        listenButtonToSave();
    }

    private void startUnlockActivity() {
        Intent intent = new Intent(ChangePatternActivity.this, UnlockActivity.class);
        startActivity(intent);
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
        Button btnChangeUnlockPattern = (Button) findViewById(R.id.btnChangeUnlockPattern);
        btnChangeUnlockPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.setPattern(finalPattern);
                Toast.makeText(ChangePatternActivity.this, "Patrón guardado", Toast.LENGTH_SHORT).show();
                startUnlockActivity();
            }
        });
    }

}
