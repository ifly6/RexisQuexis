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

/**
 * Caches URLs (as {@code String}) and responses. Cache is used to massively speed up IO operations instead of having to
 * abide by rate limits and slow Internet transfer speeds. Data is serialised to disc using {@code Gson} in Json.
 */
public class RqCacher {

    final private Path cacheLocation;
    private Map<String, String> cache;

    /**
     * Creates cache at default location {@code "./cache.json"}. See {@link #RqCacher(Path)}
     */
    public RqCacher() {
        this(Paths.get("./cache.json"));
    }

    /**
     * Creates cache with file pointer provided. If there is no file at that location or if the file is empty, a new
     * cache empty cache is initialised; it will be saved at the location specified.
     * @param p file pointer
     * @throws CacheLoadException on {@link IOException} thrown from Gson
     */
    public RqCacher(Path p) {
        cacheLocation = p;
        if (Files.exists(p) && cacheLocation.toFile().length() != 0) {
            try {
                Type type = new MapTypeToken().getType();
                cache = new Gson().fromJson(Files.newBufferedReader(cacheLocation), type);

            } catch (IOException e) {
                throw new CacheLoadException("Encountered IO exception when attempting cache load", e);
            }
        } else {
            // System.out.println("Cannot find cache. Loading empty cache.");
            cache = new HashMap<>();
        }
    }

    /**
     * @param lookup key
     * @return whether key is contained in cache
     */
    public boolean contains(String lookup) {
        return cache.containsKey(lookup);
    }

    /**
     * @param lookup key
     * @return stored response
     */
    public String get(String lookup) {
        return cache.get(lookup);
    }

    /**
     * Updates cache with provided URL and response
     * @param url      as look-up key
     * @param response to store
     */
    public void update(String url, String response) {
        cache.put(url, response);
    }

    /**
     * Saves cache to previously specified location
     * @throws IOException on save error
     */
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

    private static class MapTypeToken extends TypeToken<Map<String, String>> {
    }

    public static class CacheLoadException extends RuntimeException {
        public CacheLoadException(String message) {
            super(message);
        }

        public CacheLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
