package gamepad.pad;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by Ynscription on 20/06/2018.
 */

public class GameListing implements Serializable{

    private long _game_id;
    private String _game_name;
    private String _game_desc;
    private long _renter;
    private float _price_day;

    public long getId() {
        return _game_id;
    }

    public String getName() {
        return _game_name;
    }

    public String getDesc() {
        return _game_desc;
    }

    public long getRenter() {
        return _renter;
    }


    public float getPrice() {
        return _price_day;
    }

    public GameListing (long id, String gameName, String gameDesc, long renter, float pricePerDay) {
        _game_id = id;
        _game_name = gameName;
        _game_desc = gameDesc;
        _renter = renter;
        _price_day = pricePerDay;
    }
}
