# MiniRedis 🚀

An in-memory key-value cache system in Java with LRU eviction, TTL support, and real-time performance metrics.

![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## Features

- **⚡ O(1) Operations** - Fast get/put/delete using HashMap + Doubly Linked List
- **🔄 LRU Eviction** - Automatically removes least recently used items when full
- **⏰ TTL Support** - Set expiration time for cache entries
- **📊 Real-time Metrics** - Track hits, misses, hit rate, and evictions
- **💻 Interactive CLI** - Redis-like command interface
- **🔒 Thread-Safe** - Synchronized operations for concurrent access

## Quick Start

# Clone and compile
git clone https://github.com/yourusername/MiniRedis.git
cd MiniRedis
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="com.miniredis.Main"## Usage

miniredis> SET 1 100 0              # Store key=1, value=100, no expiry
OK

miniredis> SET 2 200 5000           # Store with 5-second TTL
OK (expires in 5000ms)

miniredis> GET 1                    # Retrieve value
100

miniredis> GET 999                  # Key not found
(nil)

miniredis> METRICS                  # View performance stats
=== Cache Metrics ===
Capacity:         10
Current Size:     2
Hits:             1
Misses:           1
Hit Rate:         50.00%
Evictions:        0## Commands

| Command | Description | Example |
|---------|-------------|---------|
| `SET <key> <value> <ttl>` | Store key-value (ttl in ms, 0=no expiry) | `SET 1 100 0` |
| `GET <key>` | Retrieve value | `GET 1` |
| `DEL <key>` | Delete key | `DEL 1` |
| `METRICS` | Show statistics | `METRICS` |
| `CLEAR` | Clear all entries | `CLEAR` |
| `EXIT` | Exit application | `EXIT` |


