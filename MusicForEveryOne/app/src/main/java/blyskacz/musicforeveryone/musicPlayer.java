package blyskacz.musicforeveryone;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class musicPlayer extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener
{

        private ImageButton btnPlay;
        private ImageButton btnNext;
        private ImageButton btnPrevious;
        private ImageButton btnPlaylist;
        private ImageButton btnRepeat;
        private ImageButton btnShuffle;
        private ImageButton btnYouTube;
        private ImageButton btnLyrics;
        private SeekBar songProgressBar;
        private TextView songTitleLabel;
        private TextView songCurrentDurationLabel;
        private TextView songTotalDurationLabel;
        private ImageView cover;
        // Media Player
        static MediaPlayer mp;
        // Handler aktualizuje czas i progres bar
        private Handler mHandler = new Handler();
        private SongsManager songManager;
        private ConvertTime utils;
        private int currentSongIndex = 0;
        private boolean isShuffle = false;
        private boolean isRepeat = false;
        private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
        public String songTitle;
        public ArrayList<Integer> indexes = new ArrayList<>();
        private ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
        public TinyDB tinyDB;

    @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_music_player);
            tinyDB = new TinyDB(getApplicationContext());
            indexes = tinyDB.getListInt("indexesList");

            // przyciski
            btnPlay = (ImageButton) findViewById(R.id.btnPlay);
            btnNext = (ImageButton) findViewById(R.id.btnNext);
            btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
            btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
            btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
            btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
            btnLyrics = (ImageButton) findViewById(R.id.btnLyrics);
            songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
            songTitleLabel = (TextView) findViewById(R.id.songTitle);
            songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
            songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
            btnYouTube = (ImageButton) findViewById(R.id.btnYouTube);
            songTitle = "";
            cover = (ImageView) findViewById(R.id.cover);
            songManager = new SongsManager();
            songsList = songManager.getPlayList();
            // Mediaplayer
            mp = new MediaPlayer();
            utils = new ConvertTime();
            // Listeners
            songProgressBar.setOnSeekBarChangeListener(this); // Important
            mp.setOnCompletionListener(this); // Important
            // przycisk play, podmiana na pause
            btnPlay.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View arg0) {
                    // sprawdzenie czy gra
                    if(mp.isPlaying())
                    {
                        if(mp!=null)
                        {
                            mp.pause();
                            // zmiana na play
                            btnPlay.setImageResource(R.drawable.btn_play);
                        }
                    }
                    else
                    {
                        // resume
                        if(mp!=null)
                        {
                            mp.start();
                            // zmiana na pause
                            btnPlay.setImageResource(R.drawable.btn_pause);
                        }
                    }
                }
            });

            // przycisk nastepnej piosenki
            btnNext.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View arg0)
                {
                    // sprawdzenie jest kolejna piosenka
                    if(currentSongIndex < (songsListData.size() - 1))
                    {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    }
                    else
                    {
                        // w przeciwnym przypadku reverse i odpoczatku
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
            });

            // poprzednia piosenka
            btnPrevious.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    if(currentSongIndex > 0)
                    {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    }
                    else
                    {
                        // od nowa ta sama piosenka
                        playSong(songsListData.size() - 1);
                        currentSongIndex = songsListData.size() - 1;
                    }
                }
            });

            // repeat
            btnRepeat.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    if(isRepeat)
                    {
                        isRepeat = false;
                        Toast.makeText(getApplicationContext(), R.string.repeatOf, Toast.LENGTH_SHORT).show();
                        btnRepeat.setImageResource(R.drawable.btn_repeat);
                    }
                    else
                    {
                        // ustawanie repeat na true
                        isRepeat = true;
                        Toast.makeText(getApplicationContext(), R.string.repeatOn, Toast.LENGTH_SHORT).show();
                        // ustawienie shuffle na false
                        isShuffle = false;
                        btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                        btnShuffle.setImageResource(R.drawable.btn_shuffle);
                    }
                }
            });

            // przycisk shuffle
            btnShuffle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    if(isShuffle)
                    {
                        isShuffle = false;
                        Toast.makeText(getApplicationContext(), R.string.shuffleOF, Toast.LENGTH_SHORT).show();
                        btnShuffle.setImageResource(R.drawable.btn_shuffle);
                    }
                    else
                    {
                        // make repeat to true
                        isShuffle= true;
                        Toast.makeText(getApplicationContext(), R.string.shuffleON, Toast.LENGTH_SHORT).show();
                        // make shuffle to false
                        isRepeat = false;
                        btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                        btnRepeat.setImageResource(R.drawable.btn_repeat);
                    }
                }
            });

            //przycisk youtube
            btnYouTube.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.google.android.youtube");
                    if(songTitle.equals(""))
                        intent.putExtra("query", "android");
                    else
                        intent.putExtra("query", songTitle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(mp.isPlaying())
                    {
                        if(mp!=null)
                        {
                            mp.pause();
                            // zmiana na play
                            btnPlay.setImageResource(R.drawable.btn_play);
                        }
                    }
                    startActivity(intent);
                }
            });

            //przycisk lyrics
            btnLyrics.setOnClickListener(new View.OnClickListener()
            {
                String title;
                String artist;
                @Override
                public void onClick(View arg0)
                {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(songsListData.get(currentSongIndex).get("songPath"));
                    title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString();
                    artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST).toString();
                    title = title.replace(" ", "+");
                    artist = artist.replace(" ", "+");
                    String url;
                    if(title.equals("") && artist.equals(""))
                        url = "http://www.tekstowo.pl/szukaj.html";
                    else
                        url = "http://www.tekstowo.pl/szukaj,wykonawca,"+artist+",tytul,"+title+".html";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    if(mp.isPlaying())
                    {
                        if(mp!=null)
                        {
                            mp.pause();
                            // zmiana na play
                            btnPlay.setImageResource(R.drawable.btn_play);
                        }
                    }
                }
            });

            // przycisk playlist
            btnPlaylist.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    Intent i = new Intent(getApplicationContext(), Playlist.class);
                    startActivityForResult(i, 100);
                }
            });
            songsListData.clear();
            for(int i=0; i<indexes.size(); i++)
            {
                for(int j=0; j<songsList.size(); j++)
                {
                    if((songsList.get(j).get("songPath")).toString().equals((songsList.get(indexes.get(i)).get("songPath")).toString()))
                    {
                        HashMap<String, String> song = songsList.get(j);
                        songsListData.add(song);
                    }
                }
            }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_music_player, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.allMusicAct:
                Intent i = new Intent(getApplicationContext(), AllMusic.class);
                startActivityForResult(i, 100);
                return true;
            case R.id.loadPlaylist:
                mp.stop();
                Intent inn = new Intent(getApplicationContext(), SavedPlaylistList.class);
                startActivityForResult(inn, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // odtwarzanie
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100)
        {
            currentSongIndex = data.getExtras().getInt("songIndex");
            indexes = data.getExtras().getIntegerArrayList("indexes");
            tinyDB.putListInt("indexesList", indexes);
            songsListData.clear();
            for(int i=0; i<indexes.size(); i++)
            {
                for(int j=0; j<songsList.size(); j++)
                {
                    if((songsList.get(j).get("songPath")).toString().equals((songsList.get(indexes.get(i)).get("songPath")).toString()))
                    {
                        HashMap<String, String> song = songsList.get(j);
                        songsListData.add(song);
                    }
                }
            }
            // play selected song
            playSong(currentSongIndex);
        }
    }

    public void  playSong(int songIndex)
    {
        // Play song
        try
        {
            mp.reset();
            mp.setDataSource(songsListData.get(songIndex).get("songPath"));
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(songsListData.get(songIndex).get("songPath"));
            byte[] artBytes =  mmr.getEmbeddedPicture();
            if(artBytes != null)
            {
                InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                Bitmap bm = BitmapFactory.decodeStream(is);
                cover.setImageBitmap(bm);
            }
            else
                cover.setImageResource(R.drawable.adele);
            mp.prepare();
            mp.start();
            // wyswietlanie tytulu
            songTitle = songsListData.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);
            // zmiana play na pause przycisk
            btnPlay.setImageResource(R.drawable.btn_pause);
            // progressbar
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            // aktualizacja progressbaru
            updateProgressBar();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //aktualizacja timera
    public void updateProgressBar()
    {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable()
    {
        public void run()
        {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            // dlugosc calkowita
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // ukonczone
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
            // aktualizacja progress baru
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
    {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        // forward or backward to certain seconds
        mp.seekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }

   // piosenka skoczona repeat on shuffle on
    @Override
    public void onCompletion(MediaPlayer arg0)
    {
        // check for repeat is ON or OFF
        if(isRepeat)
        {
            // repeat is on play same song again
            playSong(currentSongIndex);
        }
        else if(isShuffle)
        {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }
        else
        {
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsListData.size() - 1))
            {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }
            else
            {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("possition", mp.getCurrentPosition());
        mp.pause();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int pos = savedInstanceState.getInt("possition");
        mp.seekTo(pos);
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mp.stop();
        mp.release();
    }
}
