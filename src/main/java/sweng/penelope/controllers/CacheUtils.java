package sweng.penelope.controllers;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheUtils {
    public static final String BIRDS = "birds";
    public static final String CAMPUSES = "campuses";
    public static final String CAMPUSES_LIST = "campusesList";
    public static final String ASSETS = "assets";

    private CacheUtils() {
    }

    public static void evictCache(CacheManager cacheManager, String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            if (key != null)
                cache.evictIfPresent(key);
            else
                cache.clear();
        }
    }
}
