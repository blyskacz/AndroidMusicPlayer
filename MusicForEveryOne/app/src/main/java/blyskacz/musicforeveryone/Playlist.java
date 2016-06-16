package blyskacz.musicforeveryone;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Szymon on 2015-12-22.
 */
public class Playlist extends ListActivity
{
    // Songs list
    public ArrayList<HashMap<String, String>> songList = new ArrayList<HashMap<String, String>>();
    public ArrayList<Integer> indexes = new ArrayList<>();
    public ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
    public TinyDB tinyDB;
    public SongsManager plm;
    public static String strSeparator = "__,__";

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putIntegerArrayList("indexes", indexes);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tinyDB = new TinyDB(getApplicationContext());
        indexes = tinyDB.getListInt("indexesList");
        setContentView(R.layout.activity_playlist);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(savedInstanceState != null)
            indexes = (ArrayList) savedInstanceState.get("indexes");
        if(b != null)
        {
            int newIndex = (Integer) b.getInt("index");
            indexes.add(newIndex);
            String playlist = (String) b.getString("playlist");
            if(playlist != null)
            {
                indexes=convertStringToArray(playlist);
                tinyDB.putListInt("indexesList", indexes);

            }
            tinyDB.putListInt("indexesList", indexes);
        }
        plm = new SongsManager();
        // wez wsystkie z karty
        this.songList = plm.getPlayList();
        for(int i=0; i<indexes.size(); i++)
        {
            for(int j=0; j<songList.size(); j++)
            {
                if((songList.get(j).get("songPath")).toString().equals((songList.get(indexes.get(i)).get("songPath")).toString()))
                {
                    HashMap<String, String> song = songList.get(j);
                    songsListData.add(song);
                }
            }
        }

        final ListAdapter adapter = new SimpleAdapter(this, songsListData, R.layout.activity_playlist_item, new String[] { "songTitle" }, new int[]
                {
                        R.id.songTitle
                });
        setListAdapter(adapter);
        // wybranie pojedynczego listview
        final ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting listitem index
               int songIndex = position;
                for (int j = 0; j < songsListData.size(); j++)
                {
                    if (songsListData.get(j).toString().equals(lv.getItemAtPosition(position).toString()))
                        songIndex = j;
                }

                // Starting new intent
                Intent in = new Intent(view.getContext(), musicPlayer.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", songIndex);
                in.putExtra("indexes", indexes);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });
    }

    public static ArrayList convertStringToArray(String str)
    {
        String[] arr = str.split(strSeparator);
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i=0; i<arr.length; i++)
            integerArrayList.add(Integer.parseInt(arr[i]));
        return integerArrayList;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.playlist_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.cleanPlaylist:
                tinyDB.clear();
                Toast.makeText(getApplicationContext(), R.string.songAdd, Toast.LENGTH_LONG).show();
                finish();
                return true;
            case R.id.allMusicActInPl:
                Intent i = new Intent(getApplicationContext(), AllMusic.class);
                startActivityForResult(i, 100);
                return true;
            case R.id.savePlaylist:
                Intent in = new Intent(getApplicationContext(), SavePlaylist.class);
                in.putExtra("playlist", indexes);
                setResult(100, in);
                startActivityForResult(in, 100);
                return true;
            case R.id.loadPlaylist:
                Intent inn = new Intent(getApplicationContext(), SavedPlaylistList.class);
                startActivityForResult(inn, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
