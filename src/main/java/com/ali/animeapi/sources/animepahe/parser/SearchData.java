package com.ali.animeapi.sources.animepahe.parser;

import java.util.List;

public class SearchData {
    private List<SearchDataItem> data;

    public SearchData(List<SearchDataItem> data) {
        this.data = data;
    }

    public List<SearchDataItem> getData() {
        return data;
    }

    public static class SearchDataItem {
        private String title;
        private String session;
        private String poster;

        public SearchDataItem(String title, String session, String poster) {
            this.title = title;
            this.session = session;
            this.poster = poster;
        }

        public String getTitle() {
            return title;
        }

        public String getSession() {
            return session;
        }

        public String getPoster() {
            return poster;
        }
    }


}
