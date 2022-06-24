package com.example.animatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class WarningActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        bgm.stop();

        Button backButton = findViewById(R.id.warningBackButton);
        backButton.setOnClickListener(v -> {
            vib.vibrate(vibrationEffectShort);
            onBackPressed();
        });

        Button resetButton = findViewById(R.id.warningResetButton);
        resetButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("ANIMATCH", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("STEPS_EASY", 0);
            editor.putInt("STEPS_MEDIUM", 0);
            editor.putInt("STEPS_HARD", 0);
            editor.putString("DIFFICULTY", "NULL");
            editor.apply();
            vib.vibrate(vibrationEffectShort);
            onBackPressed();
        });
    }
}