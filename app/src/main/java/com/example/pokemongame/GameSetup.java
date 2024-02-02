package com.example.pokemongame;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSetup extends Thread {

    public int numCards;
    public int numPlayers;
    private final RequestQueue requestQueue;
    private final Activity act;
    private final List<Player> players = new ArrayList<>();
    private List<DeckCard> fullDeck;

    public GameSetup(Activity activity) {
        act = activity;
        requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
        getAllCards();
    }

    // Henter alle kort fra API og sætter dem ind i 'fullDeck' af 'List<DeckCard>'
    private void getAllCards() {
        String url = "https://api.tcgdex.net/v2/en/cards";
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            fullDeck = new Gson().fromJson(response, new TypeToken<List<DeckCard>>() {
            }.getType());
            Toast.makeText(act, "Total Cards: " + fullDeck.size(), Toast.LENGTH_LONG).show();
        }, error -> Log.d("Volley", error.toString()));
        requestQueue.add(request);
    }

    @Override
    public void run() {
        super.run();
        initPlayers();
    }



    private void initPlayers() {
            // Opretter alle (2) spillere og giver dem kort
        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player();
            addDeckToPlayer(p);
            players.add(p);
        }

            // Venter til alle spillere har fået kort, til at starte spillet
        boolean isFinish = false;
        while (!isFinish) {
            isFinish = true;
            for (Player p : players) {
                if (p.cards.size() != numCards) {
                    isFinish = false;
                    break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        StartGame();
    }

        // for loop til at finde random kort og køre addCard() med kortet
    private void addDeckToPlayer(Player player) {
        Random rnd = new Random();
        for (int i = 0; i < numCards; i++) {
            int numOnList = rnd.nextInt(fullDeck.size()); // finder et random tal
            DeckCard card = fullDeck.get(numOnList); // med numOnList som er random, søges et kort blandt alle kort
            addCard(card.id, player);
        }
    }

        // henter kort-info og tilføjer kortet til spilleren
    private void addCard(String id, Player player) {
        String url = "https://api.tcgdex.net/v2/en/cards/" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Card card = new Gson().fromJson(response, Card.class);
            player.cards.add(card); // kort tilføjes
        }, error -> Log.d("Volley", error.toString()));
        requestQueue.add(request);
    }

        // starter spillet (på en anden thread??)
    private void StartGame() {
        act.runOnUiThread(() -> Toast.makeText(act, "Get Ready!", Toast.LENGTH_LONG).show());

        act.startActivity(new Intent(act, GameActivity.class)
                .putExtra("Players", (Serializable) players));
    }
}