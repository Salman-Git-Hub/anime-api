package com.ali.anime.api;

import com.ali.anime.models.VideoResponseModel;
import com.ali.anime.sources.Animixplay;
import org.openqa.selenium.chrome.ChromeDriver;

public class Video {
    private final Animixplay animixplay = new Animixplay();

    public VideoResponseModel getVideo(ChromeDriver driver, String anime, String episode, String site) {
        if (site.toLowerCase().equals("animixplay")) {
            return animixplay.getEpisode(driver, anime, episode);
        }


        return new VideoResponseModel("None", "None", "None", "None");
    }


}
