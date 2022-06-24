package com.example.animatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VictoryActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);

        MediaPlayer victoryBgm = MediaPlayer.create(VictoryActivity.this, R.raw.victory_screen);
        bgm.stop();
        victoryBgm.start();

        String victoryDifficultyString = getIntent().getStringExtra("difficulty");
        int stepsCountString = getIntent().getIntExtra("steps", 0);

        Button victoryDifficulty = findViewById(R.id.victoryDifficulty);
        Button victoryScore = findViewById(R.id.victoryScore);

        victoryDifficulty.setText(victoryDifficultyString);
        victoryScore.setText(Integer.toString(stepsCountString));

        Button menuButton = findViewById(R.id.victoryMenuButton);
        menuButton.setOnClickListener(v -> {
            Intent goToMenu = new Intent(VictoryActivity.this, MainActivity.class);
            startActivity(goToMenu);
            vib.vibrate(vibrationEffectShort);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("ANIMATCH", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent goToGame = new Intent(VictoryActivity.this, GameActivity.class);

        Button nextButton = findViewById(R.id.victoryNextButton);

        if (victoryDifficultyString.equals("hard"))
        {
            nextButton.setVisibility(View.GONE);
        }

        nextButton.setOnClickListener(v -> {
            switch (victoryDifficultyString)
            {
                case "easy":
                {
                    editor.putString("DIFFICULTY", "medium");
                    editor.apply();
                    goToGame.putExtra("difficulty", "medium");
                    vib.vibrate(vibrationEffectShort);
                    startActivity(goToGame);
                }
                break;

                case "medium":
                {
                    editor.putString("DIFFICULTY", "hard");
                    editor.apply();
                    goToGame.putExtra("difficulty", "hard");
                    vib.vibrate(vibrationEffectShort);
                    startActivity(goToGame);
                }
                break;
            }
        });
    }
}