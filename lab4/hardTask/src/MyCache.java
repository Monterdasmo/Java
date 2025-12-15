import java.util.HashMap;
import java.util.Map;

/**
 * MyCache - Кеш з підтримкою expiry та eviction
 * Hard Task - Завдання 3
 */
public class MyCache<K, V> {
    
    private static class CacheEntry<K, V> {
        K key;
        V value;
        long creationTime;
        long expirationTime;
        long lastAccessTime;
        int accessCount;
        CacheEntry<K, V> prev;
        CacheEntry<K, V> next;

        CacheEntry(K key, V value, long ttl) {
            if (key == null || value == null) {
                throw new NullPointerException("Cache keys and values must not be null");
            }
            this.key = key;
            this.value = value;
            this.creationTime = System.currentTimeMillis();
            this.lastAccessTime = this.creationTime;
            this.expirationTime = ttl > 0 ? this.creationTime + ttl : Long.MAX_VALUE;
            this.accessCount = 0;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        void updateAccess() {
            this.lastAccessTime = System.currentTimeMillis();
            this.accessCount++;
        }
    }

    public enum EvictionPolicy {
        LRU,   // Least Recently Used
        LFU,   // Least Frequently Used
        FIFO   // First In First Out
    }

    private final Map<K, CacheEntry<K, V>> cacheMap;
    private final int maxSize;
    private final long defaultTtl;
    private final EvictionPolicy evictionPolicy;
    private CacheEntry<K, V> head;
    private CacheEntry<K, V> tail;

    public MyCache(int maxSize, long defaultTtlMillis, EvictionPolicy evictionPolicy) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        this.maxSize = maxSize;
        this.defaultTtl = defaultTtlMillis;
        this.evictionPolicy = evictionPolicy;
        this.cacheMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    public MyCache() {
        this(100, 0, EvictionPolicy.LRU);
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Cache keys and values must not be null");
        }

        removeExpiredEntries();

        if (cacheMap.containsKey(key)) {
            CacheEntry<K, V> entry = cacheMap.get(key);
            entry.value = value;
            entry.updateAccess();
            moveToHead(entry);
            return;
        }

        if (cacheMap.size() >= maxSize) {
            evict();
        }

        CacheEntry<K, V> newEntry = new CacheEntry<>(key, value, defaultTtl);
        cacheMap.put(key, newEntry);
        addToHead(newEntry);
    }

    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("Cache keys must not be null");
        }

        CacheEntry<K, V> entry = cacheMap.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            remove(key);
            return null;
        }

        entry.updateAccess();
        if (evictionPolicy == EvictionPolicy.LRU) {
            moveToHead(entry);
        }

        return entry.value;
    }

    public V remove(K key) {
        if (key == null) return null;

        CacheEntry<K, V> entry = cacheMap.remove(key);
        if (entry != null) {
            removeFromList(entry);
            return entry.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) return false;
        CacheEntry<K, V> entry = cacheMap.get(key);
        if (entry != null && entry.isExpired()) {
            remove(key);
            return false;
        }
        return entry != null;
    }

    public int size() {
        removeExpiredEntries();
        return cacheMap.size();
    }

    public void clear() {
        cacheMap.clear();
        head = null;
        tail = null;
    }

    private void removeExpiredEntries() {
        cacheMap.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                removeFromList(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private void evict() {
        if (cacheMap.isEmpty()) return;

        CacheEntry<K, V> entryToEvict = null;

        switch (evictionPolicy) {
            case LRU:
                entryToEvict = tail;
                break;

            case LFU:
                int minAccessCount = Integer.MAX_VALUE;
                for (CacheEntry<K, V> entry : cacheMap.values()) {
                    if (entry.accessCount < minAccessCount) {
                        minAccessCount = entry.accessCount;
                        entryToEvict = entry;
                    }
                }
                break;

            case FIFO:
                long oldestTime = Long.MAX_VALUE;
                for (CacheEntry<K, V> entry : cacheMap.values()) {
                    if (entry.creationTime < oldestTime) {
                        oldestTime = entry.creationTime;
                        entryToEvict = entry;
                    }
                }
                break;
        }

        if (entryToEvict != null) {
            remove(entryToEvict.key);
        }
    }

    private void addToHead(CacheEntry<K, V> entry) {
        entry.next = head;
        entry.prev = null;

        if (head != null) {
            head.prev = entry;
        }

        head = entry;

        if (tail == null) {
            tail = entry;
        }
    }

    private void moveToHead(CacheEntry<K, V> entry) {
        if (entry == head) return;

        removeFromList(entry);
        addToHead(entry);
    }

    private void removeFromList(CacheEntry<K, V> entry) {
        if (entry.prev != null) {
            entry.prev.next = entry.next;
        } else {
            head = entry.next;
        }

        if (entry.next != null) {
            entry.next.prev = entry.prev;
        } else {
            tail = entry.prev;
        }
    }

    @Override
    public String toString() {
        return "MyCache{size=" + size() + "/" + maxSize + ", policy=" + evictionPolicy + "}";
    }
}
