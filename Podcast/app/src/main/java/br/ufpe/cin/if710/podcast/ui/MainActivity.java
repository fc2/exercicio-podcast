package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.PodcastFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView itemsListView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        itemsListView = (ListView) findViewById(R.id.items);

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
        new DownloadXmlTask().execute(RSS_FEED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PodcastFeedAdapter adapter = (PodcastFeedAdapter) itemsListView.getAdapter();
        adapter.clear();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                saveItems(itemList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();
            new ReadFromDataBase().execute();
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    //funcao para adicionar os items no banco
    private void saveItems(List<ItemFeed> itemsList){

        for (ItemFeed item : itemsList){
            ContentValues contentValues = new ContentValues();

            contentValues.put(PodcastDBHelper.EPISODE_TITLE, item.getTitle());
            contentValues.put(PodcastDBHelper.EPISODE_LINK, item.getLink());
            contentValues.put(PodcastDBHelper.EPISODE_DESC, item.getDescription());
            contentValues.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, item.getDownloadLink());
            contentValues.put(PodcastDBHelper.EPISODE_DATE, item.getPubDate());
            contentValues.put(PodcastDBHelper.EPISODE_FILE_URI, "");

            Uri uri = getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, contentValues);

            if(uri != null){
                Log.d("AddItem", "Item added!");
            } else {
                Log.e("AddItem", "Failed to add" + item.getTitle());
            }

        }
    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
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

                    ItemFeed itemFeed = new ItemFeed(ep_title, ep_link, ep_pubDate, ep_description, ep_downloadLink);
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
        }
    }

}
