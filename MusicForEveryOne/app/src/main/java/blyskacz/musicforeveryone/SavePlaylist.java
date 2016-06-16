package blyskacz.musicforeveryone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Szymon on 2016-02-15.
 */
public class SavePlaylist extends Activity
{
    public Button save, cancel;
    public TextView name;
    public ArrayList<Integer> indexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if(b != null)
            indexes = b.getIntegerArrayList("playlist");
        setContentView(R.layout.save_playlist);
        save = (Button) findViewById(R.id.saveBtn);
        cancel = (Button) findViewById(R.id.cancelBtn);
        name = (TextView) findViewById(R.id.playlistName);

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Toast.makeText(getApplicationContext(), R.string.saveToast, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), SavedPlaylistList.class);
                i.putExtra("name", name.getText().toString());
                i.putExtra("playlist", indexes);
                setResult(100, i);
                startActivityForResult(i, 100);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent i = new Intent(getApplicationContext(), Playlist.class);
                startActivityForResult(i, 100);
            }
        });
    }
}
