package com.example.pokemongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static RequestQueue requestQueue;
    static Card card;
    List<Player> players;
    Button startButton;
    ImageButton nextButton;
    TextView tv_p1;
    TextView tv_p2;
    TextView result;
    boolean gameStarted = false;
    int turn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        requestQueue = Volley.newRequestQueue(this);
        initGui();

        players = (List<Player>) getIntent().getSerializableExtra("Players");
    }

    private void initGui() {
        startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);
        nextButton = findViewById(R.id.btn_next);
        nextButton.setOnClickListener(this);
        tv_p1 = findViewById(R.id.txtP1);
        tv_p2 = findViewById(R.id.txtP2);
        result = findViewById(R.id.result);
    }

    @Override
    public void onClick(View view) {
        result.setText("");

        // skifter knappen efter første tryk
        if (!gameStarted) {
            startButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            tv_p1.setVisibility(View.VISIBLE);
            tv_p2.setVisibility(View.VISIBLE);
            gameStarted = true;
        }


        // loop køres så længe spillere stadig har kort
        if (turn != players.get(0).cards.size()) {

            // player 1 card
            getCard(players.get(0).cards.get(turn).id, players.get(0));

            // player 2 card
            getCard(players.get(1).cards.get(turn).id, players.get(1));

            // Finder hp for kortet og sætter ind i Integer (da hp kan være null)
            Integer p1 = players.get(0).cards.get(turn).hp;
            Integer p2 = players.get(1).cards.get(turn).hp;


            // statements til at vælge hvem der vinder runden og får point
            if (p1 > p2) {
                result.setText("P1 WIN");
                players.get(0).Score += 1;
            } else if (p1 < p2) {
                result.setText("P2 WIN");
                players.get(1).Score += 1;
            } else {
                result.setText("DRAW");
            }


            Toast.makeText(this, "Round: " + (turn + 1), Toast.LENGTH_LONG).show();
            turn++;

        } else { // efter sidste runde ses resultatet
            if (players.get(0).Score > players.get(1).Score) {
                result.setText("WINNER IS \n\n Player One \n WITH " + players.get(0).Score + " POINTS");
            } else if (players.get(0).Score < players.get(1).Score) {
                result.setText("WINNER IS \n\n Player Two \n WITH " + players.get(1).Score + " POINTS");
            } else{
                result.setText("DRAW \n YOU BOTH GOT" + players.get(0).Score + " POINTS");
            }

            Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
            gameStarted = false;
        }
    }

    public void getCard(String id, Player player) { // unødvendigt ????
        String url = "https://api.tcgdex.net/v2/en/cards/" + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            card = new Gson().fromJson(response, Card.class);
            getImage(player); // eneste vigtige ???
        }, error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        );
        requestQueue.add(request);
    }

    private void getImage(Player player) {

        // viser spiller 1 kort
        if (players.get(0) == player) {
            ImageView cardImage = findViewById(R.id.img_playerCard);
            Picasso.get().load(card.image + "/high.jpg").into(cardImage);
        }
        // viser spiller 2 kort
        if (players.get(1) == player) {
            ImageView cardImage = findViewById(R.id.img_opponentCard);
            Picasso.get().load(card.image + "/high.jpg").into(cardImage);
        }
    }
}