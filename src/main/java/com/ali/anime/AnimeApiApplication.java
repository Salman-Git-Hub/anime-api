package com.ali.anime;


import com.ali.anime.api.Info;
import com.ali.anime.api.Recent;
import com.ali.anime.api.Video;
import com.ali.anime.models.InfoModel;
import com.ali.anime.models.RecentlyAddedItem;
import com.ali.anime.models.VideoRequestModel;
import com.ali.anime.models.VideoResponseModel;
import com.ali.anime.utils.UserAgent;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@SpringBootApplication
@RestController
//@EnableAsync
public class AnimeApiApplication {
	private final Info info = new Info();
	private final Video video = new Video();
	private final Recent recent = new Recent();
	public static ChromeDriver infoDriver, videoDriver, recentDriver;
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		List<String> arg = Arrays.asList(args);
		String path = System.getProperty("user.dir") + "\\src\\main\\resources\\driver\\chromedriver.exe";
		if (!arg.isEmpty()) {
			path = arg.get(0);
		}
		loadChromeDriver(path);
		System.out.println("WebDriver loaded successfully!");
		context = SpringApplication.run(AnimeApiApplication.class, args);
	}


	private static void loadChromeDriver(String chromedriverPath) {
		System.setProperty("webdriver.chrome.silentOutput", "true");
		System.setProperty("java.awt.headless", "true");
		System.setProperty("webdriver.chrome.driver", chromedriverPath);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("--headless");
		String userAgent = UserAgent.getRandomUserAgent();
		options.addArguments(String.format("user-agent=\"%s\"", userAgent));
		DesiredCapabilities dcap = new DesiredCapabilities();
		dcap.setJavascriptEnabled(false);
		options.merge(dcap);
		//System.out.println(userAgent);
		infoDriver = new ChromeDriver(options);
		videoDriver = new ChromeDriver(options);
		recentDriver = new ChromeDriver(options);
		return;
	}


	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}


	@GetMapping("/api/anime/info/{name}&{site}")
	public InfoModel animeInfo(@PathVariable("name") String name, @PathVariable("site") String site) {
		return info.getInfo(infoDriver, name.replace(" ", "-").replace(":", "")
				.replace("!", "").replace(",", ""), site);
		//return new InfoModel("cover", "title", null, "plot", "type", null, "released", "status", "other");

	}


	@GetMapping("/api/anime/recent/{type}&{limit}&{site}")
	public List<RecentlyAddedItem> recentAnime(@PathVariable("type") String type, @PathVariable("limit") int limit,
											   @PathVariable("site") String site) {
		return recent.getRecentlyAdded(recentDriver, site, type, limit);
	}


	@PostMapping("/api/anime/video")
	public VideoResponseModel animeVideo(@RequestBody VideoRequestModel videoRequestModel) {
		String anime = videoRequestModel.getAnime().replace(" ", "-").replace(":", "")
				.replace("!", "").replace(",", "");
		String episode = videoRequestModel.getEpisode();
		String site = videoRequestModel.getSite();
		return video.getVideo(videoDriver, anime, episode, site);
	}


	@GetMapping("/close")
	public String close() {
		new Thread(this::shutDown).start();
		return "Closed SpringApplication";
	}

	private void shutDown() {
		infoDriver.close();
		videoDriver.close();
		recentDriver.close();
		SpringApplication.exit(context);
	}

}
