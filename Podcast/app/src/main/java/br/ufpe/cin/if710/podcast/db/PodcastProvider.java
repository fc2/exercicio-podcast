package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class PodcastProvider extends ContentProvider {
    public PodcastProvider() {
    }

    private PodcastDBHelper podcastDBHelper;


    @Override
    public boolean onCreate() {
        // Implement this to initialize your content provider on startup.
        Context context;
        Context context = this.getContext();
        podcastDBHelper = PodcastDBHelper.getInstance(context);
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Implement this to handle requests to insert a new row.
        final SQLiteDatabase database = podcastDBHelper.getWritableDatabase();

        Uri returnUri;
        long id = database.insert(PodcastProviderContract.EPISODE_TABLE, null, values);
        //verificar se conseguimos adicionar
        if (id > 0){
            //contruindo a nova uri
            returnUri = ContentUris.withAppendedId(PodcastProviderContract.EPISODE_LIST_URI, id);
            Log.d("PodcastProvider", "inserting" + returnUri.toString());

        } else {
            throw new android.database.SQLException("Failed to insert row to" + uri);
        }

        //notificar ao resolver que aconteceu mudancas no uri
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
