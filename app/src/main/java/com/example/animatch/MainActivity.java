package com.example.animatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity
{
    public int[]                animalsArray;
    public final int[]          animalsArrayEasy    = { R.drawable.dog,     R.drawable.lion,    R.drawable.mouse,
                                                        R.drawable.dog,     R.drawable.lion,    R.drawable.mouse,};

    public final int[]          animalsArrayMedium  = { R.drawable.dog,     R.drawable.gorilla, R.drawable.lion,
                                                        R.drawable.monkey,  R.drawable.mouse,   R.drawable.rabbit,
                                                        R.drawable.dog,     R.drawable.gorilla, R.drawable.lion,
                                                        R.drawable.monkey,  R.drawable.mouse,   R.drawable.rabbit };

    public final int[]          animalsArrayHard    = { R.drawable.dog,     R.drawable.gorilla, R.drawable.lion,
                                                        R.drawable.monkey,  R.drawable.mouse,   R.drawable.rabbit,
                                                        R.drawable.chicken, R.drawable.cow,     R.drawable.penguin,
                                                        R.drawable.zebra,   R.drawable.dog,     R.drawable.gorilla,
                                                        R.drawable.lion,    R.drawable.monkey,  R.drawable.mouse,
                                                        R.drawable.rabbit,  R.drawable.chicken, R.drawable.cow,
                                                        R.drawable.penguin, R.drawable.zebra};

    public Vibrator             vib;
    public VibrationEffect      vibrationEffectShort;
    public VibrationEffect      vibrationEffectLong;

    public MediaPlayer          bgm, flip, success, shuffle, victory;

    public SensorManager        mSensorManager;
    public SensorEventListener  mSensorListener;

    public float                mAccel;
    public float                mAccelCurrent;
    public float                mAccelLast;

    public Toast                toast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Hide action bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        animalsArray = animalsArrayEasy;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrationEffectShort = VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE);
        vibrationEffectLong = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);

        /* Init audio */
        bgm     = MediaPlayer.create(MainActivity.this, R.raw.bgm);
        flip    = MediaPlayer.create(MainActivity.this, R.raw.flip);
        success = MediaPlayer.create(MainActivity.this, R.raw.success);
        shuffle = MediaPlayer.create(MainActivity.this, R.raw.shuffle);
        victory = MediaPlayer.create(MainActivity.this, R.raw.victory);
        bgm.setLooping(true);
        bgm.setVolume(0.5f, 0.5f);
        bgm.start();

        /* Init animals array as list */
        List<Integer> animals = new ArrayList<>();
        for (int j : animalsArrayHard)
        {
            animals.add(j);
        }

        /* Randomize icon */
        ImageButton mainMenuIcon = findViewById(R.id.imageButtonMainMenu);
        Random randomNumber      = new Random();
        AtomicBoolean isPressed  = new AtomicBoolean(false);
        mainMenuIcon.setImageResource(animals.get(randomNumber.nextInt(animals.size())));
        mainMenuIcon.setOnClickListener(v -> {
            flip.start();
            if (!isPressed.get())
            {
                mainMenuIcon.setImageResource(R.drawable.hidden);
                isPressed.set(true);
            }
            else
            {
                mainMenuIcon.setImageResource(animals.get(randomNumber.nextInt(animals.size())));
                isPressed.set(false);
            }
        });

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setPaintFlags(continueButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        /* New game button logic */
        Button newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(v -> {
            Intent goToNewGame = new Intent(MainActivity.this, NewGameActivity.class);
            vib.vibrate(vibrationEffectShort);
            startActivity(goToNewGame);
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bgm.pause();
    }
}