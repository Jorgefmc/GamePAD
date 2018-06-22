package gamepad.pad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.util.Date;

public class OfferInspectActivity extends AppCompatActivity {

    private long userId;
    private GameListing game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_inspect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        userId = getIntent().getLongExtra("receiver", -1);
        game = (GameListing) getIntent().getSerializableExtra("game");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(game.getName());


        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(this.game.getUrl());

        Button rentButton = findViewById(R.id.inspect_rent_button);
        rentButton.setOnClickListener(new OnRentClickListener(userId, game));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

    }

    private class OnRentClickListener implements View.OnClickListener {

        private GameListing _game;
        private long _receiverId;

        public OnRentClickListener(long receiverId, GameListing game) {
            _game = game;
            _receiverId = receiverId;
        }

        @Override
        public void onClick(View v) {
            GameRenting renting = new GameRenting(-1, _game.getGameId(), _game.getName(), _game.getDesc(),
                    _game.getRenter(), _receiverId, _game.getPrice(), new Date(), null);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("renting", renting);
            returnIntent.putExtra("listing_id", _game.getId());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }
    }

}
