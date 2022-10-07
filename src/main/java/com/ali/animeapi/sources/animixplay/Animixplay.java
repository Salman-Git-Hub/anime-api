package com.ali.animeapi.sources.animixplay;

import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.models.Search;
import com.ali.animeapi.sources.animixplay.parser.InfoData;
import com.ali.animeapi.sources.animixplay.parser.RecentResponseData;
import com.ali.animeapi.utils.Network;
import com.ali.animeapi.utils.SourceLogger;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Animixplay {

    private final String searchApiUrl = "https://animixplay.to/api/search";
    private final String popularUrl = "https://animixplay.to/assets/s/popularfull.json";
    private final String infoUrl = "https://animixplay.to/assets/mal/%s.json";
    private final String cdnSearchUrl = "https://cdn.animixplay.to/api/search";
    private final Network network = new Network();
    private SourceLogger sourceLogger = new SourceLogger("Animixplay");
    private Logger logger = sourceLogger.getLogger();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
//                    System.out.println("Animixplay: " + chain.request().url());
                    logger.info("Network: " + chain.request().url());
                    return chain.proceed(chain.request());
                }
            })
            .build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public List<Recent> getSubs(String time) throws IOException {
        logger.info(String.format("Requested: getSubs(); time=%s", time));
        if (time == null || time.equals("")) {
            time = "3020-05-06 00:00:00"; // default value for first page
        }
        // request post data
        RequestBody body = new FormBody.Builder()
                .add("seasonal", time).build();

        Request request = network.POST(searchApiUrl, null, body);
        ResponseBody r = client.newCall(request).execute().body();
        // string to ResponseData object
        RecentResponseData responseData = new GsonBuilder().create().fromJson(r.string(), RecentResponseData.class);

        if (responseData == null || responseData.getResult() == null) {
            logger.warning("Process: responseData is null");
            return new ArrayList<>();
        }
        List<Recent> recent = new ArrayList<>();
        for (RecentResponseData.ResponseDataItems item : responseData.getResult()) {
            String title = item.getTitle();
            String cover = item.getPicture();
            String episode = List.of(item.getInfotext().replace("EP ", "").split("/")).get(0);
            String url = "https://animixplay.to" + item.getUrl();
            String date = item.getTimetop();
            recent.add(new Recent(title, episode, url, cover, date));
        }
        recent.add(new Recent(responseData.getLast()));
        logger.info("Response: Success!");
        return recent;

    }


    // public List<Recent> getDubs(String time) throws IOException {}

    public List<Recent> getPopulars() throws IOException {
        logger.info("Requested: getPopulars()");
        Request request = network.GET(popularUrl, null);
        ResponseBody r = client.newCall(request).execute().body();
        RecentResponseData recentResponseData = new GsonBuilder().create().fromJson(r.string(), RecentResponseData.class);
        List<Recent> popular = new ArrayList<>();
        for (RecentResponseData.ResponseDataItems item: recentResponseData.getResult()) {
            String title = item.getTitle();
            String cover = item.getPicture();
            String url = item.getUrl();
            popular.add(new Recent(title, null, url, cover, null));
        }
        logger.info("Response: Success!");
        return popular;

    }


    public List<Recent> getMovies(String code) throws IOException {
        logger.info(String.format("Requested: getMovies(); code=%s", code));
        if (code == null || code.equals("")) {
            code = "99999999";
        }

        RequestBody body = new FormBody.Builder()
                .add("movie", code)
                .build();
        Request request = network.POST(searchApiUrl, null, body);
        ResponseBody r = client.newCall(request).execute().body();
        RecentResponseData recentResponseData = new GsonBuilder().create().fromJson(r.string(), RecentResponseData.class);
        List<Recent> movies = new ArrayList<>();
        for (RecentResponseData.ResponseDataItems item: recentResponseData.getResult()) {
            String title = item.getTitle();
            String url = item.getUrl();
            String cover = item.getPicture();
            movies.add(new Recent(title, null, url, cover, null));
        }
        movies.add(new Recent(recentResponseData.getLast()));
        logger.info("Response: Success!");
        return movies;
    }

    public Info getInfo(String code) throws IOException {
        logger.info(String.format("Requested: getInfo(); code=%s", code));
        String url = String.format(infoUrl, code);
        Request request = network.GET(url, null);
        ResponseBody r = client.newCall(request).execute().body();
        InfoData infoData = new GsonBuilder().create().fromJson(r.string(), InfoData.class);
        String title = infoData.getTitle();
        String cover = infoData.getImage_url();
//        String episodes = infoData.getEpisodes();
        List<String> episodes = episodesList(code);
        String plot = infoData.getSynopsis();
        List<String> genre = new ArrayList<>();
        for (InfoData.Genre g: infoData.getGenres()) {
            genre.add(g.getName());
        }
        String released = infoData.getAired().getString();
        String status = infoData.getStatus();
        List<String> studioList = new ArrayList<>();
        for (InfoData.Studio s: infoData.getStudios()) {
            studioList.add(s.getName());
        }
        String studio = String.join(", ", studioList);
        String type = String.join(" ", infoData.getPremiered(), infoData.getType());
        logger.info("Response: Success!");
        return new Info(cover, title, episodes, plot, type, genre, released, status, studio);

    }

    private List<String> episodesList(String code) throws IOException {
       String url = getSourceUrl(code);
        Request request2 = network.GET("https://animixplay.to" + url, null);
        String r = client.newCall(request2).execute().body().string();
        Document document = Jsoup.parse(r);
        Element t = document.getElementById("epslistplace");
        JsonObject jsonObject = new GsonBuilder().create().fromJson(t.text(), JsonObject.class);
        List<String> eps = new ArrayList<>();
        for (String e: jsonObject.keySet()) {
            if (!e.equals("eptotal") && !e.equals("extra")) {
                eps.add(e);
            }
        }
        try {
            for (String e: jsonObject.get("extra").getAsJsonObject().keySet()) {
                eps.add(e);
            }
        }
        catch (NullPointerException e) {
        }
        return eps;

    }


    private String getSourceUrl(String code) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("recomended", code)
                .build();

        Request request = network.POST(searchApiUrl, null, body);
        JsonArray response = new GsonBuilder().create().fromJson(client.newCall(request).execute().body().string(), JsonObject.class).getAsJsonArray("data");
        String url = response.get(0).getAsJsonObject().getAsJsonArray("items").get(0).getAsJsonObject().get("url").getAsString();
        return url;
    }


    public Episode getEpisode(String code, String episode) throws IOException {
        logger.info(String.format("Requested: getEpisode(); code=%s, episode=%s", code, episode));
        String url = "https://animixplay.to" + getSourceUrl(code) + "/ep" + episode;
        Request request = network.GET(url, null);
        String html = client.newCall(request).execute().body().string();
        Document doc = Jsoup.parse(html);
        Element t = doc.getElementById("epslistplace");
        JsonObject jsonObject = new GsonBuilder().create().fromJson(t.text(), JsonObject.class);
        if (jsonObject.isJsonNull()) {
            logger.warning("Process: JsonObject is null");
            return new Episode(null, null, episode, null);
        }
        String gogoHdId = List.of(jsonObject.get(String.valueOf(Integer.parseInt(episode) - 1)).getAsString().split("=")).get(1);
        if (gogoHdId != null) {
            String engogHdId = Base64.getEncoder().encodeToString(gogoHdId.getBytes(StandardCharsets.UTF_8));
            String ue = Base64.getEncoder().encodeToString((gogoHdId + "LTXs3GrU8we9O" + engogHdId).getBytes(StandardCharsets.UTF_8));
            String apiUrl = "https://animixplay.to/api/live" + ue;
            Request request2 = network.GET(apiUrl, null);
            String s = client.newCall(request2).execute().request().url().toString();
            try {
                String enUrl = List.of(s.split("#")).get(1);
                String streamUrl = new String(Base64.getDecoder().decode(enUrl.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                logger.info("Response: Success!");
                return new Episode(apiUrl, streamUrl, episode, String.valueOf(System.currentTimeMillis()));
            }
            catch (IndexOutOfBoundsException e) {
                logger.log(Level.SEVERE, "Process: Index Error\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        logger.log(Level.SEVERE, "Process: gogoHdId is null");
        logger.log(Level.SEVERE, "Response: Bad!");
        return new Episode(null, null, episode, null);

    }


    public List<Search> getSearch(String q) throws IOException {
        logger.info(String.format("Requested: getSearch(); q=%s", q));
        RequestBody body = new FormBody.Builder()
                .add("qfast", q)
                .add("root", "animixplay.to")
                .build();
        Request request = network.POST(cdnSearchUrl, null, body);
        ResponseBody r = client.newCall(request).execute().body();
        String html = new GsonBuilder().create().fromJson(r.string(), JsonObject.class).get("result").getAsString();
        Document doc = Jsoup.parse(html);
        List<Element> results = doc.getElementsByTag("a");
        logger.info(String.format("Process: %d threads", results.size()));
        List<Search> search = new ArrayList<>();
        for (Element e: results) {
            Thread t = new Thread(() -> {
                String title = e.attr("title");
                String url = e.attr("href");
                String cover = e.getElementsByClass("resultimg2").attr("src");
                search.add(new Search(title, url, cover));
            });
            t.run();
        }
        logger.info("Response: Success!");
        return search;

    }





}
