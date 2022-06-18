package com.example.animatch;

import android.os.Bundle;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends GameActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Init animals array as list */
        List<Integer> animals = new ArrayList<>();
        for (int i : animalsArray)
        {
            animals.add(i);
        }

        ImageButton mainMenuIcon = findViewById(R.id.imageButtonMainMenu);
        Random randomNumber = new Random(animals.size());
        mainMenuIcon.setImageResource(animals.get(randomNumber.nextInt()));
    }
}