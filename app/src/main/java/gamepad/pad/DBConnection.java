package gamepad.pad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ynscription on 19/06/2018.
 */

public class DBConnection extends SQLiteOpenHelper{

    private static final int _DATABASE_VERSION = 1;
    private static final String _DATABASE_NAME = "pad_db";
    private static DBConnection instance= null;

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
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createAll(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO transfer db isntead of deleting it
        killAll(db);
        onCreate (db);
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
            db.close();
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

        db.close();

        return id;
    }

    public void resetDB () {
        SQLiteDatabase db = this.getWritableDatabase();
        killAll(db);
        createAll(db);
    }

    private void killAll (SQLiteDatabase db) {
        db.execSQL ("DROP TABLE IF EXISTS Users; " +
                "DROP TABLE IF EXISTS Games; " +
                "DROP TABLE IF EXISTS Listings; " +
                "DROP TABLE IF EXISTS ActiveRents; " +
                "DROP TABLE IF EXISTS CompletedRents; " +
                "VACUUM");
    }

    private void createAll (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "username Text, " +
                "pw TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Games(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Listings (" +
                "user_id INTEGER, " +
                "game_id INTEGER, " +
                "date DATETIME, " +
                "price_day REAL, " +
                "location BLOB, " +
                "PRIMARY KEY (user_id, game_id, date));");

        db.execSQL("CREATE TABLE IF NOT EXISTS ActiveRents (" +
                "giver_id INTEGER, " +
                "receiver_id INTEGER, " +
                "game_id INTEGER, " +
                "start_date DATETIME, " +
                "end_date DATETIME, " +
                "price_day REAL, " +
                "location BLOB, " +
                "PRIMARY KEY (giver_id, receiver_id, game_id, start_date));");

        db.execSQL("CREATE TABLE IF NOT EXISTS CompletedRents (" +
                "giver_id INTEGER, " +
                "receiver_id INTEGER, " +
                "game_id INTEGER, " +
                "start_date DATETIME, " +
                "end_date DATETIME, " +
                "price_total REAL, " +
                "PRIMARY KEY (giver_id, receiver_id, game_id, start_date));");
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
        db.close();
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
        db.close();
        return res;
    }

}
