package com.ali.anime.api;

import com.ali.anime.models.*;
import com.ali.anime.sources.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class Info {
    private final Animixplay animixplay = new Animixplay();

    public InfoModel getInfo(ChromeDriver driver, String anime, String site) {
        if (site.equals("animixplay")) {
            return animixplay.getInfo(driver, anime);
        }

        return new InfoModel(null, null, null, null, null, null, null, null);
    }

}
