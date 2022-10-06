package com.ali.animeapi.sources.animixplay.parser;

import java.util.List;

public class RecentResponseData {
    private List<ResponseDataItems> result;
    private String last;

    public RecentResponseData(List<ResponseDataItems> result, String last) {
        this.result = result;
        this.last = last;
    }

    public List<ResponseDataItems> getResult() {
        return result;
    }

    public String getLast() {
        return last;
    }


    public static class ResponseDataItems {
        private String title;
        private String url;
        private String picture;
        private String infotext;
        private String timetop;

        public ResponseDataItems(String title, String url, String picture, String infotext, String timetop) {
            this.title = title;
            this.url = url;
            this.picture = picture;
            this.infotext = infotext;
            this.timetop = timetop;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getPicture() {
            return picture;
        }

        public String getInfotext() {
            return infotext;
        }

        public String getTimetop() {
            return timetop;
        }


    }

}

