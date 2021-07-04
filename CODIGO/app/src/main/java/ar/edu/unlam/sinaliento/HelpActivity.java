package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends AppCompatActivity {

    Button mBackToAppBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mBackToAppBtn = findViewById(R.id.backToAppBtn);
        mBackToAppBtn.setOnClickListener(v -> {
            Intent appIntent = new Intent(HelpActivity.this, AppActivity.class);
            startActivity(appIntent);
        });
    }
}
