package com.ali.anime.models;


public class RecentlyAddedItem {
    private final int id;
    private final String title;
    private final String cover;
    private final String episode;
    private final String date;

    public RecentlyAddedItem(int id, String title, String cover, String episode, String date) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.episode = episode;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getEpisode() {
        return episode;
    }

    public String getDate() {
        return date;
    }
}
