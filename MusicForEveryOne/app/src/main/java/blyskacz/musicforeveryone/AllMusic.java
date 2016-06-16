package blyskacz.musicforeveryone;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Created by Szymon on 2015-11-17.
 */
public class AllMusic extends ListActivity
{
    // Songs list
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
        SongsManager plm = new SongsManager();
        // wez wsystkie z karty
        this.songsList = plm.getPlayList();
        for (int i = 0; i < songsList.size(); i++)
        {
            HashMap<String, String> song = songsList.get(i);
            songsListData.add(song);
        }
        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData, R.layout.activity_all_music_item, new String[] { "songTitle" }, new int[]
                {
                    R.id.songTitle
                });
        setListAdapter(adapter);
        // wybranie pojedynczego listview
        final ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // getting listitem index
                int songIndex = position;
                // Starting new intent
                Intent in = new Intent(getApplicationContext(), musicPlayer.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });

        // obsluga dlugiego kliknieca, dodawanie do listy + toast z komunikatem
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = lv.getItemAtPosition(position).toString();
                int songIndex = position;
                //indexes.add(songIndex);
                Toast.makeText(view.getContext(), R.string.songAdd, Toast.LENGTH_LONG).show();
                Intent in = new Intent(getApplicationContext(), Playlist.class);
                //in.putExtra("songIndexes", indexes);
                in.putExtra("index", songIndex);
                setResult(100, in);
                startActivity(in);
                return false;
            }
        });
    }
}

