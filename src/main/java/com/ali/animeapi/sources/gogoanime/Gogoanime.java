package com.ali.animeapi.sources.gogoanime;

import android.util.Base64;
import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.models.Search;
import com.ali.animeapi.utils.Network;
import com.ali.animeapi.utils.SourceLogger;
import com.google.gson.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Gogoanime {

    private final String recentUrl = "https://ajax.gogo-load.com/ajax/page-recent-release.html?page=%s&type=%s";
    private final String popularUrl = "https://gogoanime.dk/popular.html?page=%d";
    private final String movieUrl = "https://gogoanime.dk/anime-movies.html?page=%s";
    private final String infoUrl = "https://gogoanime.dk/category/%s";
    private final String episodeListUrl = "https://ajax.gogo-load.com/ajax/load-list-episode?ep_start=0&ep_end=%s&id=%s";
    private final String episodeUrl = "https://gogoanime.dk/%s-episode-%s";
    private final String searchUrl = "https://ajax.gogo-load.com/site/loadAjaxSearch?keyword=%s&id=-1&link_web=https://gogoanime.dk/";

    private final Network network = new Network();
    private SourceLogger sourceLogger = new SourceLogger("Gogoanime");
    private Logger logger = sourceLogger.getLogger();

    CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    logger.info("Network: " + chain.request().url());
                    return chain.proceed(chain.request());
                }
            })
            .cookieJar(cookieJar)
            .build();

    public List<Recent> getSubs(String page) throws IOException {
        logger.info(String.format("Requested: getSubs(); page=%s", page));
        if (page == null) {
            page = "1";
        }
        Request request = network.GET(String.format(recentUrl, page, "1"), null);
        ResponseBody r = client.newCall(request).execute().body();
        Document doc = Jsoup.parse(r.string());
        List<Element> elements = doc.getElementsByClass("pagination-list")
                .get(0)
                .getElementsByAttributeValueContaining("data-page", String.valueOf(Integer.parseInt(page) + 1));
        String nextPage = "";
        if (!elements.isEmpty()) {
            nextPage = elements.get(0).ownText();
        }

        List<Element> items = doc.getElementsByClass("items").get(0).getElementsByTag("li");
        List<Recent> recent = new ArrayList<>();
        for (Element item: items) {
            Element i = item.getElementsByTag("div").get(0);
            String title = i.getElementsByTag("a").get(0).attr("title");
            String url = "https://gogoanime.dk" +  i.getElementsByTag("a").get(0).attr("href");
            String cover = i.getElementsByTag("img").attr("src");
            String episode = item.getElementsByClass("episode").first().ownText().replace("Episode ", "");
            recent.add(new Recent(title, episode, url, cover, "Today"));
        }
        recent.add(new Recent(String.format(recentUrl, nextPage, "1")));
        logger.info("Response: Success!");
        return recent;

    }

    public List<Recent> getPopulars() throws IOException {
        logger.info(String.format("Requested: getPopulars()"));
        List<Recent> popular = new ArrayList<>();
        for (int i=1; i<5; i++) {
            Request request = network.GET(String.format(popularUrl, i), null);
            String r = client.newCall(request).execute().body().string();
            logger.info(String.format("Process: page=%d thread", i));
            new Thread(() -> popular.addAll(scrapePage(r))).run();
        }
        logger.info("Response: Success!");
        return popular;
    }


    public List<Recent> getMovies(String page) throws IOException {
        logger.info(String.format("Requested: getMovies(); page=%s", page));
        Request request = network.GET(String.format(movieUrl, page), null);
        ResponseBody r = client.newCall(request).execute().body();
        List<Recent> movie = scrapePage(r.string());
        logger.info("Response: Success!");
        return movie;
    }

    public Info getInfo(String name) throws IOException {
        logger.info(String.format("Requested: getInfo(); name=%s", name));
        Request request = network.GET(String.format(infoUrl, name), null);
        ResponseBody r = client.newCall(request).execute().body();
        Document doc = Jsoup.parse(r.string());
        Element animeInfo = doc.getElementsByClass("anime_info_body_bg").get(0);
        String cover = animeInfo.getElementsByTag("img").attr("src");
        String title = animeInfo.getElementsByTag("h1").text();
        List<Element> tags = animeInfo.getElementsByTag("p");
        String type = "", status = null, released = null, studio = null, plot = "";
        List<String> genre = new ArrayList<>();
        for (Element e: tags) {
            if (e.text().contains("Type:")) {
                type = e.getElementsByTag("a").text();
            }
            else if (e.text().contains("Genre:")) {
                for (Element i: e.getElementsByTag("a")) {
                    genre.add(i.text().replace(", ", ""));
                }
            }
            else if (e.text().contains("Released: ")) {
                released = e.ownText();
            }
            else if (e.text().contains("Plot")) {
                plot = e.ownText();
            }
            else if (e.text().contains("Status:")) {
                status = e.getElementsByTag("a").text();
            }
        }
        String id = doc.getElementById("movie_id").attr("value");
        Element ep = doc.getElementById("episode_page").getElementsByTag("a").get(0);
        String endEp = ep.attr("ep_end");
        List<String> episodes = scrapeEpisodes(id, endEp);
        logger.info("Response: Success!");
        return new Info(cover, title, episodes, plot, type, genre, released, status, null);

    }

    public Episode getEpisode(String name, String episode) throws IOException {
        logger.info(String.format("Requested: getEpisode(); name=%s, episode=%s", name, episode));
        String url = String.format(episodeUrl, name, episode);
        Request request = network.GET(url, null);
        ResponseBody r = client.newCall(request).execute().body();
        Document doc = Jsoup.parse(r.string());
        Element links = doc.getElementsByClass("anime_muti_link").get(0);
        String vidcdnUrl = "https:" + links.getElementsByClass("vidcdn").get(0).getElementsByTag("a").attr("data-video");

        Request request1 = network.GET(vidcdnUrl, null);
        ResponseBody r1 = client.newCall(request1).execute().body();
        Document document = Jsoup.parse(r1.string());
        String iv = document.getElementsByClass("wrapper").attr("class").replace("wrapper container-", "");
        String secretKey = document.body().attr("class").replace("container-", "");
        String s = document.head().getElementsByAttribute("data-value").attr("data-value");
        String decryptKey = document.getElementsByClass("videocontent").attr("class").replace("videocontent videocontent-", "");

        String encryptedAjaxParams = cryptoHandler(s, iv, secretKey, false);
        if (encryptedAjaxParams == null) {
            logger.severe("Process: encryptedAjaxParams is null");
            return new Episode(null, null, episode, null);
        }

        HttpUrl httpUrl = HttpUrl.parse(vidcdnUrl);
        String id = httpUrl.queryParameter("id");;
        String encryptedId = cryptoHandler(id, iv, secretKey, true);
        int i = encryptedAjaxParams.indexOf("&");
        // "${host}encrypt-ajax.php?id=$encryptedId&$encryptAjaxParams&alias=$id"
        String ur = String.format("https://%s/encrypt-ajax.php?id=%s&%s&alias=%s", httpUrl.host(), encryptedId, encryptedAjaxParams.substring(i), id);

        Request request2 = network.GET(ur, Headers.of("X-Requested-With", "XMLHttpRequest"));
        ResponseBody r2 = client.newCall(request2).execute().body();
        JsonObject jsonObject = new GsonBuilder().create().fromJson(r2.string(), JsonObject.class);
        String data = jsonObject.get("data").getAsString();
        String decryptedData = cryptoHandler(data, iv, decryptKey, false);
        if (decryptedData == null) {
            logger.severe("Process: decryptedData is null");
            return new Episode(vidcdnUrl, null, episode, "Today");
        }
        JsonArray source = new GsonBuilder().create().fromJson(decryptedData, JsonObject.class).getAsJsonArray("source");
        String streamUrl = source.get(0).getAsJsonObject().get("file").getAsString();
        logger.info("Response: Success!");
        return new Episode(vidcdnUrl, streamUrl, episode, "Today");


    }


    public List<Search> getSearch(String q) throws IOException {
        logger.info(String.format("Requested: getSearch(); q=%s", q));
        String uri = String.format(searchUrl, q);
        Request request = network.GET(uri, null);
        ResponseBody r = client.newCall(request).execute().body();
        JsonObject jsonObject = new GsonBuilder().create().fromJson(r.string(), JsonObject.class);
        Document doc = Jsoup.parse(jsonObject.get("content").getAsString());
        List<Element> searchItems = doc.getElementById("header_search_autocomplete_body").getElementsByClass("list_search_ajax");
        logger.info(String.format("Process: %d threads", searchItems.size()));
        List<Search> search = new ArrayList<>();
        for (Element e: searchItems) {
            new Thread(() -> {
                Element a = e.getElementsByTag("a").first();
                String url = a.attr("href");
                String title = a.ownText();
                String cover = a.getElementsByTag("div").attr("style")
                        .replace("background: url(\"", "")
                        .replace("\")", "");
                search.add(new Search(title, url, cover));
            }).run();
        }
        logger.info("Response: Success!");
        return search;

    }



    private List<String> scrapeEpisodes(String id, String endEp) throws IOException {
        logger.info(String.format("Process: scrapeEpisodes(); id=%s, endEp=%s", id, endEp));
        Request request = network.GET(String.format(episodeListUrl, endEp, id), null);
        ResponseBody r = client.newCall(request).execute().body();
        Document doc = Jsoup.parse(r.string());
        List<Element> eps = doc.getElementById("episode_related").getElementsByTag("a");
        List<String> epList = new ArrayList<>();
        for (Element e: eps) {
            String ep = e.getElementsByTag("div").get(0).ownText();
            epList.add(ep);
        }
        return epList;
    }


    private List<Recent> scrapePage(String html) {
        logger.info("Process: scrapePage()");
        Document doc = Jsoup.parse(html);
        List<Recent> recentPageItems = new ArrayList<>();
        List<Element> items = doc.getElementsByClass("items").get(0).getElementsByTag("li");
        for (Element item: items) {
            Element i = item.getElementsByTag("div").get(0);
            String title = i.getElementsByTag("a").get(0).attr("title");
            String url = "https://gogoanime.dk" +  i.getElementsByTag("a").get(0).attr("href");
            String cover = i.getElementsByTag("img").attr("src");
            recentPageItems.add(new Recent(title, null, url, cover, null));
        }
        return recentPageItems;

    }

    private String cryptoHandler(String s, String iv, String secretKeyString, Boolean encrypt) {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (! encrypt) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                return new String(cipher.doFinal(Base64.decode(s, Base64.DEFAULT)));
            }
            else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                return Base64.encodeToString(cipher.doFinal(s.getBytes()), Base64.NO_WRAP);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            logger.severe(String.format("Process: cryptoHandler(); s=%s, iv=%s, secretKeyString=%s, encrypt=%b\nException: %s",
                    s, iv, secretKey, encrypt, e.getMessage()));
        }
        return null;
    }


}
