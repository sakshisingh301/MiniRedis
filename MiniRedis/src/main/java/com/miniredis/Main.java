package com.miniredis;


import com.miniredis.cache.LRUCache;
import com.miniredis.cli.CommandProcessor;

import java.util.Scanner;

public class Main {

    private static final int DEFAULT_CAPACITY = 10;
    public static void main(String[] args) {

        LRUCache cache = new LRUCache(DEFAULT_CAPACITY);
        CommandProcessor cli = new CommandProcessor(cache);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       MiniRedis Cache System          ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Cache Capacity: " + DEFAULT_CAPACITY);
        System.out.println("Type HELP for available commands\n");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("miniredis> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String command = scanner.nextLine();
            String response = cli.process(command);

            if ("EXIT".equals(response)) {
                System.out.println("\nGoodbye! 👋");
                break;
            }

            System.out.println(response);
        }

        scanner.close();





    }
}

