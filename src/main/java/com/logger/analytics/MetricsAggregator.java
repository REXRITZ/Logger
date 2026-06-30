package com.logger.analytics;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.logger.model.LogEntry;
import com.logger.model.ResponseType;
import com.logger.parser.LogParser;

public class MetricsAggregator {
    
    private final LogParser logParser;

    public MetricsAggregator() {
        logParser = new LogParser();
    }

    public void analyzeLogs(String path) {
        long start = System.nanoTime();
        Path filePath = Paths.get(path);
        Map<ResponseType,Integer> codeFreq = new HashMap<>();
        try(Stream<String> lines = Files.lines(filePath)){
            lines.forEach(line -> {
                LogEntry entry = logParser.parseLogEntry(line);
                codeFreq.merge(entry.statusCode(), 1, Integer::sum);
            });
        } catch(IOException e) {
            throw new UncheckedIOException("An error occurred reading logs from path: " + path, e);
        }
        long elapsedNanos = System.nanoTime() - start;
        double elapsedMillis = elapsedNanos / 1_000_000.0;

        int success = 0, clientSideFailed = 0, serverSideFailed = 0, invalid = 0, total = 0;
        for(Entry<ResponseType,Integer> entry : codeFreq.entrySet()) {
            switch(entry.getKey()) {
                case SUCCESS:
                case CREATED:
                    success += entry.getValue();
                    break;
                case NOT_FOUND:
                case UNAUTHORIZED:
                case BAD_REQUEST:
                    clientSideFailed += entry.getValue();
                    break;
                case INTERNAL_SERVER_ERROR:
                    serverSideFailed += entry.getValue();
                    break;
                case UNKNOWN:
                default:
                    invalid += entry.getValue();
            }
        }
        total = success + clientSideFailed + serverSideFailed + invalid;

        System.out.printf("%nExecution time: %.3f ms%n", elapsedMillis);
        System.out.printf("%-20s %5s%n", "STATUS", "COUNT");
        System.out.println("-------------------- -----");
        System.out.printf("%-20s %5d%n", "TOTAL", total);
        System.out.printf("%-20s %5d%n", "SUCCESS", success);
        System.out.printf("%-20s %5d%n", "CLIENT_SIDE_FAIL", clientSideFailed);
        System.out.printf("%-20s %5d%n", "SERVER_SIDE_FAIL", serverSideFailed);
        System.out.printf("%-20s %5d%n", "INVALID", invalid);
    }

}
