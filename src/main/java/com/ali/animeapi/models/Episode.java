package com.ali.animeapi.models;

public class Episode {

    private String apiUrl;
    private String streamUrl;
    private String episode;

    public Episode(String apiUrl, String streamUrl, String episode) {
        this.apiUrl = apiUrl;
        this.streamUrl = streamUrl;
        this.episode = episode;
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
}
