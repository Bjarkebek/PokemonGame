package com.example.pokemongame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    List<Card> cards = new ArrayList<>();
    int Score;
}
