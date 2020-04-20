package com.tokbox.multipartytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class JavaMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button startBtn = findViewById(R.id.button_join_room);
        startBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, JavaCallActivity.class);
            startActivity(intent);
        });
    }
}
