import java.util.HashMap;
import java.util.Map;

/**
 * MyCache - кеш з підтримкою expiry та eviction.
 * Підтримує політики: LRU / LFU / FIFO.
 * Не підтримує null ключі/значення.
 */
public class MyCache<K, V> {

    static final class CacheEntry<K, V> {
        final K key;
        V value;

        final long creationTime;
        final long expirationTime;

        long lastAccessTime;
        int accessCount;

        CacheEntry(K key, V value, long ttlMillis) {
            if (key == null || value == null) {
                throw new NullPointerException("Cache keys and values must not be null");
            }
            this.key = key;
            this.value = value;

            long now = System.currentTimeMillis();
            this.creationTime = now;
            this.lastAccessTime = now;
            this.expirationTime = ttlMillis > 0 ? now + ttlMillis : Long.MAX_VALUE;
            this.accessCount = 0;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        void onAccess() {
            this.lastAccessTime = System.currentTimeMillis();
            this.accessCount++;
        }
    }

    public enum EvictionPolicy {
        LRU,   // Least Recently Used
        LFU,   // Least Frequently Used
        FIFO   // First In First Out
    }

    private final Map<K, CacheEntry<K, V>> map;
    private final int maxSize;
    private final long defaultTtlMillis;
    private final EvictionPolicy policy;

    public MyCache(int maxSize, long defaultTtlMillis, EvictionPolicy policy) {
        if (maxSize <= 0) throw new IllegalArgumentException("Max size must be positive");
        if (policy == null) throw new NullPointerException("EvictionPolicy must not be null");

        this.maxSize = maxSize;
        this.defaultTtlMillis = defaultTtlMillis;
        this.policy = policy;
        this.map = new HashMap<>();
    }

    public MyCache() {
        this(100, 0, EvictionPolicy.LRU);
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Cache keys and values must not be null");
        }

        removeExpiredEntries();

        CacheEntry<K, V> existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            existing.onAccess();
            return;
        }

        if (map.size() >= maxSize) {
            evictOne();
        }

        map.put(key, new CacheEntry<>(key, value, defaultTtlMillis));
    }

    public V get(K key) {
        if (key == null) throw new NullPointerException("Cache key must not be null");

        CacheEntry<K, V> entry = map.get(key);
        if (entry == null) return null;

        if (entry.isExpired()) {
            map.remove(key);
            return null;
        }

        entry.onAccess();
        return entry.value;
    }

    public V remove(K key) {
        if (key == null) return null;
        CacheEntry<K, V> removed = map.remove(key);
        return removed == null ? null : removed.value;
    }

    public boolean containsKey(K key) {
        if (key == null) return false;

        CacheEntry<K, V> entry = map.get(key);
        if (entry == null) return false;

        if (entry.isExpired()) {
            map.remove(key);
            return false;
        }
        return true;
    }

    public int size() {
        removeExpiredEntries();
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    private void removeExpiredEntries() {
        map.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    private void evictOne() {
        if (map.isEmpty()) return;

        CacheEntry<K, V> victim = null;

        switch (policy) {
            case LRU:
                long oldestAccess = Long.MAX_VALUE;
                for (CacheEntry<K, V> e : map.values()) {
                    if (e.lastAccessTime < oldestAccess) {
                        oldestAccess = e.lastAccessTime;
                        victim = e;
                    }
                }
                break;

            case LFU:
                int minCount = Integer.MAX_VALUE;
                long oldestAmongMin = Long.MAX_VALUE;
                for (CacheEntry<K, V> e : map.values()) {
                    // tie-breaker: oldest access time
                    if (e.accessCount < minCount || (e.accessCount == minCount && e.lastAccessTime < oldestAmongMin)) {
                        minCount = e.accessCount;
                        oldestAmongMin = e.lastAccessTime;
                        victim = e;
                    }
                }
                break;

            case FIFO:
                long oldestCreate = Long.MAX_VALUE;
                for (CacheEntry<K, V> e : map.values()) {
                    if (e.creationTime < oldestCreate) {
                        oldestCreate = e.creationTime;
                        victim = e;
                    }
                }
                break;
        }

        if (victim != null) {
            map.remove(victim.key);
        }
    }

    @Override
    public String toString() {
        return "MyCache{size=" + size() + "/" + maxSize + ", policy=" + policy + "}";
    }
}
