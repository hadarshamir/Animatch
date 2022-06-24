package com.example.animatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageButton;

public class NewGameActivity extends MainActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffectShort = VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE);

        SharedPreferences sharedPreferences = getSharedPreferences("ANIMATCH", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        bgm.pause();

        Intent goToGame = new Intent(NewGameActivity.this, GameActivity.class);

        /* Easy button logic */
        ImageButton easyGameButton = findViewById(R.id.easyButton);
        easyGameButton.setOnClickListener(v -> {
            editor.putString("DIFFICULTY", "easy");
            editor.apply();
            goToGame.putExtra("difficulty", "easy");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

        /* Medium button logic */
        ImageButton mediumGameButton = findViewById(R.id.mediumButton);
        mediumGameButton.setOnClickListener(v -> {
            editor.putString("DIFFICULTY", "medium");
            editor.apply();
            goToGame.putExtra("difficulty", "medium");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

        /* Hard button logic */
        ImageButton hardGameButton = findViewById(R.id.hardButton);
        hardGameButton.setOnClickListener(v -> {
            editor.putString("DIFFICULTY", "hard");
            editor.apply();
            goToGame.putExtra("difficulty", "hard");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

        /* Back button logic */
        Button back = findViewById(R.id.backToMenu);
        back.setOnClickListener(v ->
        {
            onBackPressed();
            vib.vibrate(vibrationEffectShort);
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        settingsBgm.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!shouldMute())
        {
            settingsBgm.pause();
        }
        else
        {
            settingsBgm.start();
        }
    }
}