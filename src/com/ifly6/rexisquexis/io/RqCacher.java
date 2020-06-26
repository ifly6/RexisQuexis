package com.ifly6.rexisquexis.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RqCacher {

    private Path cacheLocation;
    private Map<String, String> cache;

    public RqCacher() {
        this(Paths.get("./cache.json"));
    }

    public RqCacher(Path p) {
        cacheLocation = p;
        try {
            if (cacheLocation.toFile().length() == 0)
                throw new IOException("Cache is empty");

            Type type = new TypeToken<Map<String, String>>() { // this doesn't make sense to me but whatever
            }.getType();
            cache = new Gson().fromJson(Files.newBufferedReader(cacheLocation), type);

        } catch (IOException e) {
            System.out.println("Cannot find cache. Loading empty cache.");
            cache = new HashMap<>();
        }
    }

    public boolean contains(String k) {
        return cache.containsKey(k);
    }

    public String get(String k) {
        return cache.get(k);
    }

    public void update(String url, String response) {
        cache.put(url, response);
    }

    public void save() throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = Files.newBufferedWriter(cacheLocation);
        try {
            g.toJson(cache, writer);
        } finally {
            writer.flush(); // required to complete writing
            writer.close();
        }
    }
}
