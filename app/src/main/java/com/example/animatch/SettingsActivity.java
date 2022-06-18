package com.example.animatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity
{
    private final String[]      colorsArray     = { "blue", "brown", "dark_blue", "dark_green", "green",
                                                    "grey", "light_grey", "pink", "red", "yellow" };
    private EditText            numOfObjects;
    private Spinner             colorPicker;
    private ScrollView          scrollView;
    private GridLayout          colorIconsGrid;
    private ImageButton         colorIcon;

    private boolean             isEmptyButton   = true;
    private int                 colorIconCount  = 0;

    private MediaPlayer         bgm, flip, shuffle;

    private float               mAccel;
    private float               mAccelCurrent;
    private float               mAccelLast;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffectShort = VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE);
        VibrationEffect vibrationEffectLong  = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE);
        

        bgm     = MediaPlayer.create(SettingsActivity.this, R.raw.settings_bgm);
        flip    = MediaPlayer.create(SettingsActivity.this, R.raw.flip);
        shuffle = MediaPlayer.create(SettingsActivity.this, R.raw.shuffle);
        bgm.setLooping(true);
        bgm.start();

        /* Hide action bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        numOfObjects = findViewById(R.id.numOfObjects);
        numOfObjects.setText("1");

        ConstraintLayout secretsLayout = findViewById(R.id.secretsLayout);
        secretsLayout.setOnTouchListener((v, event) ->
        {
            hideKeyboardAndClearFocus();
            return true;
        });

        colorIconsGrid = new GridLayout(this);
        colorIconsGrid.setId(R.id.colorIconsGrid);
        colorIconsGrid.setColumnCount(6);
        setEmptyIcon();

        scrollView = findViewById(R.id.scrollView);
        scrollView.addView(colorIconsGrid);

        colorPicker = findViewById(R.id.colorOfObjects);
        colorPicker.setSelection(0);

        Button create = findViewById(R.id.create);
        create.setOnClickListener(v ->
        {
            addColorIconsByNumberAndColor(Integer.parseInt(numOfObjects.getText().toString()), false);
            vib.vibrate(vibrationEffectShort);
        });

        Button random = findViewById(R.id.random);
        random.setOnClickListener(v ->
        {
            addColorIconsByNumberAndColor(Integer.parseInt(numOfObjects.getText().toString()), true);
            vib.vibrate(vibrationEffectShort);
        });

        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(v ->
        {
            resetIcons();
            hideKeyboardAndClearFocus();
            vib.vibrate(vibrationEffectShort);
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v ->
        {
            onBackPressed();
            vib.vibrate(vibrationEffectShort);
        });

        /* Accelerometer logic */
        // Reset on shake
        SensorEventListener mSensorListener = new SensorEventListener()
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
                    vib.vibrate(vibrationEffectLong);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
            }
        };
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast    = SensorManager.GRAVITY_EARTH;
    }

    private void addColorIconsByNumberAndColor(int numOfColorIcons, boolean isRandom)
    {
        hideKeyboardAndClearFocus();
        flip.start();

        colorIconCount += numOfColorIcons;
        if (colorIconCount > 25000)
        {
            resetIcons();
            colorIconCount = 0;
            Toast.makeText(getApplicationContext(), R.string.crash, Toast.LENGTH_SHORT).show();
            return;
        }

        if (numOfColorIcons > 9999)
        {
            Toast.makeText(getApplicationContext(), R.string.crash, Toast.LENGTH_SHORT).show();
            return;
        }
        String chosenColor = colorPicker.getSelectedItem().toString();

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
                chosenColor = colorsArray[rand.nextInt(10)];
            }

            switch (chosenColor)
            {
                case "blue":
                { colorIcon.setImageResource(R.drawable.blue); }        break;

                case "brown":
                { colorIcon.setImageResource(R.drawable.brown); }       break;

                case "dark_blue":
                { colorIcon.setImageResource(R.drawable.dark_blue); }   break;

                case "dark_green":
                { colorIcon.setImageResource(R.drawable.dark_green); }  break;

                case "yellow":
                { colorIcon.setImageResource(R.drawable.yellow); }      break;

                case "green":
                { colorIcon.setImageResource(R.drawable.green); }       break;

                case "grey":
                { colorIcon.setImageResource(R.drawable.grey); }        break;

                case "light_grey":
                { colorIcon.setImageResource(R.drawable.light_grey); }  break;

                case "red":
                { colorIcon.setImageResource(R.drawable.red); }         break;

                case "pink":
                { colorIcon.setImageResource(R.drawable.pink); }        break;
            }
            colorIcon.setClickable(false);
            colorIcon.setBackgroundColor(212121);
            colorIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            colorIconsGrid.addView(colorIcon, 100, 100);
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
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

    private void hideKeyboardAndClearFocus()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (numOfObjects.isFocused())
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        numOfObjects.clearFocus();
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
