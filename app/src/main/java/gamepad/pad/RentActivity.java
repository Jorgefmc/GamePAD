package gamepad.pad;

import android.app.Activity;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Api;
import com.igdb.api_android_java.callback.onSuccessCallback;
import com.igdb.api_android_java.model.APIWrapper;
import com.igdb.api_android_java.model.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RentActivity extends AppCompatActivity {

    private long selectedGameID = -1;
    private String selectedGameName = null;
    private String selectedGameDesc = null;

    private GameSearcher searcher;
    private long user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Poner en Alquiler");

        //TODO: PARA VOLVER A LA ACTIVIDAD ANTERIOR UTILIZA EL SIGUIENTE CODIGO!!!!;
        Button save = (Button) findViewById(R.id.save_game);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRentClicked();
            }
        });

        /*Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/

        SearchView gameSearch = findViewById(R.id.rent_search_game);
        searcher = new GameSearcher(this);
        gameSearch.setOnQueryTextListener(searcher);
        gameSearch.setQueryHint("Buscar juego");
        gameSearch.requestFocus();

        selectedGameName = null;
        selectedGameDesc = null;

        user_id = getIntent().getLongExtra("id", -1);
    }

    private class GameSearcher implements SearchView.OnQueryTextListener {

        private RentActivity _parent;



        public GameSearcher (RentActivity parent) {
            _parent = parent;

        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            //TODO Search for game
            if (query.length() <= 0)
                return false;

            Parameters params = new Parameters();
            params.addSearch(query);
            params.addFields("id, name, summary");
            params.addLimit("1");


            APIWrapper gameDBWrapper = new APIWrapper(getApplicationContext(), "8025839066468e04dc7a1e84c6afba2b");
            gameDBWrapper.search(APIWrapper.Endpoint.GAMES, params, new onSuccessCallback() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    String name = null;
                    String desc = null;
                    long gameID = -1;

                    try {
                        JSONObject result = jsonArray.getJSONObject(0);
                        gameID = result.getLong("id");
                        name = result.getString("name");
                        desc = result.getString("summary");


                    } catch (JSONException e) {}

                    boolean res = gameID != -1;

                    _parent.searchCallback(res, gameID, name, desc);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    _parent.searchCallback(false, -1, null, null);
                }
            });

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //TODO populate suggestions
            return false;
        }
    }

    private void searchCallback(boolean success, long gameID, String name, String desc) {
        if (success) {
            selectedGameID = gameID;
            selectedGameName = name;
            selectedGameDesc = desc;
            ((TextView) findViewById(R.id.rent_game_result)).setText(name);
        }
        else {
            selectedGameID = -1;
            selectedGameName = null;
            selectedGameDesc = null;
            ((TextView) findViewById(R.id.rent_game_result)).setText("Juego no encontrado.");
        }

    }


    private void onRentClicked () {
        boolean cancel = false;
        View focusView = null;

        TextView price_day = (TextView) findViewById(R.id.rent_price_day);
        float price = -1;
        try {
            price = Float.parseFloat(price_day.getText().toString());
        }catch (NumberFormatException e) {
            cancel = true;
            focusView = price_day;
            price_day.setError("Debe ser un numero");
        }

        if (!cancel && price < 0) {
            cancel = true;
            focusView = price_day;
            price_day.setError("Debe ser positivo");
        }

        TextView search = findViewById(R.id.rent_game_result);
        if (selectedGameDesc == null || selectedGameName == null) {
            cancel = true;
            focusView = search;
            search.setError("Debes seleccionar un juego");
        }

        //TODO get location and if the location is valid, add it to the game


        GameListing gameListing = new GameListing(selectedGameID, selectedGameName, selectedGameDesc, user_id, price);


        Intent returnIntent = new Intent();
        returnIntent.putExtra("game", gameListing);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

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

}
