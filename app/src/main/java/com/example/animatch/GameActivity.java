package com.example.animatch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameActivity extends NewGameActivity
{
    /* Declarations */
    private ImageButton[]       buttons             = null;
    private final boolean[]     pressedArray        = { false, false, false, false,
                                                        false, false, false, false,
                                                        false, false, false, false,
                                                        false, false, false, false,
                                                        false, false, false, false };
    private int                 pressedCount        = 0;
    private int                 matchCount          = 0;
    private int                 stepsCount          = 0;
    private boolean             shouldMute          = true;
    private boolean             emptyIcon           = true;
    private int                 rotateDirection     = 1;
    private int                 secretsCount        = 4;
    private boolean             secretsEnabled      = false;

    private LinearLayout        colorIcons;
    private ImageButton         colorIcon;
    private TextView            title;
    private Button              steps, mute;

    private Intent              goToSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /* Init views and buttons */
        title        = findViewById(R.id.title);
        colorIcons   = findViewById(R.id.colorMatchLayout);
        Button reset = findViewById(R.id.reset);
        steps        = findViewById(R.id.steps);
        mute         = findViewById(R.id.mute);

        setEmptyIcon(); // Set the empty space between the color icons and the controls

        /* Init buttons array */
        buttons = new ImageButton[20];

        /* Init animals array as list */
        List<Integer> animals = new ArrayList<>();

        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.removeAllViews();

        String difficulty = getIntent().getStringExtra("difficulty");
        switch (difficulty)
        {
            case "easy":
            {
                animalsArray = animalsArrayEasy;
                gridLayout.setColumnCount(2);
                gridLayout.setRowCount(3);
                resizeGrid(gridLayout, 450, 450);
            }
            break;

            case "medium":
            {
                animalsArray = animalsArrayMedium;
                gridLayout.setColumnCount(3);
                gridLayout.setRowCount(4);
                resizeGrid(gridLayout, 350, 350);
            }
            break;

            case "hard":
            {
                animalsArray = animalsArrayHard;
                gridLayout.setColumnCount(4);
                gridLayout.setRowCount(5);
                resizeGrid(gridLayout, 250, 250);
            }
            break;
        }
        for (int j : animalsArray)
        {
            animals.add(j);
        }

        /* Log tags for debug purposes (they change whenever a new drawable is added) */
        for (int i = 0; i < animalsArray.length; i++)
        {
            Log.d("stags: ", getResources().getResourceName(animals.get(i)));
            Log.d("tags: ", animals.get(i) + ", ");
        }

        /* Shuffle animals array */
        Collections.shuffle(animals);

        /* Card button logic */
        for (int index = 0; index < animalsArray.length; index++)
        {
            final int i = index;
            buttons[i].setOnClickListener(v ->
            {
                flip.start(); // Play sound on click
                buttons[i].setImageResource(pressedArray[i] ? R.drawable.hidden : animals.get(i));
                buttons[i].setTag(getResources().getResourceName(animals.get(i)));
                if (pressedArray[i]) // Face up
                {
                    pressedCount--;
                }
                else // Face down
                {
                    pressedCount++;
                    steps.setText(String.valueOf(++stepsCount)); // Update steps only on face up flip
                }
                pressedArray[i] = !pressedArray[i];

                checkState(i); // Make sure only two cards are face up at a time
                checkMatch(buttons[i], i, difficulty); // Check for a match
            });
        }

        /* Reset button logic */
        reset.setOnClickListener(v ->
        {
            resetGame(animals);
            vib.vibrate(vibrationEffectShort);
        });

        reset.setOnLongClickListener(v ->
        {
            if (secretsEnabled)
            {
                steps.setText(R.string.steps);
                secretsEnabled = false;
                secretsCount = 4;
            }
            return true;
        });

        /* Settings button logic */
        steps.setOnClickListener(v ->
        {
            if (toast != null)
            {
                toast.cancel();
            }
            goToSettings = new Intent(GameActivity.this, SettingsActivity.class);
            vib.vibrate(vibrationEffectShort);
            if (secretsCount == 1)
            {
                secretsEnabled = true;
                steps.setText(R.string.secrets);
                toast = Toast.makeText(getApplicationContext(), getString(R.string.secret_found), Toast.LENGTH_SHORT);
                toast.show();
            }
            if (secretsCount == 0)
            {
                startActivity(goToSettings);
            }
            else
            {
                if (--secretsCount == 1)
                {
                    toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.secret_steps_1) + " " + secretsCount + " " + getString(R.string.secret_steps_3),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (secretsCount > 0)
                {
                    toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.secret_steps_1) + " " + secretsCount + " " + getString(R.string.secret_steps_2),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        /* Mute button logic */
        mute.setOnClickListener(v ->
        {
            mute(mute, shouldMute);
            shouldMute = !shouldMute;
            vib.vibrate(vibrationEffectShort);
        });

        /* Accelerometer logic */
        mSensorListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;
                if (mAccel > 11)
                {
                    resetGame(animals); // Reset on shake
                    vib.vibrate(vibrationEffectLong);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener,
                                                                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                                                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel          = 10f;
        mAccelCurrent   = SensorManager.GRAVITY_EARTH;
        mAccelLast      = SensorManager.GRAVITY_EARTH;
    }

    private void resizeGrid(GridLayout gridLayout, int width, int height)
    {
        for (int i = 0, k = 0; i < gridLayout.getColumnCount(); i++)
        {
            for (int j = 0; j < gridLayout.getRowCount(); j++)
            {
                ImageButton card = new ImageButton(this);
                card.setBackgroundResource(R.drawable.color_icon);
                card.setImageResource(R.drawable.hidden);
                card.setBackgroundColor(212121);
                card.setScaleType(ImageView.ScaleType.FIT_CENTER);

                GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
                cardParams.width = width;
                cardParams.height = height;
                card.setLayoutParams(cardParams);

                gridLayout.addView(card);

                buttons[k++] = card;
            }
        }
    }

    private void checkMatch(ImageButton btn, int index, String difficultyLevel)
    {
        int difficultyMatchCount = 0;

        for (int i = 0; i < animalsArray.length; i++)
        {
            if (i == index)
            {
                continue;
            }
            if (btn.getTag().equals(buttons[i].getTag()) && pressedArray[i]) // Found a match
            {
                if (matchCount++ != 5)
                {
                    success.start();
                }
                addColorIcon(btn);
                pressedArray[i]     = false;
                pressedArray[index] = false;
                btn.setClickable(false);
                buttons[i].setClickable(false);
                btn.setRotation(0);
                btn.animate().rotation(rotateDirection * 360).setDuration(500);
                buttons[i].setRotation(0);
                buttons[i].animate().rotation(rotateDirection * 360).setDuration(500);

                break;
            }
        }

        switch (difficultyLevel)
        {
            case "easy":
            {
                difficultyMatchCount = 3;
            }
            break;

            case "medium":
            {
                difficultyMatchCount = 6;
            }
            break;

            case "hard":
            {
                difficultyMatchCount = 10;
            }
            break;
        }
        if (matchCount == difficultyMatchCount / 2) // Halfway
        {
            title.setText(getResources().getString(R.string.halfway));
        }
        else if (matchCount == difficultyMatchCount) // Finished
        {
            victory.start();
            title.setText(getResources().getString(R.string.victory));
            for (int i = 0; i < animalsArray.length; i++)
            {
                buttons[i].setRotation(0);
                buttons[i].animate().rotation(rotateDirection * 360).setDuration(500);
            }
        }
    }

    private void checkState(int index)
    {
        if (pressedCount == 3) // Hide two face up cards once a third one is flipped
        {
            pressedCount = 1; // Reset the count
            for (int i = 0; i < animalsArray.length; i++)
            {
                if (i == index)
                {
                    continue; // Doesn't check itself
                }
                if (pressedArray[i])
                {
                    buttons[i].setImageResource(R.drawable.hidden);
                    pressedArray[i] = false;
                }
            }
        }
    }

    private void addColorIcon(ImageButton matchedPair)
    {
        if (emptyIcon)
        {
            colorIcons.removeAllViews();
            emptyIcon = false;
        }
        colorIcon = new ImageButton(GameActivity.this);
        colorIcon.setBackgroundResource(R.drawable.color_icon);
        switch (matchedPair.getTag().toString())
        {
            case "com.example.animatch:drawable/dog":
            {
                colorIcon.setImageResource(R.drawable.blue);
            }
            break;

            case "com.example.animatch:drawable/gorilla":
            {
                colorIcon.setImageResource(R.drawable.yellow);
            }
            break;

            case "com.example.animatch:drawable/lion":
            {
                colorIcon.setImageResource(R.drawable.green);
            }
            break;

            case "com.example.animatch:drawable/monkey":
            {
                colorIcon.setImageResource(R.drawable.grey);
            }
            break;

            case "com.example.animatch:drawable/mouse":
            {
                colorIcon.setImageResource(R.drawable.red);
            }
            break;

            case "com.example.animatch:drawable/rabbit":
            {
                colorIcon.setImageResource(R.drawable.pink);
            }
            break;

            case "com.example.animatch:drawable/chicken":
            {
                colorIcon.setImageResource(R.drawable.dark_blue);
            }
            break;

            case "com.example.animatch:drawable/cow":
            {
                colorIcon.setImageResource(R.drawable.dark_green);
            }
            break;

            case "com.example.animatch:drawable/penguin":
            {
                colorIcon.setImageResource(R.drawable.light_grey);
            }
            break;

            case "com.example.animatch:drawable/zebra":
            {
                colorIcon.setImageResource(R.drawable.brown);
            }
            break;
        }
        colorIcon.setClickable(false);
        colorIcon.setBackgroundColor(212121);
        colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        colorIcons.addView(colorIcon, 100, 100);
    }

    private void resetGame(List<Integer> animals)
    {
        shuffle.start();
        Collections.shuffle(animals); // Shuffle the animals array

        /* Alternate rotation */
        if (rotateDirection == 1)
        {
            rotateDirection = -1;
        }
        else
        {
            rotateDirection = 1;
        }

        for (int i = 0; i < animalsArray.length; i++)
        {
            title.setText(getResources().getString(R.string.instructions));
            if (secretsEnabled)
            {
                steps.setText(R.string.secrets);
            }
            else
            {
                steps.setText(R.string.steps);
            }
            stepsCount      = 0;
            emptyIcon       = true;
            setEmptyIcon();

            buttons[i].setImageResource(R.drawable.hidden);
            buttons[i].setTag(getResources().getResourceName(animals.get(i)));
            buttons[i].setClickable(true);
            buttons[i].clearAnimation();
            buttons[i].setRotation(0);
            buttons[i].animate().rotation(rotateDirection * 360).setDuration(500);
            buttons[i].setRotation(0);

            pressedArray[i] = false;
            pressedCount    = 0;
            matchCount      = 0;
        }
    }

    private void mute(Button muteButton, boolean shouldMute)
    {
        if (shouldMute)
        {
            bgm.pause();
            muteButton.setPaintFlags(muteButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            bgm.start();
            muteButton.setPaintFlags(muteButton.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private void setEmptyIcon()
    {
        colorIcons.removeAllViews();
        colorIcon = new ImageButton(GameActivity.this);
        colorIcon.setBackgroundResource(R.drawable.color_icon);
        colorIcon.setImageResource(R.drawable.empty);
        colorIcon.setClickable(false);
        colorIcon.setBackgroundColor(212121);
        colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        colorIcons.addView(colorIcon, 100, 100);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bgm.pause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (shouldMute)
        {
            bgm.start();
        }
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
