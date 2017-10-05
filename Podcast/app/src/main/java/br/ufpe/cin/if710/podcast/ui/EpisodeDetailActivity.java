package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class EpisodeDetailActivity extends Activity {

    private TextView mEpTitleTV;
    private TextView mEpPubDateTV;
    private TextView mEpDescriptionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        //para mostrar o botao de voltar na barra
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO preencher com informações do episódio clicado na lista...
        //preenchendo com infos do episódio clicado
        this.mEpTitleTV = findViewById(R.id.epTitleTv);
        this.mEpPubDateTV = findViewById(R.id.epPubDateTv);
        this.mEpDescriptionTV = findViewById(R.id.epDescriptionTv);

        this.mEpTitleTV.setText(getIntent().getExtras().getString(XmlFeedAdapter.EP_TITLE));
        this.mEpPubDateTV.setText(getIntent().getExtras().getString(XmlFeedAdapter.EP_PUBDATE));
        this.mEpDescriptionTV.setText(getIntent().getExtras().getString(XmlFeedAdapter.EP_DESCRIPTION));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Respondendo  action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
