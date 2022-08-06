package com.ali.anime.models;

public class VideoRequestModel {
    private String anime;
    private String episode;
    private String site;

    public VideoRequestModel(String anime, String episode, String site) {
        this.anime = anime;
        this.episode = episode;
        this.site = site;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }
}
