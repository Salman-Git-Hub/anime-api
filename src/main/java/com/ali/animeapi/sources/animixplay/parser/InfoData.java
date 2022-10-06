package com.ali.animeapi.sources.animixplay.parser;

import java.util.List;

public class InfoData {

    private Aired aired;
    private String episodes;
    private List<Genre> genres;
    private String image_url;
    private String premiered;
    private String type;
    private String status;
    private List<Studio> studios;
    private String synopsis;
    private String title;


    public InfoData(Aired aired, String episodes, List<Genre> genres, String cover, String premiered, String type, String status, List<Studio> studios, String synopsis, String title) {
        this.aired = aired;
        this.episodes = episodes;
        this.genres = genres;
        this.image_url = cover;
        this.premiered = premiered;
        this.type = type;
        this.status = status;
        this.studios = studios;
        this.synopsis = synopsis;
        this.title = title;
    }

    public Aired getAired() {
        return aired;
    }

    public String getEpisodes() {
        return episodes;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getPremiered() {
        return premiered;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public List<Studio> getStudios() {
        return studios;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getTitle() {
        return title;
    }

    public static class Studio {
        private String name;

        public Studio(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public static class Genre {
        private String name;

        public Genre(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public static class Aired {
        private String string;

        public Aired(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

}
