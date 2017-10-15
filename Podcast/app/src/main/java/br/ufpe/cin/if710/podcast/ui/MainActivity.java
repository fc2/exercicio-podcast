package br.ufpe.cin.if710.podcast.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastDBHelper;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.services.DownloadXMLService;
import br.ufpe.cin.if710.podcast.ui.adapter.PodcastFeedAdapter;

import android.content.IntentFilter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";

    //TODO teste com outros links de podcast

    private ListView itemsListView;
    private ProgressBar mLoadingIndicator;

    private FinishedXMLDownloadedReceiver finishedDownloadedXMLReceiver = new FinishedXMLDownloadedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        itemsListView = (ListView) findViewById(R.id.items);

        checkStoragePermission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //new DownloadXmlTask().execute(RSS_FEED);

        // iniciando o IntentService pra fazer o parse e salvar no banco
        Intent downloadXML = new Intent(getApplicationContext(), DownloadXMLService.class);
        downloadXML.putExtra(DownloadXMLService.EXTRA_RSS_FEED, RSS_FEED);
        getApplicationContext().startService(downloadXML);

        //registrar o broadcast receiver para poder receber as mensagens
        this.registerReceiver(this.finishedDownloadedXMLReceiver,
                new IntentFilter(DownloadXMLService.FINISHED_DOWNLOADED_XML));
    }

    @Override
    protected void onStop() {
        super.onStop();
        PodcastFeedAdapter adapter = (PodcastFeedAdapter) itemsListView.getAdapter();
        adapter.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister o receiver
        unregisterReceiver(this.finishedDownloadedXMLReceiver);
    }


    //AsyncTask para ler as coisas do banco
    private class ReadFromDataBase extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... voids) {

            Cursor cursor = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI,
                    null, null, null, null);

            return cursor;

        }


        //inicializando o listview and set adapter. o onPostExecute() roda na main UI thread.
        protected void onPostExecute(Cursor cursor){

            List<ItemFeed> items = new ArrayList<>();

            if (cursor != null) {
                cursor.moveToFirst();

                //recuperando as informacoes do banco
                while (cursor.moveToNext()){

                    String ep_title = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_TITLE));
                    String ep_pubDate = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_DATE));
                    String ep_downloadLink = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_DOWNLOAD_LINK));
                    String ep_description = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_DESC));
                    String ep_link = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_LINK));
                    String ep_episodeURI = cursor.getString(cursor.getColumnIndexOrThrow(PodcastProviderContract.EPISODE_FILE_URI));

                    ItemFeed itemFeed = new ItemFeed(ep_title, ep_link, ep_pubDate, ep_description, ep_downloadLink,ep_episodeURI);
                    items.add(itemFeed);

                }
                //fechando o cursor
                cursor.close();
            }

            //Adapter Personalizado
            PodcastFeedAdapter adapter = new PodcastFeedAdapter(getApplicationContext(), R.layout.itemlista, items);

            //atualizar o list view
            itemsListView.setAdapter(adapter);
            itemsListView.setTextFilterEnabled(true);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

    }

    public void checkStoragePermission(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }else {
            Log.d("PERMISSAO", "Write external storage já permitido.");
        }
    }

    public class FinishedXMLDownloadedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //quando acabar lá, ler as coisas do banco
            new ReadFromDataBase().execute();
        }
    }




}
