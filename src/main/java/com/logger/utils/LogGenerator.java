package com.logger.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class LogGenerator {
    
    private static final List<String> IP_ADDRS = List.of("192.168.1.1", "10.0.0.15", "172.16.254.1", "203.0.113.43", "198.51.100.12");
    private static final List<String> METHODS = List.of("GET", "POST", "PUT", "DELETE");
    private static final List<String> ENDPOINTS = List.of("/api/v1/users", "/login", "/home", "/api/v1/products", "/checkout");
    private static final List<String> STATUS_CODES = List.of("200", "201", "400", "401", "404", "500");

    public static String generateLog() {
        // TODO: Use ThreadLocalRandom for faster log data generation
        Random random = new Random();

        Instant timestamp = Instant.now().minus(random.nextInt(0, 24), ChronoUnit.HOURS);
        String ip = IP_ADDRS.get(random.nextInt(IP_ADDRS.size()));
        String method = METHODS.get(random.nextInt(METHODS.size()));
        String endpoint = ENDPOINTS.get(random.nextInt(ENDPOINTS.size()));
        String code = STATUS_CODES.get(random.nextInt(STATUS_CODES.size()));

        String entry = String.format("%s [%s] %s \"%s\" %s\n", ip, timestamp, method, endpoint, code);
        return entry;
    }

    public static void main(String args[]) {
        int entries = 1000000;
        Path path = Path.of("access.log");
        try {
            for(int i = 0; i <= entries; ++i) {
                String entry = generateLog();
                if(i == 0) {
                    Files.writeString(path, "");
                } else {
                    Files.writeString(path, entry, StandardOpenOption.APPEND);  
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
