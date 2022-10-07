package com.ali.animeapi.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SourceLogger {

    private String sourceName;
    private Logger logger;
    private FileHandler fileHandler;
    private SimpleFormatter formatter;

    public SourceLogger(String sourceName) {
        this.sourceName = sourceName;
        this.logger = Logger.getLogger(sourceName);
        try {
            this.fileHandler = new FileHandler(String.format("log/%s.log", sourceName));
            this.logger.addHandler(this.fileHandler);
            this.formatter = new SimpleFormatter();
            this.fileHandler.setFormatter(this.formatter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
