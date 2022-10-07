package com.ali.animeapi.sources.animepahe.parser;

import java.util.List;

public class RecentResponseData {

    private List<ResponseDataItems> data;
    private String next_page_url;

    public RecentResponseData(List<ResponseDataItems> data, String next_page_url) {
        this.data = data;
        this.next_page_url = next_page_url;
    }

    public List<ResponseDataItems> getData() {
        return data;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public static class ResponseDataItems {
        private String session;
        private String snapshot;
        private String anime_title;
        private String created_at;
        private String episode;

        public ResponseDataItems(String session, String snapshot, String anime_title, String created_at, String episode) {
            this.session = session;
            this.snapshot = snapshot;
            this.anime_title = anime_title;
            this.created_at = created_at;
            this.episode = episode;
        }

        public String getSession() {
            return session;
        }

        public String getSnapshot() {
            return snapshot;
        }

        public String getAnime_title() {
            return anime_title;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getEpisode() {
            return episode;
        }
    }

}
