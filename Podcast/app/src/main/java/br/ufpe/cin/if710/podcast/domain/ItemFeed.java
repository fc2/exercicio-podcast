package br.ufpe.cin.if710.podcast.domain;

public class ItemFeed {
    private final String title;
    private final String link;
    private final String pubDate;
    private final String description;
    private final String downloadLink;
    private final String localURI;
    private int audioCurrentTime;


    public ItemFeed(String title, String link, String pubDate, String description,
                    String downloadLink, String localURI, int audioCurrentTime) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
        this.localURI = localURI;
        this.audioCurrentTime = audioCurrentTime;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getLocalURI() {
        return localURI;
    }

    public int getAudioCurrentTime() {
        return audioCurrentTime;
    }

    public void setAudioCurrentTime(int audioCurrentTime) {
        this.audioCurrentTime = audioCurrentTime;
    }

    @Override
    public String toString() {
        return title;
    }
}