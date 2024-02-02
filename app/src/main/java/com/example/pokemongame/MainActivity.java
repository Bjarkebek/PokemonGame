package com.example.pokemongame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;

import com.android.volley.RequestQueue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vælger hvor mange kort der skal spilles med
        NumberPicker cardsPicker = findViewById(R.id.np_cards);
        cardsPicker.setMinValue(3);
        cardsPicker.setMaxValue(20);
        cardsPicker.setValue(5);


        GameSetup gameSetup = new GameSetup(this); // laver ny instans af gamesetup

        Button startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(view -> {
            startButton.setText("Loading");
            startButton.setEnabled(false);
            gameSetup.numPlayers = 2; // sætter spillere fast til 2
            gameSetup.numCards = cardsPicker.getValue(); // tager value fra det valgte antal kort
            gameSetup.start(); // kører setup i GameSetup.java
        });
    }
}