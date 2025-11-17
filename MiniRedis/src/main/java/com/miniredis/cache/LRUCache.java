package com.miniredis.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    private int capacity;
    HashMap<Integer, Node> map = new HashMap<>();
    Node head;
    Node tail;
    private long hits = 0;
    private long misses = 0;
    private long evictions = 0;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if(!map.containsKey(key)) {
            misses++;
            return -1;
        }

        Node node = map.get(key);

        // Check if the key value pair is expired, remove the expired node
        if(node.expiryTime > 0 && System.currentTimeMillis() > node.expiryTime) {
            // Remove the node
            removeNode(node);
            map.remove(key);
            misses++;
            return -1;
        }

        hits++;
        removeNode(node);
        addNodeAtTheFront(node);
        return node.value;
    }

    private void addNodeAtTheFront(Node node) {
        Node nextNode = head.next;
        node.next = nextNode;
        nextNode.prev = node;
        head.next = node;
        node.prev = head;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    public void put(int key, int value, int ttl) {
        // Calculate expiry time
        long expiryTime = 0;
        if(ttl > 0) {
            expiryTime = System.currentTimeMillis() + ttl;
        }

        // If the key is already present, remove the old node
        if (map.containsKey(key)) {
            Node oldNode = map.get(key);
            removeNode(oldNode);
            map.remove(key);
        }

        // Remove LRU if at capacity
        if (map.size() == capacity) {
            Node lessRecentlyUsed = tail.prev;
            removeNode(lessRecentlyUsed);
            map.remove(lessRecentlyUsed.key);
            evictions++;
        }

        // Create new node and add to front
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        newNode.expiryTime = expiryTime;
        addNodeAtTheFront(newNode);
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        long totalRequests=hits+misses;
        //out of all requests, how many were served from the cache
        double hitRate = totalRequests > 0 ? (double) hits / totalRequests * 100 : 0.0;
        double missRate = totalRequests > 0 ? (double) misses / totalRequests * 100 : 0.0;
        //lets say (total capacity=100 && usage= 15% and eviction==0 then your cache is too big)
        // total capacity= 100 && usage==100 && eviction==500 then your cache is too small
        double usagePercent = (double) map.size() / capacity * 100;


        metrics.put("capacity", capacity);
        metrics.put("currentSize", map.size());
        metrics.put("hits", hits);
        metrics.put("misses", misses);
        metrics.put("hitRate", hitRate);
        metrics.put("evictions", evictions);
        metrics.put("missRate", missRate);
        metrics.put("totalRequests", totalRequests);
        metrics.put("usagePercentage", usagePercent);

        return metrics;
    }

    /**
     * Delete a key from cache
     * @param key The key to delete
     * @return true if deleted, false if not found
     */
    public boolean delete(int key) {
        if (!map.containsKey(key)) {
            return false;
        }

        Node node = map.get(key);
        removeNode(node);
        map.remove(key);
        return true;
    }

    /**
     * Clear all entries from cache
     */
    public void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
        // reset metrics:
        hits = 0;
        misses = 0;
        evictions = 0;
    }
}
