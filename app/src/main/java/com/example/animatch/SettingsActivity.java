package com.example.animatch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity
{
    private final String[]      colorsArray = { "blue", "green", "grey", "pink", "red", "yellow" };
    private EditText            numOfObjects;
    private Spinner             colorPicker;
    private GridLayout          colorIconsGrid;
    private ImageButton         colorIcon;
    private Button              create, random, clear, back;

    private ConstraintLayout    secretsLayout;

    private boolean             isEmptyButton = true;

    private MediaPlayer         bgm, flip, shuffle;

    private SensorManager       mSensorManager;
    private SensorEventListener mSensorListener;

    private float               mAccel;
    private float               mAccelCurrent;
    private float               mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        bgm     = MediaPlayer.create(SettingsActivity.this, R.raw.settings_bgm);
        flip    = MediaPlayer.create(SettingsActivity.this, R.raw.flip);
        shuffle = MediaPlayer.create(SettingsActivity.this, R.raw.shuffle);
        bgm.start();

        /* Hide action bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        secretsLayout = findViewById(R.id.secretsLayout);
        secretsLayout.setOnClickListener(v ->
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        });

        numOfObjects = findViewById(R.id.numOfObjects);
        numOfObjects.setText("1");

        colorIconsGrid  = findViewById(R.id.colorIconsGrid);
        setEmptyIcon();

        colorPicker = findViewById(R.id.colorOfObjects);
        colorPicker.setSelection(0);

        create = findViewById(R.id.create);
        create.setOnClickListener(v ->
        {
            flip.start();
            addColorIconsByNumberAndColor(Integer.parseInt(numOfObjects.getText().toString()), false);
            vib.vibrate(5);
        });

        random = findViewById(R.id.random);
        random.setOnClickListener(v ->
        {
            flip.start();
            addColorIconsByNumberAndColor(Integer.parseInt(numOfObjects.getText().toString()), true);
            vib.vibrate(5);
        });

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(v ->
        {
            resetIcons();
            vib.vibrate(5);
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(v ->
        {
            onBackPressed();
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
                    resetIcons(); // Reset on shake
                    vib.vibrate(300);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
            }
        };
        mSensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent   = SensorManager.GRAVITY_EARTH;
        mAccelLast      = SensorManager.GRAVITY_EARTH;
    }

    private void addColorIconsByNumberAndColor(int numOfColorIcons, boolean isRandom)
    {
        if (numOfColorIcons > 9999)
        {
            Toast.makeText(getApplicationContext(), R.string.crash, Toast.LENGTH_SHORT).show();
            return;
        }
        String chosenColor = colorPicker.getSelectedItem().toString();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText())
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (isEmptyButton)
        {
            colorIconsGrid.removeAllViews();
            isEmptyButton = false;
        }

        for (int i = 0; i < numOfColorIcons; i++)
        {
            colorIcon = new ImageButton(SettingsActivity.this);
            colorIcon.setBackgroundResource(R.drawable.color_icon);

            if (isRandom)
            {
                Random rand = new Random();
                chosenColor = colorsArray[rand.nextInt(6)];
            }

            switch (chosenColor)
            {
                case "blue":
                { colorIcon.setImageResource(R.drawable.blue); }    break;

                case "yellow":
                { colorIcon.setImageResource(R.drawable.yellow); }  break;

                case "green":
                { colorIcon.setImageResource(R.drawable.green); }   break;

                case "grey":
                { colorIcon.setImageResource(R.drawable.grey); }    break;

                case "red":
                { colorIcon.setImageResource(R.drawable.red); }     break;

                case "pink":
                { colorIcon.setImageResource(R.drawable.pink); }    break;
            }
            colorIcon.setClickable(false);
            colorIcon.setBackgroundColor(212121);
            colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            colorIconsGrid.addView(colorIcon, 100, 100);
        }
    }

    private void resetIcons()
    {
        shuffle.start();
        colorIconsGrid.removeAllViews();
        setEmptyIcon();
    }

    private void setEmptyIcon()
    {
        colorIcon = new ImageButton(SettingsActivity.this);
        colorIcon.setBackgroundResource(R.drawable.color_icon);
        colorIcon.setImageResource(R.drawable.empty);
        colorIcon.setClickable(false);
        colorIcon.setBackgroundColor(212121);
        colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        colorIconsGrid.addView(colorIcon, 100, 100);
        isEmptyButton = true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bgm.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bgm.start();
    }
}
