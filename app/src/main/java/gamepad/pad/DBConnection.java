package gamepad.pad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ynscription on 19/06/2018.
 */

public class DBConnection extends SQLiteOpenHelper{

    private static final int _DATABASE_VERSION = 1;
    private static final String _DATABASE_NAME = "pad_db";
    private static DBConnection instance= null;
    public static final long DEFAULT_USER = 0;
    private SQLiteDatabase _db;
    private SimpleDateFormat _df;

    //Thread safe singleton
    public static DBConnection db (Context context) {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null)
                    instance = new DBConnection(context);
            }
        }
        return instance;
    }


    private DBConnection(Context context) {
        super(context, _DATABASE_NAME, null, _DATABASE_VERSION);
        _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createAll(db);
        addDefault(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO transfer db isntead of deleting it
        killAll(db);
        onCreate (db);
    }

    public void resetDB () {
        SQLiteDatabase db = this.getWritableDatabase();
        killAll(db);
        createAll(db);
        //TODO default user must not make it to the release
        addDefault(db);
    }

    private void killAll (SQLiteDatabase db) {
        db.execSQL ("DROP TABLE IF EXISTS Users;");
        db.execSQL ("DROP TABLE IF EXISTS Games;");
        db.execSQL ("DROP TABLE IF EXISTS Listings;");
        db.execSQL ("DROP TABLE IF EXISTS ActiveRents;");
        db.execSQL ("DROP TABLE IF EXISTS CompletedRents;");
        db.execSQL("VACUUM");
    }

    private void createAll (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "username Text, " +
                "pw TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Games(" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "url TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Listings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "game_id INTEGER, " +
                "price_day REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS ActiveRents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "giver_id INTEGER, " +
                "receiver_id INTEGER, " +
                "game_id INTEGER, " +
                "start_date DATETIME, " +
                "price_day REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS CompletedRents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "giver_id INTEGER, " +
                "receiver_id INTEGER, " +
                "game_id INTEGER, " +
                "start_date DATETIME, " +
                "end_date DATETIME, " +
                "price_total REAL);");

    }

    private void addDefault (SQLiteDatabase db) {
        Cursor cursor = db.query("Users", new String[]{"id"},
                "id=?", new String[]{"" + DEFAULT_USER},
                null, null, null);

        ContentValues values = new ContentValues();
        values.put("id", DEFAULT_USER);
        values.put ("email", "user@test.com");
        values.put ("username", "User");
        values.put ("pw", "12345");

        if (cursor.moveToFirst())
            db.update("Users", values, "id=?", new String[]{"" + DEFAULT_USER});
        else
            db.insert("Users", null, values);

        cursor.close();
        //db.close();
    }

    public long createUser (String email, String name, String pw) throws LoginException {
        SQLiteDatabase db = this.getWritableDatabase();

        String [] args = {email, name};

        //Error si email o username ya existe
        Cursor cursor = db.query("Users", new String[]{"id"},
                "email=? OR username=?", args,
                null, null, null);

        if (cursor.moveToFirst()) {
            boolean qemail = false;
            boolean qname = false;
            do {
                qemail = email.equals(cursor.getString(cursor.getColumnIndex("email")));
                qname = name.equals(cursor.getString(cursor.getColumnIndex("username")));
            }while(cursor.moveToNext() && !qemail && !qname);
            cursor.close();
            //db.close();
            if (qemail) {
                LoginException e = new LoginException("Email is already in use", LoginException.USED_EMAIL);
                throw e;
            }
            else {
                LoginException e = new LoginException("Username is already in use.", LoginException.USED_NAME);
                throw e;
            }
        }
        cursor.close();


        ContentValues values = new ContentValues();
        values.put ("email", email);
        values.put ("username", name);
        values.put ("pw", pw);

        long id = db.insert("Users", null, values);

        //db.close();

        return id;
    }

    public long loginUser (String email, String pw) throws LoginException {
        long res = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        String[] args = {email, pw};

        Cursor cursor = db.query("Users", new String[]{"id"},
                "email=? AND pw=?", args,
                null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() == 1) {
            res = cursor.getLong(cursor.getColumnIndex("id"));
        }
        else {
            LoginException e = new LoginException("Incorrect login.", LoginException.INCORRECT_LOGIN);
            throw e;
        }

        cursor.close();
        //db.close();
        return res;
    }

    public String getUserName (long user_id) {
        String res = null;
        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query("Users", new String[]{"username"},
                "id=? ", new String[] {"" + user_id},
                null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() == 1) {
            res = cursor.getString(cursor.getColumnIndex("username"));
        }

        cursor.close();
        //db.close();
        return res;
    }

    public String getUserEmail (long user_id) {
        String res = null;
        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query("Users", new String[]{"email"},
                "id=? ", new String[] {"" + user_id},
                null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() == 1) {
            res = cursor.getString(cursor.getColumnIndex("email"));
        }

        cursor.close();
        //db.close();
        return res;
    }

    public void addListing(GameListing gameListing) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("Games", new String[]{"id"},
                "id=? ", new String[] {"" + gameListing.getId()},
                null, null, null);



        if (!cursor.moveToFirst()){
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("id", gameListing.getId());
            values.put ("name", gameListing.getName());
            values.put ("description", gameListing.getDesc());
            values.put ("url", gameListing.getUrl());

            db.insert("Games", null, values);

        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put ("user_id", gameListing.getRenter());
        values.put ("game_id", gameListing.getId());
        values.put ("price_day", gameListing.getPrice());

        db.insert("Listings", null, values);

    }



    public GameListing[] getExclusiveGameListings(long userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("Listings", null,
                "user_id<>?", new String []{"" + userID},
                null, null, null);

        GameListing [] res = null;
        try {
            if (cursor.moveToFirst()) {
                res = new GameListing[cursor.getCount()];
                int pos = 0;
                do {
                    long game_id = cursor.getLong(cursor.getColumnIndex("game_id"));
                    long user_id = cursor.getLong(cursor.getColumnIndex("user_id"));
                    float price_day = cursor.getFloat(cursor.getColumnIndex("price_day"));
                    Cursor gameCursor = db.query("Games", new String[]{"name", "description", "url"},
                            "id=? ", new String[]{"" + game_id},
                            null, null, null);
                    if (gameCursor.moveToFirst() && gameCursor.getCount() == 1) {
                        String gameName = gameCursor.getString(gameCursor.getColumnIndex("name"));
                        String gameDesc = gameCursor.getString(gameCursor.getColumnIndex("description"));
                        String gameURL = gameCursor.getString(gameCursor.getColumnIndex("url"));
                        res[pos] = new GameListing(game_id, gameName, gameDesc, gameURL, user_id, price_day);
                    }
                    gameCursor.close();
                    pos++;
                } while (cursor.moveToNext());
            }
        }catch (SQLiteException e) {res = null;}
        cursor.close();

        return res;
    }

    public GameListing[] getGameListingsFromUser(long userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("Listings", null,
                "user_id=?", new String []{"" + userID},
                null, null, null);



        GameListing [] res = null;
        if (cursor.moveToFirst()) {
            res = new GameListing[cursor.getCount()];
            int pos = 0;
            do {
                long game_id = cursor.getLong(cursor.getColumnIndex("game_id"));
                long user_id = cursor.getLong(cursor.getColumnIndex("user_id"));
                float price_day = cursor.getFloat(cursor.getColumnIndex("price_day"));
                Cursor gameCursor = db.query("Games", new String[]{"name", "description", "url"},
                        "id=? ", new String[] {"" + game_id},
                        null, null, null);
                if (gameCursor.moveToFirst() && gameCursor.getCount() == 1) {
                    String gameName = gameCursor.getString(gameCursor.getColumnIndex("name"));
                    String gameDesc = gameCursor.getString(gameCursor.getColumnIndex("description"));
                    String gameURL = gameCursor.getString(gameCursor.getColumnIndex("url"));
                    res[pos] = new GameListing(game_id, gameName, gameDesc, gameURL, user_id, price_day);
                }
                gameCursor.close();
                pos++;
            }while(cursor.moveToNext());
        }
        cursor.close();

        return res;
    }

    public long addRenting (GameRenting game) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put ("giver_id", game.getGiver());
        values.put ("receiver_id", game.getReceiver());
        values.put ("game_id", game.getGameId());
        values.put("start_date", _df.format(game.getStartDate()));
        values.put ("price_day", game.getPrice());

        long id = db.insert("ActiveRents", null, values);

        //db.close();

        return id;

    }

    public long finishRenting (long id) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor activeCursor = db.query("ActiveRents", new String[]{"id"},
                "id=?", new String []{"" + id},
                null, null, null);

        if (activeCursor.moveToFirst() && activeCursor.getCount() == 1) {

            long giver = activeCursor.getLong(activeCursor.getColumnIndex("giver_id"));
            long receiver = activeCursor.getLong(activeCursor.getColumnIndex("receiver_id"));
            long gameId = activeCursor.getLong(activeCursor.getColumnIndex("game_id"));
            String startDate = activeCursor.getString(activeCursor.getColumnIndex("start_date"));
            float price = activeCursor.getFloat(activeCursor.getColumnIndex("price_day"));


            int res = db.delete("ActiveRents", "id=?", new String[]{"" + id});
            if (res != 1) {
                //TODO This should throw an exception
            }

            ContentValues values = new ContentValues();
            values.put ("giver_id", giver);
            values.put ("receiver_id", receiver);
            values.put ("game_id", gameId);
            values.put("start_date", startDate);
            values.put("end_date", _df.format(new Date()));
            values.put ("price_day", price);

            long newId = db.insert("CompletedRents", null, values);
            activeCursor.close();

            return newId;
        }
        else {
            activeCursor.close();
            return -1;
        }

    }

    public GameRenting[] getOnRentByUser(long userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("ActiveRents", null,
                "giver_id=?", new String []{"" + userID},
                null, null, null);



        return null;
    }

    public GameListing[] getReceivedByUser(long userID) {

        return null;
    }
}
