package com.ali.animeapi.models;

import java.util.List;

public class Info {
    private final String cover;
    private final String title;
    private final List<String> episodes;
    private final String plot;
    private final String type;
    private final List<String> genre;
    private final String released;
    private final String status;
    private final String studio;

    public Info(String cover, String title, List<String> episodes, String plot, String type, List<String> genre, String released, String status, String studio) {
        this.cover = cover;
        this.title = title;
        this.episodes = episodes;
        this.plot = plot;
        this.type = type;
        this.genre = genre;
        this.released = released;
        this.status = status;
        this.studio = studio;
    }

    public String getCover() {
        return cover;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getEpisodes() {
        return episodes;
    }

    public String getPlot() {
        return plot;
    }

    public String getType() {
        return type;
    }

    public List<String> getGenre() {
        return genre;
    }

    public String getReleased() {
        return released;
    }

    public String getStatus() {
        return status;
    }

    public String getStudio() {
        return studio;
    }
}
