package com.ali.animeapi.sources.animepahe.parser;

import java.util.List;

public class EpisodeListData {

    private List<EpisodeDataItem> data;
    private String next_page_url;

    public EpisodeListData(List<EpisodeDataItem> data, String next_page_url) {
        this.data = data;
        this.next_page_url = next_page_url;
    }

    public List<EpisodeDataItem> getData() {
        return data;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public static class EpisodeDataItem {
        private String created_at;
        private String episode;
        private String session;
        private String snapshot;

        public EpisodeDataItem(String created_at, String episode, String session, String snapshot) {
            this.created_at = created_at;
            this.episode = episode;
            this.session = session;
            this.snapshot = snapshot;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getEpisode() {
            return episode;
        }

        public String getSession() {
            return session;
        }

        public String getSnapshot() {
            return snapshot;
        }
    }


}
