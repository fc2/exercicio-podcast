package br.ufpe.cin.if710.podcast.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastDBHelper;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.MainActivity;


public class DownloadXMLService extends IntentService {

    //debug tag
    private String TAG = "ServiceXML";

    public static final String FINISHED_DOWNLOADED_XML = "br.ufpe.cin.if710.podcast.services.action.FINISHED_DOWNLOADED_XML";

    public static final String EXTRA_RSS_FEED = "RSS_FEED";

    public DownloadXMLService() {
        super("DownloadXMLService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        List<ItemFeed> itemList = new ArrayList<>();
        try {

            String RSSFeedURL = intent.getStringExtra(EXTRA_RSS_FEED);

            itemList = XmlFeedParser.parse(getRssFeed(RSSFeedURL));
            saveItems(itemList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        // Send the feedback message to the MainActivity
        Intent finishedDownloadedIntent = new Intent(DownloadXMLService.FINISHED_DOWNLOADED_XML);
        finishedDownloadedIntent.putExtra("Intent", "downloaded!");
        sendBroadcast(finishedDownloadedIntent);
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
                // Log.d("AddItem", "Item added!");
            } else {
                Log.e("AddItem", "Failed to add" + item.getTitle());
            }

        }
    }


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

}
