package com.example.animatch;

import android.content.Context;
import android.content.Intent;
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

        Intent goToGame = new Intent(NewGameActivity.this, GameActivity.class);

        /* easy button logic */
        ImageButton easyGameButton = findViewById(R.id.easyButton);
        easyGameButton.setOnClickListener(v -> {
            goToGame.putExtra("difficulty", "easy");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

        /* medium button logic */
        ImageButton mediumGameButton = findViewById(R.id.mediumButton);
        mediumGameButton.setOnClickListener(v -> {
            goToGame.putExtra("difficulty", "medium");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

        /* hard button logic */
        ImageButton hardGameButton = findViewById(R.id.hardButton);
        hardGameButton.setOnClickListener(v -> {
            goToGame.putExtra("difficulty", "hard");
            flip.start(); // Play sound on click
            vib.vibrate(vibrationEffectShort);
            startActivity(goToGame);
        });

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
        bgm.pause();
    }
}