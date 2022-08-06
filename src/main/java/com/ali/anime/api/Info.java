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


    // Gogoanime
    /*driver.get(String.format(infoUrl, anime));
        try {
        Thread.sleep(400);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
        System.out.println(driver.getPageSource());
    WebElement animeInfoBody = driver.findElement(By.className("anime_info_body_bg"));
    String cover = animeInfoBody.findElement(By.tagName("img")).getAttribute("src");
    String title = animeInfoBody.findElement(By.tagName("h1")).getText();
    List<WebElement> arrayList = animeInfoBody.findElements(By.tagName("p"));
    String type = arrayList.get(1).findElement(By.tagName("a")).getAttribute("title");
    String plot = arrayList.get(2).getText().replace("Plot Summary: ", "").replace("\"", "");
    List<WebElement> genres = arrayList.get(3).findElements(By.tagName("a"));
    List<String> genre = new ArrayList();
        for (WebElement i : genres) {
        genre.add(i.getText().replace(", ", ""));
    }
    String released = arrayList.get(4).getText().replace("Released: ", "");
    String status = arrayList.get(5).findElement(By.tagName("a")).getText();
    String otherName = arrayList.get(6).getText().replace("Other name: ", "");

    String l_episode = driver.findElement(By.id("episode_related")).findElements(By.tagName("li")).get(0).findElement(By.className("name")).getText().replace("EP", "");

    //List<WebElement> epLinks = driver.findElement(By.id("episode_page")).findElements(By.tagName("li"));
    //List<String> episodes = new ArrayList();
    //JavascriptExecutor js = (JavascriptExecutor) driver;
    //for (WebElement ep: epLinks) {
    //    js.executeScript("arguments[0].click()", ep.findElement(By.tagName("a")));
    //new WebDriverWait(driver, Duration.ofMillis(400)).until(ExpectedConditions.elementToBeClickable(ep.findElement(By.tagName("a")))).click();
    //    List<WebElement> litags = driver.findElement(By.id("episode_related")).findElements(By.tagName("li"));
    //    for (WebElement i : litags) {
    //        episodes.add(i.findElement(By.className("name")).getText().replace("EP ", ""));

        return new InfoModel(cover, title, l_episode, plot, type, genre, released, status, otherName);*/

}
