package com.example.animatch;

import android.content.Context;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    /* Declarations */
    private ImageButton[]       buttons         = null;
    private final int[]         animalsArray    = { R.drawable.dog, R.drawable.gorilla, R.drawable.lion,
                                                    R.drawable.monkey, R.drawable.mouse, R.drawable.rabbit,
                                                    R.drawable.dog, R.drawable.gorilla, R.drawable.lion,
                                                    R.drawable.monkey, R.drawable.mouse, R.drawable.rabbit };
    private final boolean[]     pressedArray    = { false, false, false,
                                                    false, false, false,
                                                    false, false, false,
                                                    false, false, false };
    private int                 pressedCount    = 0;
    private int                 matchCount      = 0;
    private int                 stepsCount      = 0;
    private boolean             shouldMute      = true;
    private boolean             emptyIcon       = true;
    private int                 rotateDirection = 1;

    private LinearLayout        colorIcons;
    private ImageButton         colorIcon;
    private TextView            title;
    private Button              reset;
    private Button              steps;
    private Button              mute;

    private MediaPlayer         bgm, flip, success, shuffle, victory;
    private SensorManager       mSensorManager;
    private SensorEventListener mSensorListener;

    private float               mAccel;
    private float               mAccelCurrent;
    private float               mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /* Hide action bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        /* Init audio */
        bgm     = MediaPlayer.create(MainActivity.this, R.raw.bgm);
        flip    = MediaPlayer.create(MainActivity.this, R.raw.flip);
        success = MediaPlayer.create(MainActivity.this, R.raw.success);
        shuffle = MediaPlayer.create(MainActivity.this, R.raw.shuffle);
        victory = MediaPlayer.create(MainActivity.this, R.raw.victory);
        bgm.setLooping(true);
        bgm.setVolume(0.5f, 0.5f);
        bgm.start();

        /* Init views and buttons */
        title       = findViewById(R.id.title);
        colorIcons  = findViewById(R.id.colorMatchLayout);
        reset       = findViewById(R.id.reset);
        steps       = findViewById(R.id.steps);
        mute        = findViewById(R.id.mute);

        setEmptyIcon(); // Set the empty space between the color icons and the controls

        /* Init buttons array */
        buttons = new ImageButton[] {
                findViewById(R.id.imageButton1), findViewById(R.id.imageButton2), findViewById(R.id.imageButton3),
                findViewById(R.id.imageButton4), findViewById(R.id.imageButton5), findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7), findViewById(R.id.imageButton8), findViewById(R.id.imageButton9),
                findViewById(R.id.imageButton10),findViewById(R.id.imageButton11),findViewById(R.id.imageButton12)
        };

        /* Init animals array as list */
        List<Integer> animals = new ArrayList<>();
        for (int j : animalsArray)
        {
            animals.add(j);
        }

        /* Log tags for debug purposes (they change whenever a new drawable is added) */
        for (int i = 0; i < animalsArray.length; i++)
        {
            Log.d("tags: ", animals.get(i) + ", ");
        }

        /* Shuffle animals array before buttons init */
        Collections.shuffle(animals);

        /* Card button logic */
        for (int index = 0; index < animalsArray.length; index++)
        {
            final int i = index;
            buttons[i].setTag(animals.get(i));
            buttons[i].setOnClickListener(v ->
            {
                flip.start(); // Play sound on click
                buttons[i].setImageResource(pressedArray[i] ? R.drawable.hidden : animals.get(i));
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
                checkMatch(buttons[i], i); // Check for a match
            });
        }

        /* Reset button logic */
        reset.setOnClickListener(v ->
        {
            reset(animals);
            shuffle.start();
            vib.vibrate(5);
        });

        /* Mute button logic */
        mute.setOnClickListener(v ->
        {
            mute(mute, shouldMute);
            shouldMute = !shouldMute;
            vib.vibrate(5);
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
                    reset(animals); // Reset on shake
                    shuffle.start();
                    vib.vibrate(300);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private void checkMatch(ImageButton btn, int index)
    {
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
                pressedArray[i] = false;
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

        if (matchCount == 3) // Halfway
        {
            title.setText(getResources().getString(R.string.halfway));
        }
        else if (matchCount == 6) // Finished
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
        colorIcon = new ImageButton(MainActivity.this);
        colorIcon.setBackgroundResource(R.drawable.color_icon);
        switch (matchedPair.getTag().toString())
        {
            case "2131230823":
            {
                colorIcon.setImageResource(R.drawable.blue);
            }
            break;

            case "2131230825":
            {
                colorIcon.setImageResource(R.drawable.yellow);
            }
            break;

            case "2131230840":
            {
                colorIcon.setImageResource(R.drawable.green);
            }
            break;

            case "2131230859":
            {
                colorIcon.setImageResource(R.drawable.grey);
            }
            break;

            case "2131230861":
            {
                colorIcon.setImageResource(R.drawable.red);
            }
            break;

            case "2131230886":
            {
                colorIcon.setImageResource(R.drawable.pink);
            }
            break;
        }
        colorIcon.setClickable(false);
        colorIcon.setBackgroundColor(212121);
        colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        colorIcons.addView(colorIcon, 100, 100);
    }

    private void reset(List<Integer> animals)
    {
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
            steps.setText(R.string.steps);
            stepsCount = 0;
            setEmptyIcon();
            emptyIcon = true;

            buttons[i].setImageResource(R.drawable.hidden);
            buttons[i].setTag(animals.get(i));
            buttons[i].setClickable(true);
            buttons[i].clearAnimation();
            buttons[i].setRotation(0);
            buttons[i].animate().rotation(rotateDirection * 360).setDuration(500);
            buttons[i].setRotation(0);

            pressedArray[i] = false;
            pressedCount = 0;
            matchCount = 0;
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
        colorIcon = new ImageButton(MainActivity.this);
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
