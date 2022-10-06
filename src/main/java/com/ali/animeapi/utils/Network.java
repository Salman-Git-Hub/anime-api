package com.ali.animeapi.utils;

import okhttp3.*;

public class Network {

    public Request POST(String url, Headers header, RequestBody body) {
        if (body == null) {
            body = new FormBody.Builder().build();
        }
        if (header == null) {
            header = new Headers.Builder().build();
        }
        return new Request.Builder()
                .url(url)
                .headers(header)
                .post(body)
                .build();
    }

    public Request GET(String url, Headers header) {
        if (header == null) {
            header = new Headers.Builder().build();
        }

        return new Request.Builder()
                .url(url)
                .headers(header)
                .build();

    }



}
