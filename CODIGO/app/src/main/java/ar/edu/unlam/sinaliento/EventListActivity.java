package ar.edu.unlam.sinaliento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class EventListActivity extends AppCompatActivity {

    SharedPreferences eventSharedPreferences;
    ListView mEventListView;
    Button mBackToAppBtn;
    ArrayList<String> eventArrayList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        eventSharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.event_shared_preferences_name),
                Context.MODE_PRIVATE
        );

        eventArrayList = new ArrayList<>();
        fillEventList();

        mEventListView = findViewById(R.id.eventListView);
        mBackToAppBtn = findViewById(R.id.backToAppBtn);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventArrayList);

        mBackToAppBtn.setOnClickListener(v -> {
            finish();
        });

        mEventListView.setAdapter(arrayAdapter);
    }

    private synchronized void fillEventList(){
        Map<String,?> keys = eventSharedPreferences.getAll();

        for (Map.Entry<String,?> entry : keys.entrySet()){
            eventArrayList.add(entry.getValue().toString());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
