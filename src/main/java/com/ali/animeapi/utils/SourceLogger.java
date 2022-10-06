package com.ali.animeapi.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class SourceLogger {

    private String sourceName;
    private Logger logger;
    private FileHandler fileHandler;

    public SourceLogger(String sourceName) {
        this.sourceName = sourceName;
        this.logger = Logger.getLogger(sourceName);
        try {
            this.fileHandler = new FileHandler(String.format("log/%s.log", sourceName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
