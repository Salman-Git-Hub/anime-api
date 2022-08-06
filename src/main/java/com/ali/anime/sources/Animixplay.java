package com.ali.anime.sources;

import com.ali.anime.models.InfoModel;
import com.ali.anime.models.RecentlyAddedItem;
import com.ali.anime.models.VideoResponseModel;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Animixplay {
    private static final String infoUrl = "https://animixplay.to/v1/%s";
    private static final String videoUrl = "https://animixplay.to/v1/%s/ep%s";
    private static final String recentUrl = "https://animixplay.to/?tab=%s";

    public InfoModel getInfo(ChromeDriver driver, String anime) {
        driver.get(String.format(infoUrl, anime));
        List<WebElement> epsList = driver.findElement(By.id("epslistplace")).findElements(By.className("btn-primary"));
        List<String> epList = new ArrayList<>();
        for (WebElement i: epsList) {
            epList.add(i.getText().trim());
        }
        String url = driver.findElement(By.id("aligncenter")).findElement(By.id("animebtn")).getAttribute("href");
        driver.get(url);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement animeLeftSide = driver.findElement(By.id("animeleftside"));
        String cover = driver.findElement(By.id("maincoverimage")).getAttribute("src");
        String title = List.of(animeLeftSide.findElement(By.id("addTitle")).getText().split("\n")).get(0);
        WebElement mainInfo = driver.findElement(By.id("addInfo"));
        List<String> mainInfoList = List.of(mainInfo.getAttribute("innerHTML").split("<br>"));
        //String episode = mainInfoList.get(1).replace(" Episodes: ", "").replace("\"", "").trim();
        String status = mainInfoList.get(2).replace(" Status: ", "").replace("\"", "").trim();
        String released = mainInfoList.get(3).replace(" Aired: ", "").replace("\"", "").trim();
        String studio = mainInfoList.get(mainInfoList.size() - 2).replace(" Studios: ", "").replace("\"", "").trim();
        List<String> genre = new ArrayList<>();
        for (WebElement i: mainInfo.findElements(By.tagName("a"))) {
            try{
                if (Objects.requireNonNull(i.getAttribute("href")).contains("genre")) {
                    genre.add(i.getText());
                }
            }
            catch (NullPointerException ignored) {
                ;
            }

        }
        String plot = driver.findElement(By.id("panelplace")).getText().replace("\\", "");
        return new InfoModel(cover, title, epList, plot, genre, released, status, studio);

    }

    public VideoResponseModel getEpisode(ChromeDriver driver, String anime, String episode) {
        driver.get(String.format(videoUrl, anime, episode.toUpperCase()));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //String source = driver.getPageSource().strip();
        //int st_tk = source.indexOf("cUPMDTk: ");
        //int ed_tk = source.indexOf("cFPWv");
        //System.out.println(source.substring(st_tk, ed_tk).replace("cUMPDTk: ", "").replace(",", ""));

        String title = driver.findElement(By.className("playerpage")).findElement(By.className("animetitle")).getText();
        String siteUrl = driver.findElement(By.id("iframeplayer")).getAttribute("src");
        //System.out.println(siteUrl);
        driver.get(siteUrl);
        try {
            Thread.sleep(450);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String perf = "var performance = window.performance || window.mozPerformance || window.msPerformance || " +
                "window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String network = String.valueOf(js.executeScript(perf));
        network = network.replace("[{", "").replace("}]", "");
        List<String> nls = List.of(network.split(",\s+"));
        String videoUrl = "";
        for (String i: nls) {
            if (i.startsWith("name=")) {
                List<String> checkI = List.of(i.replace("name=", "").split("/"));
                for (String j: checkI) {
                    if (j.startsWith("master.m3u8") || j.endsWith(".m3u8")) {
                        videoUrl = i.replace("name=", "");
                        break;
                    }
                }
            }

        }
        if (videoUrl.isEmpty()) {
            videoUrl = "None";
        }
        return new VideoResponseModel(videoUrl, siteUrl, title, episode);
    }


    public List<RecentlyAddedItem> getRecent(ChromeDriver driver, String type, int limit) {
        driver.get(String.format(recentUrl, type));
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String loadMore = "loadmorenewep();";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for(int i=1; i<limit; i++) {
            js.executeScript(loadMore);
        }
        int ad = 5 * limit;
        try {
            Thread.sleep(280 + ad);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<WebElement> results = driver.findElement(By.className("searchresult")).findElements(By.tagName("li"));
        List<RecentlyAddedItem> recentlyAddedItems = new ArrayList<>();
        int index = 1;
        for(WebElement element: results) {
            String cover = element.findElement(By.tagName("img")).getAttribute("src");
            String title = element.findElement(By.className("name")).getText();
            if (type.equals("movie") || type.equals("popular")) {
                String date = "";
                String episode = "";
                recentlyAddedItems.add(new RecentlyAddedItem(index, title, cover, episode, date));
            }
            else {
                String date = element.findElement(By.className("timetext")).getText();
                String episode = List.of(element.findElement(By.className("infotext"))
                                .getText()
                                .split("/"))
                        .get(0).replace("EP ", "");
                recentlyAddedItems.add(new RecentlyAddedItem(index, title, cover, episode, date));
            }
            index++;
        }
        return recentlyAddedItems;
    }


}

