package com.ali.animeapi.sources;

import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.models.Search;
import com.ali.animeapi.sources.animepahe.Animepahe;
import com.ali.animeapi.sources.animixplay.Animixplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimeSource {
    private Animixplay animixplay = new Animixplay();
    private Animepahe animepahe = new Animepahe();

    public List<Recent> getRecent(String type, String q, String site) {
        return switch (type) {
            case "sub" -> getSub(q, site);
            case "popular" -> getPopular(site);
            case "movie" -> getMovie(q, site);
            default -> new ArrayList<>();
        };
    }

    public Info getAnimeInfo(String q, String site) {

        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getInfo(q);
            } else if (Objects.equals(site, "animepahe")) {
                return animepahe.getInfo(q);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new Info(null, null, null, null, null, null, null,
                    null, null);
        }
        return new Info(null, null, null, null, null, null, null,
                null, null);
    }

    public Episode getAnimeEpisode(String q, String ep, String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getEpisode(q, ep);
            } else if (Objects.equals(site, "animepahe")) {
                return animepahe.getEpisode(q, ep);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new Episode(null, null, null, null);
        }
        return new Episode(null, null, ep, null);
    }

    public List<Search> getAnimeSearch(String q, String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getSearch(q);
            }
            else if (Objects.equals(site, "animepahe")) {
                return animepahe.getSearch(q);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }


    private List<Recent> getSub(String q, String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getSubs(q);
            } else if (Objects.equals(site, "animepahe")) {
                return animepahe.getSubs(q);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    private List<Recent> getPopular(String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getPopulars();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private List<Recent> getMovie(String q, String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getMovies(q);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }




}
