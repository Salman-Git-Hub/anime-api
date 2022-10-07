package com.ali.animeapi;

import com.ali.animeapi.models.Episode;
import com.ali.animeapi.models.Info;
import com.ali.animeapi.models.Recent;
import com.ali.animeapi.models.Search;
import com.ali.animeapi.sources.AnimeSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class AnimeApiApplication {

	private final AnimeSource animeSource = new AnimeSource();

	public static void main(String[] args) {
		SpringApplication.run(AnimeApiApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}

	@GetMapping("/anime/sub")
	public List<Recent> sub(@RequestParam String q, @RequestParam String site) {
		return animeSource.getRecent("sub", q, site);
	}

	@GetMapping("/anime/popular")
	public List<Recent> popular(@RequestParam String site) {
		return animeSource.getRecent("popular", null, site);
	}

	@GetMapping("/anime/movie")
	public List<Recent> movie(@RequestParam String q, @RequestParam String site) {
		return animeSource.getRecent("movie", q, site);
	}

	@GetMapping("/anime/info")
	public Info info(@RequestParam String q, @RequestParam String site) {
		return animeSource.getAnimeInfo(q, site);
	}

	@GetMapping("/anime/episode")
	public Episode episode(@RequestParam String q, @RequestParam String ep, @RequestParam String site) {
		return animeSource.getAnimeEpisode(q, ep, site);
	}

	@GetMapping("/anime/search")
	public List<Search> search(@RequestParam String q, @RequestParam String site) {
		return animeSource.getAnimeSearch(q, site);
	}

}
