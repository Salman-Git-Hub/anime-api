package com.ali.animeapi.sources.animepahe;

import com.ali.animeapi.utils.Network;
import com.ali.animeapi.utils.SourceLogger;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Logger;

public class Animepahe {


    private final Network network = new Network();
    private SourceLogger sourceLogger = new SourceLogger("AnimePahe");
    private Logger logger = sourceLogger.getLogger();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    logger.info("Network: " + chain.request().url());
                    return chain.proceed(chain.request());
                }
            })
            .build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");




}
