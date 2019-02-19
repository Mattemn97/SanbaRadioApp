package com.example.sanbaradioapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Menu_laterale extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button buttonPlay;
    private Button buttonStopPlay;
    private MediaPlayer player;
    private TextView titolo_brano;
    private TextView artista_brano;
    private TextView chi_siamo;
    private TextView info_orario;
    private TextView info_titolo;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_laterale);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //inizializzazione menu_laterale
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);
        buttonStopPlay = findViewById(R.id.buttonStopPlay);
        buttonStopPlay.setEnabled(false);
        buttonStopPlay.setOnClickListener(this);
        titolo_brano = findViewById(R.id.text_titolo_brano);
        artista_brano = findViewById(R.id.text_artista_brano);
        initVolControls();
        initializeMediaPlayer();
        getInfoBrano();

        //inizializzazione palinsestoLayout
        info_orario = findViewById(R.id.text_orario_palinsesto);
        info_titolo = findViewById(R.id.text_titolo_palinsesto);

        //inizializzazione chisiamoLayout
        chi_siamo = findViewById(R.id.textChisiamo);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void onClick(View v) {
        if (v == buttonPlay) {
            startPlaying();
        } else if (v == buttonStopPlay) {
            stopPlaying();
        }
    }

    private void startPlaying() {
        buttonStopPlay.setEnabled(true);
        buttonPlay.setEnabled(false);

        player.prepareAsync();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });

    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }

        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource("https://ssl1.azotosolutions.com:1020/stream?icy=http");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initVolControls() {
        try {
            SeekBar volumeSeekbar = (SeekBar) findViewById(R.id.seekBar_volume);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            assert audioManager != null;
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            stopPlaying();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            stopPlaying();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_laterale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public void getInfoBrano() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Elements query = null;
                String link = "https://www.sanbaradio.it/ascoltaci";

                try {
                    Document document = Jsoup.connect(link).get();
                    query = document.select("#stream1 > div > div.player-ctr > div.track-info-wpr > div > div > span.artist-name.animated");


                } catch (IOException e) {
                }

                final String finalInfo = query.text();
                return finalInfo;
            }

            @Override
            protected void onPostExecute(String finalInfo) { titolo_brano.setText(finalInfo);
            }
        }.execute();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Elements query = null;
                String link = "https://www.sanbaradio.it/ascoltaci";

                try {
                    Document document = Jsoup.connect(link).get();
                    query = document.select("#stream1 > div > div.player-ctr > div.track-info-wpr > div > div > span.songtitle.animated");


                } catch (IOException e) {
                }

                final String finalInfo = query.text();
                return finalInfo;
            }

            @Override
            protected void onPostExecute(String finalInfo) { artista_brano.setText(finalInfo);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getPalinsesto(final int id) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String link = null;
                // scelgo che link utilizzare
                switch (id) {
                    case R.id.nav_palinsesto_lunedi:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/luned%C3%AC";
                        break;
                    case R.id.nav_palinsesto_martedi:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/marted%C3%AC";
                        break;
                    case R.id.nav_palinsesto_mercoledi:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/mercoled%C3%AC";
                        break;
                    case R.id.nav_palinsesto_giovedi:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/gioved%C3%AC";
                        break;
                    case R.id.nav_palinsesto_venerdi:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/venerd%C3%AC";
                        break;
                    case R.id.nav_palinsesto_sabato:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/sabato";
                        break;
                    case R.id.nav_palinsesto_domenica:
                        link = "https://www.sanbaradio.it/index.php?q=node/14044/view/domenica";
                        break;
                }
                try {
                    final Document document = Jsoup.connect(link).get();
                    final Elements rows = document.select("#content > div.content-middle > div > div > table > tbody > tr");

                    Handler mainHandler = new Handler(getMainLooper());
                    mainHandler.post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            info_orario.setText(null);
                            info_titolo.setText(null);

                            Element row = rows.get(1);
                            Elements cells = row.select("td");
                            Log.d("MENU", row.text());
                            for (int j = 0; j < cells.size() - 1; j++) {
                                Element cell = cells.get(j);
                                switch (j){
                                    case 0:
                                        info_orario.setText(cell.text());
                                        break;
                                    case 1:
                                        info_titolo.setText(cell.text());
                                        break;
                                }
                            }

                            for (int i = 2; i < rows.size(); i++) {
                                Element row2 = rows.get(i);
                                Elements cells2 = row2.select("td");
                                Log.d("MENU", row2.text());
                                for (int j = 0; j < cells2.size() - 1; j++) {
                                    Element cell2 = cells2.get(j);
                                    switch (j){
                                        case 0:
                                            info_orario.setText(info_orario.getText() + "\n\n\n\n\n" + cell2.text());
                                            break;
                                        case 1:
                                            info_titolo.setText( info_titolo.getText() + "\n\n\n\n\n" + cell2.text());
                                            break;
                                    }
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getchisiamo() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Elements query = null;
                String link = "http://www.sanbaradio.it/content/sanbaradio";

                try {
                    Document document = Jsoup.connect(link).get();
                    query = document.select("#content > div.content-middle > div > div.content > p");

                } catch (IOException e) {
                }

                final String finalInfo = query.text();
                return finalInfo;
            }

            @Override
            protected void onPostExecute(String finalInfo) {
                chi_siamo.setText(finalInfo);
            }
        }.execute();
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_diretta) {
            findViewById(R.id.streamingLayout).setVisibility(View.VISIBLE);
            getInfoBrano();
            findViewById(R.id.chi_siamoLayout).setVisibility(View.GONE);
            findViewById(R.id.palinsestoLayout).setVisibility(View.GONE);

        } else if (id == R.id.nav_chi_siamo) {
            findViewById(R.id.chi_siamoLayout).setVisibility(View.VISIBLE);
            getchisiamo();
            findViewById(R.id.streamingLayout).setVisibility(View.GONE);
            findViewById(R.id.palinsestoLayout).setVisibility(View.GONE);

        } else {
            findViewById(R.id.chi_siamoLayout).setVisibility(View.GONE);
            findViewById(R.id.streamingLayout).setVisibility(View.GONE);
            getPalinsesto(id);
            findViewById(R.id.palinsestoLayout).setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
