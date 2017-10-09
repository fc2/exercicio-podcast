package br.ufpe.cin.if710.podcast.ui.adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;


import java.net.URL;
import java.io.*;



public class PodcastFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;

    public static final String EP_TITLE = "Title";
    public static final String EP_PUBDATE = "PubDate";
    public static final String EP_DESCRIPTION = "Description";


    public PodcastFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button downloadButton;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.downloadButton = (Button) convertView.findViewById(R.id.item_action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ItemFeed item = getItem(position);

        holder.item_title.setText(item.getTitle());
        holder.item_date.setText(item.getPubDate());

        convertView.setOnClickListener((new View.OnClickListener() {

            //escutando clicks no itemFeed da lista
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(getContext(), EpisodeDetailActivity.class);
                detailsIntent.putExtra(EP_TITLE, item.getTitle());
                detailsIntent.putExtra(EP_PUBDATE, item.getPubDate());
                detailsIntent.putExtra(EP_DESCRIPTION, item.getDescription());

                getContext().startActivity(detailsIntent);
            }
        }));

        //ouvindo click no botao de baixar
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chamando o AsyncTask para aixar o posdcast selecionado
                new DownloadPodcast(getContext(), item).execute();
            }
        });

        return convertView;
    }
}


//AsyncTask<Params, Progress, Result>
class DownloadPodcast extends AsyncTask<Void, Void, Void>{

    private ItemFeed itemFeed;
    private Context context;
    private String TAG = "DOWNLOAD_TASK";

    private File file;


    public DownloadPodcast(Context context, ItemFeed itemFeed){
        this.itemFeed = itemFeed;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Baixando o episodio...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            //pegar o InputStream do url do episodio clicado
            URL episodeURL = new URL(this.itemFeed.getDownloadLink());

            //criando uma conexao
            HttpURLConnection urlConnection = (HttpURLConnection) episodeURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            //dizendo onde vai salvar o file baixado

            File folderSDCard = new File(Environment.getExternalStorageDirectory() + "/" + "Podcast");

            if (!folderSDCard.exists()) {
                folderSDCard.mkdir();
            }

            String fileName = this.itemFeed.getTitle() + ".mp3";
            file = new File(folderSDCard, fileName);

            if(!file.exists()){
                file.createNewFile();
                Log.d(TAG,"Arquivo criado!");
            }else {
                Log.d(TAG,"Esse arquivo já existe!");
            }

            //para escrever o dado baixado no arquivo criado
            FileOutputStream outputStream = new FileOutputStream(file);
            //ler os dados do arquivo
            InputStream inputStream = urlConnection.getInputStream();

            byte [] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer))>0){
                outputStream.write(buffer, 0, bufferLength);
            }

            //fechando os streams quando finalizados
            outputStream.close();
            inputStream.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(file == null){
            Log.e(TAG, "Aconteceu alguma coisa errada!");
            Toast.makeText(context, "Ocorreu um erro durante o download...", Toast.LENGTH_SHORT).show();
        }else {
            Log.d(TAG, "Fim do download!");
            Toast.makeText(context, "Finalizando o download...", Toast.LENGTH_SHORT).show();

            //todo salvar o caminho do arquivo no banco
            Log.d(TAG, file.getPath());
        }
    }
}
