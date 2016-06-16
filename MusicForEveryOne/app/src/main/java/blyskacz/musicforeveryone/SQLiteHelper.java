package blyskacz.musicforeveryone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Szymon on 2016-02-16.
 */
public class SQLiteHelper extends SQLiteOpenHelper implements BaseColumns
{

    public static String DATABASE_NAME = "playlists.db";
    public static final int DATABASE_VERSION = 1;
    public static final String PLAYLIST_TABLE = "TableOfPlaylist";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String NAME_COLUMN = "name";
    public static final String PLAYLIST_COLUMN = "playlist";
    private String CREATE_TABLE_SQL_QUERY = "create table " + PLAYLIST_TABLE + "(" + COLUMN_ID +  " integer primary key autoincrement," +
            NAME_COLUMN + " text not null, " + PLAYLIST_COLUMN + " text not null);";
    public static String strSeparator = "__,__";

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_SQL_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion != newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE);
            onCreate(db);
        }
    }

    public static String convertArrayToString(ArrayList<Integer> arrayList)
    {
        String str = "";
        for (int i = 0;i<arrayList.size(); i++)
        {
            str = str+arrayList.get(i).toString();
            // Do not append comma at the end of last element
            if(i<arrayList.size()-1)
                str = str+strSeparator;
        }
        return str;
    }

    public void savaDataInDatabase(String name, ArrayList<Integer> playlist)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try
        {
            String playlistStr = convertArrayToString(playlist);
            ContentValues newItem = new ContentValues();
            newItem.put(NAME_COLUMN, name);
            newItem.put(PLAYLIST_COLUMN, playlistStr);
            db.insert(PLAYLIST_TABLE, null, newItem);
            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            Log.d("Database", "Error");
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void deleteDataFromDatabase(String name)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try
        {
            String selection = NAME_COLUMN + " = ";
            String [] selectionArg = {name};
            db.delete(PLAYLIST_TABLE, selection, selectionArg);
        }
        catch(Exception e)
        {
            Log.d("Database", "Error");
        }
        finally
        {
            db.endTransaction();
        }
    }

    public Cursor getCursor()
    {
        String [] projection = {NAME_COLUMN, PLAYLIST_COLUMN};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PLAYLIST_TABLE, projection, null, null, null, null, null);
        return cursor;
    }
}

