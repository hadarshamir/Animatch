package com.example.animatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends MainActivity {

    private Button musicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bgm.stop();
        settingsBgm.start();

        /* Music button logic */
        musicButton = findViewById(R.id.musicButton);
        musicButton.setOnClickListener(v ->
        {
            mute(musicButton);
            vib.vibrate(vibrationEffectShort);
        });

        /* Reset button logic */
        Button resetSave = findViewById(R.id.resetSaveButton);
        resetSave.setOnClickListener(v -> {
            Intent goToWarning = new Intent(SettingsActivity.this, WarningActivity.class);
            vib.vibrate(vibrationEffectLong);
            startActivity(goToWarning);
        });

        /* Secrets button logic */
        Button secretsButton = findViewById(R.id.secretsButton);
        secretsButton.setOnClickListener(v -> {
            Intent goToSecrets = new Intent(SettingsActivity.this, DebugActivity.class);
            vib.vibrate(vibrationEffectShort);
            startActivity(goToSecrets);
        });

        if (secretsEnabled)
        {
            secretsButton.setVisibility(View.VISIBLE);
        }
        else
        {
            secretsButton.setVisibility(View.GONE);
        }


        /* Back button logic */
        Button back = findViewById(R.id.backToMenuFromSettings);
        back.setOnClickListener(v ->
        {
            onBackPressed();
            vib.vibrate(vibrationEffectShort);
        });

    }

    private void mute(Button musicButton)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("ANIMATCH", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (shouldMute)
        {
            settingsBgm.pause();
            musicButton.setPaintFlags(musicButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            settingsBgm.start();
            musicButton.setPaintFlags(musicButton.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        shouldMute = !shouldMute;
        editor.putBoolean("SHOULD_MUTE", shouldMute);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!shouldMute())
        {
            settingsBgm.pause();
            musicButton.setPaintFlags(musicButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            settingsBgm.start();
            musicButton.setPaintFlags(musicButton.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        settingsBgm.pause();
    }
}