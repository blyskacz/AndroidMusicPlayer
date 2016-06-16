package blyskacz.musicforeveryone;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Szymon on 2016-02-15.
 */
public class SavedPlaylistList extends ListActivity
{
    public ArrayList<Integer> indexes = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> playlistBase = new ArrayList<>();
    public ArrayList<HashMap<String, String>> Base = new ArrayList<>();
    public ArrayList<String> nameBase = new ArrayList<>();
    public String name;
    public TinyDB tinyDBplaylist;
    public TinyDB tinyDBname;
    SQLiteHelper sql;
    public static String strSeparator = "__,__";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_playlist_list);
        sql = new SQLiteHelper(getApplicationContext());
        tinyDBplaylist = new TinyDB(getApplicationContext());
        tinyDBname = new TinyDB(getApplicationContext());
        nameBase = tinyDBname.getListString("name");
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b != null)
        {
            name = b.getString("name");
            indexes = b.getIntegerArrayList("playlist");
            nameBase.add(name);
            tinyDBname.putListString("name", nameBase);
            sql.savaDataInDatabase(name, indexes);
        }
        for (int i=0; i<nameBase.size(); i++)
        {
            HashMap<String, String> playlist = new HashMap<String, String>();
            playlist.put("name", nameBase.get(i));
            Base.add(playlist);
        }

       final ListAdapter adapter = new SimpleAdapter(this, Base, R.layout.activity_playlist_item, new String[] { "name" }, new int[]
                {
                        R.id.songTitle
                });
        setListAdapter(adapter);
        // wybranie pojedynczego listview
        final ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currname = nameBase.get(position);
                Cursor cursor = sql.getCursor();
                cursor.moveToFirst();
                while(true)
                {
                    if(((cursor.getString(cursor.getColumnIndex("name"))).toString()).equals(currname))
                        break;
                    else
                        cursor.moveToNext();
                }
                String strIndex = (cursor.getString(cursor.getColumnIndex("playlist"))).toString();
                Intent i = new Intent(getApplicationContext(),Playlist.class);
                i.putExtra("playlist", strIndex);
                startActivityForResult(i, 100);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                sql.deleteDataFromDatabase(nameBase.get(position));
                Base.remove(position);
                nameBase.remove(position);
                tinyDBname.putListString("name", nameBase);
                Intent i = new Intent(getApplicationContext(), SavedPlaylistList.class);
                startActivityForResult(i, 100);
                Toast.makeText(view.getContext(), R.string.deleteToast, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}
