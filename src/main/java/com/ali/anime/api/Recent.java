package com.ali.anime.api;

import com.ali.anime.models.RecentlyAddedItem;
import com.ali.anime.sources.Animixplay;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class Recent {
    private final Animixplay animixplay = new Animixplay();

    public List<RecentlyAddedItem> getRecentlyAdded(ChromeDriver driver, String site, String type, int limit) {
        if (site.toLowerCase().equals("animixplay")) {
            return animixplay.getRecent(driver, type, limit);
        }
        List<RecentlyAddedItem> recentlyAddedItems = new ArrayList<>();
        recentlyAddedItems.add(new RecentlyAddedItem(1, "none", "none", "none", "none"));
        return recentlyAddedItems;
    }



}
