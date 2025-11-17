package com.miniredis.cache;

public class Node {

    int key;
    int value;
    Node prev;
    Node next;
    long expiryTime;
    public Node(int key, int value)
    {
        this.key=key;
        this.value=value;
        this.expiryTime= Long.MAX_VALUE;
    }
}
