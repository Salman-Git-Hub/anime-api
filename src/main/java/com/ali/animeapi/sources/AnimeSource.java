package com.ali.animeapi.sources;

import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.sources.animixplay.Animixplay;
import com.ali.animeapi.sources.animixplay.parser.SearchData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimeSource {
    private Animixplay animixplay = new Animixplay();

    public List<Recent> getRecent(String type, String q, String site) {
        return switch (type) {
            case "sub" -> getSub(q, site);
            case "popular" -> getPopular(site);
            case "movie" -> getMovie(q, site);
            default -> new ArrayList<>();
        };
    }

    public Info getAnimeInfo(String q, String site) {
        if (Objects.equals(site, "animixplay")) {
            try {
                return animixplay.getInfo(q);
            } catch (IOException e) {
                e.printStackTrace();
                return new Info(null, null, null, null, null, null, null,
                        null, null);
            }
        }
        return new Info(null, null, null, null, null, null, null,
                null, null);
    }

    public Episode getAnimeEpisode(String q, String ep, String site) {
        if (Objects.equals(site, "animixplay")) {
            try {
                return animixplay.getEpisode(q, ep);
            } catch (IOException e) {
                e.printStackTrace();
                return new Episode(null, null, ep);
            }
        }
        return new Episode(null, null, ep);
    }

    public List<SearchData> getAnimeSearch(String q, String site) {
        if (Objects.equals(site, "animixplay")) {
            try {
                return animixplay.getSearch(q);
            }
            catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }


    private List<Recent> getSub(String q, String site) {
        try {
            if (Objects.equals(site, "animixplay")) {
                return animixplay.getSubs(q);
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
