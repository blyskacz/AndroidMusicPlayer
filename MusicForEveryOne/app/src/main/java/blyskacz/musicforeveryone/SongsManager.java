package blyskacz.musicforeveryone;

/**
 * Created by Szymon on 2015-11-13.
 */

import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager
{
    // SDCard Path
    final String MEDIA_PATH = new String("/sdcard/Music");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    String songTitle;
    ImageView coverart;
    // Constructor
    public SongsManager()
    {

    }

   // czytanie piosenek i zapis na playliste
    public ArrayList<HashMap<String, String>> getPlayList()
    {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        File home = new File(MEDIA_PATH);
        if (home.listFiles(new FileExtensionFilter()).length > 0)
        {
            for (File file : home.listFiles(new FileExtensionFilter()))
            {
                mmr.setDataSource(file.getPath());
                if((mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null) && (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null))
                    songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + " - " + mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ;
                else
                    songTitle = file.getName().substring(0, (file.getName().length() - 4));
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", songTitle);
                song.put("songPath", file.getPath());
                // dodanie do playlisty
                songsList.add(song);
            }
        }
        return songsList;
    }

    // filtrowanie czy mp3
    class FileExtensionFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
