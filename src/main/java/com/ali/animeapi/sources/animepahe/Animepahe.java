package com.ali.animeapi.sources.animepahe;

import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.models.Search;
import com.ali.animeapi.sources.animepahe.parser.EpisodeListData;
import com.ali.animeapi.sources.animepahe.parser.RecentResponseData;
import com.ali.animeapi.sources.animepahe.parser.SearchData;
import com.ali.animeapi.utils.Network;
import com.ali.animeapi.utils.SourceLogger;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Animepahe {

    private final String subUrl = "https://animepahe.com/api?m=airing&page=%s";
    private final String infoUrl = "https://animepahe.com/anime/";
    private final String episodeListUrl = "https://animepahe.com/api?m=release&id=%s&sort=episode_asc&page=1";
    private final String episodeUrl = "https://animepahe.com/api?m=links&id=%s&p=kwik";
    private final String searchUrl = "https://animepahe.com/api?m=search&q=%s";

    private final Network network = new Network();
    private final SourceLogger sourceLogger = new SourceLogger("AnimePahe");
    private final Logger logger = sourceLogger.getLogger();
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
        String uri = String.format(subUrl, page);
        Request request = network.GET(uri, null);
        ResponseBody r = client.newCall(request).execute().body();
        RecentResponseData responseData = new GsonBuilder().create().fromJson(r.string(), RecentResponseData.class);

        if (responseData == null || responseData.getData() == null) {
            logger.warning("Process: responseData is null");
            return new ArrayList<>();
        }
        List<Recent> recent = new ArrayList<>();
        for (RecentResponseData.ResponseDataItems item: responseData.getData()) {
            String title = item.getAnime_title();
            String episode = item.getEpisode();
            String cover = item.getSnapshot();
            String date = item.getCreated_at();
            String url = "https://animepahe.com/anime/" + item.getSession();
            recent.add(new Recent(title, episode, url, cover, date));
        }
        recent.add(new Recent(responseData.getNext_page_url()));
        logger.info("Response: Success!");
        return recent;

    }

    // no getPopulars(), getMovies()

    public Info getInfo(String sess) throws IOException {
        logger.info(String.format("Requested: getInfo(); sess=%s", sess));
        String url = infoUrl + sess;
        Request request = network.GET(url, null);
        ResponseBody r = client.newCall(request).execute().body();
        Document doc = Jsoup.parse(r.string());
        String cover = doc.getElementsByClass("anime-poster").get(0).getElementsByTag("a").attr("href");
        String title = doc.getElementsByTag("h1").first().getElementsByTag("span").first().ownText();
        String plot = doc.getElementsByClass("anime-synopsis").get(0).text();
        List<Element> elements = doc.getElementsByClass("anime-info").get(0).getElementsByTag("p");
        String type = "", status = null, aired = null, season = "", studio = null;
        for (Element e: elements) {
            if (e.text().contains("Type:")) {
                type = e.getElementsByTag("a").text();
            }
            if (e.text().contains("Status:")) {
                status = e.getElementsByTag("a").text();
            }
            if (e.text().contains("Aired:")) {
                aired = e.ownText();
            }
            if (e.text().contains("Season:")) {
                season = e.getElementsByTag("a").text();
            }
            if (e.text().contains("Studio:")) {
                studio = e.ownText();
            }

        }
        List<String> genre = new ArrayList<>();
        for (Element e: doc.getElementsByClass("anime-genre").get(0).getElementsByTag("li")) {
            String g = e.getElementsByTag("a").text();
            genre.add(g);
        }
        EpisodeListData episodeListData = episodesList(sess);
        List<String> episodes = new ArrayList<>();
        for(EpisodeListData.EpisodeDataItem item: episodeListData.getData()) {
            episodes.add(item.getEpisode());
        }
        logger.info("Response: Success!");
        return new Info(cover, title, episodes, plot, season + " " + type, genre, aired, status, studio);

    }

    public Episode getEpisode(String sess, String episode) throws IOException {
        logger.info(String.format("Requested: getEpisode(); sess=%s, episode=%s", sess, episode));
        EpisodeListData episodeListData = episodesList(sess);
        EpisodeListData.EpisodeDataItem item = episodeListData.getData().get(Integer.parseInt(episode) - 1);
        String url = String.format(episodeUrl, item.getSession());
        Request request = network.GET(url, null);
        String body = client.newCall(request).execute().body().string();
        JsonArray episodeData = new GsonBuilder().create().fromJson(body, JsonObject.class).getAsJsonArray("data");
        List<String> epUrls = new ArrayList<>();
        List<String> apiUrl = new ArrayList<>();
        for (JsonElement e: episodeData) {
            Set<String> key = e.getAsJsonObject().keySet();
            if (key.contains("360")) {
                logger.info("Process: Parsing 360 url");
                apiUrl.add("360");
                new Thread(() -> epUrls.add(kwikParser(e.getAsJsonObject().getAsJsonObject("360").get("kwik").getAsString()))).run();
            }
            if (key.contains("720")) {
                logger.info("Process: Parsing 720 url");
                apiUrl.add("720");
                new Thread(() -> epUrls.add(kwikParser(e.getAsJsonObject().getAsJsonObject("720").get("kwik").getAsString()))).run();
            }
            if (key.contains("1080")) {
                logger.info("Process: Parsing 1080 url");
                apiUrl.add("1080");
                new Thread(() -> epUrls.add(kwikParser(e.getAsJsonObject().getAsJsonObject("1080").get("kwik").getAsString()))).run();
            }

        }
        logger.info("Response: Success!");
        return new Episode(String.join(", ", apiUrl), String.join(", ", epUrls), episode, item.getCreated_at());

    }

    public List<Search> getSearch(String q) throws IOException {
        logger.info(String.format("Requested: getSearch(); q=%s", q));
        String uri = String.format(searchUrl, q);
        Request request = network.GET(uri, null);
        ResponseBody body = client.newCall(request).execute().body();
        SearchData searchData = new GsonBuilder().create().fromJson(body.string(), SearchData.class);
        List<Search> search = new ArrayList<>();
        logger.info(String.format("Process: %d threads", searchData.getData().size()));
        for (SearchData.SearchDataItem item: searchData.getData()) {
            new Thread(() -> {
                String title = item.getTitle();
                String cover = item.getPoster();
                String url = "https://animepahe.com/anime/" + item.getSession();
                search.add(new Search(title, url, cover));
            }).run();
        }
        logger.info("Response: Success!");
        return search;
    }

    private String kwikParser(String kwikUrl) {
        Request request = network.GET(kwikUrl, Headers.of(
                "referer", "https://animepahe.com"
        ));
        String body = null;
        try {
            body = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe(String.format("Process: kwikParser(); kwikUrl=%s\nIOException " + e.getMessage()));
        }
        int s = body.indexOf("m3u8|uwu");
        int e = body.lastIndexOf("'.split('|')");
        List<String> urls = new ArrayList<>(List.of(body.substring(s, e).split("\\|")));
        Collections.reverse(urls);
        String url = urls.get(0) + "://" + urls.get(1) + "-" + urls.get(2) + "." + urls.get(3) + "." + urls.get(4) + "."
                + urls.get(5) + "/" + urls.get(6) + "/" + urls.get(7) + "/" + urls.get(8) + "/uwu.m3u8";
        return url;
    }


    private EpisodeListData episodesList(String sess) throws IOException {
        logger.info(String.format("Process: getEpisodes(); sess=%s", sess));
        Request request = network.GET(String.format(episodeListUrl, sess), null);
        ResponseBody r = client.newCall(request).execute().body();
        return new GsonBuilder().create().fromJson(r.string(), EpisodeListData.class);


    }



}
