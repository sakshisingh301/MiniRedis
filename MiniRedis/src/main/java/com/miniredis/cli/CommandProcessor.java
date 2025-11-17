package com.miniredis.cli;

import com.miniredis.cache.LRUCache;
import java.util.Map;

public class CommandProcessor {
    private final LRUCache cache;

    public CommandProcessor(LRUCache cache) {
        this.cache = cache;
    }

    /**
     * Process a command and return response
     * @param command The command string (e.g., "SET key value 60")
     * @return Response string
     */
    public String process(String command) {
        //takes the command
        if (command == null || command.trim().isEmpty()) {
            return "ERROR: Empty command";
        }

        String[] parts = command.trim().split("\\s+");
        String cmd = parts[0].toUpperCase();
       //different methods for different commands
        try {
            switch (cmd) {
                case "SET":
                    return handleSet(parts);

                case "GET":
                    return handleGet(parts);

                case "DEL":
                case "DELETE":
                    return handleDelete(parts);

                case "METRICS":
                case "STATS":
                    return handleMetrics();

                case "CLEAR":
                    return handleClear();

                case "EXIT":
                case "QUIT":
                    return "EXIT";

                default:
                    return "ERROR: Unknown command '" + cmd + "'. Type HELP for available commands.";
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String handleSet(String[] parts) {
        //If the arguments are less than 3 then throw error (for SET we need SET <Key> <Value> [ttl])
        if (parts.length < 3) {
            return "ERROR: SET requires at least key and value.\nUsage: SET <key> <value> [ttl_milliseconds]";
        }

        try {
            int key = Integer.parseInt(parts[1]);
            int value = Integer.parseInt(parts[2]);
            int ttl = 0;  

          // Sometimes TTL is not provided, so we need to check if it is provided
            if (parts.length >= 4) {
                ttl = Integer.parseInt(parts[3]);
                if (ttl < 0) {
                    return "ERROR: TTL must be non-negative";
                }
            }

            cache.put(key, value, ttl);
            return ttl > 0 ? "OK (expires in " + ttl + "ms)" : "OK";

        } catch (NumberFormatException e) {
            return "ERROR: Key, value, and TTL must be integers";
        }
    }

    private String handleGet(String[] parts) {
        //GET <Key>
        if (parts.length < 2) {
            return "ERROR: GET requires a key.\nUsage: GET <key>";
        }

        try {
            int key = Integer.parseInt(parts[1]);
            int value = cache.get(key);

            if (value == -1) {
                return "(nil)";
            }
            return String.valueOf(value);

        } catch (NumberFormatException e) {
            return "ERROR: Key must be an integer";
        }
    }

    private String handleDelete(String[] parts) {
        if (parts.length < 2) {
            return "ERROR: DEL requires a key.\nUsage: DEL <key>";
        }

        try {
            int key = Integer.parseInt(parts[1]);
            boolean deleted = cache.delete(key);
            return deleted ? "OK: Key deleted" : "(nil) Key not found";

        } catch (NumberFormatException e) {
            return "ERROR: Key must be an integer";
        }
    }

    private String handleMetrics() {
        Map<String, Object> metrics = cache.getMetrics();

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Cache Metrics ===\n");
        sb.append(String.format("Capacity:         %d\n", metrics.get("capacity")));
        sb.append(String.format("Current Size:     %d\n", metrics.get("currentSize")));
        sb.append(String.format("Usage:            %.2f%%\n", metrics.get("usagePercentage")));
        sb.append(String.format("Total Requests:   %d\n", metrics.get("totalRequests")));
        sb.append(String.format("Hits:             %d\n", metrics.get("hits")));
        sb.append(String.format("Misses:           %d\n", metrics.get("misses")));
        sb.append(String.format("Hit Rate:         %.2f%%\n", metrics.get("hitRate")));
        sb.append(String.format("Miss Rate:        %.2f%%\n", metrics.get("missRate")));
        sb.append(String.format("Evictions:        %d\n", metrics.get("evictions")));
        sb.append("=====================\n");

        return sb.toString();
    }

    private String handleClear() {
        cache.clear();
        return "OK: Cache cleared";
    }


}