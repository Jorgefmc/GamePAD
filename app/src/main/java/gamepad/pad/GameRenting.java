package gamepad.pad;

import java.util.Date;

/**
 * Created by Ynscription on 22/06/2018.
 */

public class GameRenting {

    private long _rent_id;
    private long _game_id;
    private String _game_name;
    private String _game_desc;
    private long _giver_id;
    private long _receiver_id;
    private float _price_day;
    private Date _start_date;
    private Date _end_date;

    public long getRendId () {return _rent_id;}
    public long getGameId() {return _game_id;}
    public String getName() {return _game_name;}
    public String getDesc() {return _game_desc;}
    public long getGiver () {return  _giver_id;}
    public long getReceiver () {return  _receiver_id;}
    public float getPrice () {return _price_day;}
    public Date getStartDate() {return _start_date;}
    public Date getEndDate () {return _end_date;}



    public GameRenting (long id, long gameId, String gameName, String gameDesc, long giverId, long receiverId,
                        float pricePerDay, Date startDate, Date endDate) {
        _rent_id = id;
        _game_id = gameId;
        _game_name = gameName;
        _game_desc = gameDesc;
        _giver_id = giverId;
        _receiver_id = receiverId;
        _price_day = pricePerDay;
        _start_date = startDate;
        _end_date = endDate;
    }
}
