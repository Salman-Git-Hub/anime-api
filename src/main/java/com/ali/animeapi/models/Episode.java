package com.ali.animeapi.models;

public class Episode {

    private String apiUrl;
    private String streamUrl;
    private String episode;
    private String date;

    public Episode(String apiUrl, String streamUrl, String episode, String date) {
        this.apiUrl = apiUrl;
        this.streamUrl = streamUrl;
        this.episode = episode;
        this.date = date;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getEpisode() {
        return episode;
    }

    public String getDate() {
        return date;
    }
}
