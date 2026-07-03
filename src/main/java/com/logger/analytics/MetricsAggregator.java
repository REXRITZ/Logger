package com.logger.analytics;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import com.logger.model.LogEntry;
import com.logger.model.ResponseType;
import com.logger.parser.LogParser;

public class MetricsAggregator {
    
    private final LogParser logParser;
    private final int NUM_CORES;

    public MetricsAggregator() {
        logParser = new LogParser();
        NUM_CORES = Runtime.getRuntime().availableProcessors();
    }

    public void analyzeLogs(String path) {
        
        Path filePath = Paths.get(path);
        ExecutorService service = Executors.newFixedThreadPool(NUM_CORES);
        Map<ResponseType, Long> codeFreq = new HashMap<>();
        
        try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            long chunkSize = (fileSize + NUM_CORES - 1) / NUM_CORES;
            long seekStart = 0;
            List<Callable<Map<ResponseType, Long>>> tasks = new ArrayList<>();

            for(int cores = 0; cores < NUM_CORES; ++cores) {
                long seekEnd = (cores == NUM_CORES-1) ? fileSize : seekStart + chunkSize;
                if(seekEnd < fileSize) {
                    seekEnd = getChunkEndPosition(fileChannel, seekEnd);
                }
                long limit = seekEnd - seekStart;
                long curr = seekStart;
                tasks.add(() -> processChunk(fileChannel, curr, limit));
                seekStart = seekEnd;
            }
            List<Future<Map<ResponseType, Long>>> results = service.invokeAll(tasks);
            service.shutdown();

            for(Future<Map<ResponseType, Long>> result : results) {
                Map<ResponseType, Long> chunkResult = result.get();
                chunkResult.forEach((status, count) ->
                    codeFreq.merge(status, count, Long::sum)
                );
            }
        } catch(IOException e) {
            throw new UncheckedIOException("Failed to read the file: " + path, e);
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error during parallel log processing", e);
        }
        printResults(codeFreq);
    }

    private long getChunkEndPosition(FileChannel fileChannel, long seekStart) throws IOException {
        fileChannel.position(seekStart);
        ByteBuffer buffer = ByteBuffer.allocate(1);
        long seekEnd = seekStart;
        while(fileChannel.read(buffer) > 0) {
            byte b = buffer.get();
            buffer.clear();
            if(b == '\n') {
                seekEnd++; // includes newline as part of chunk
                break;
            }
            seekEnd++;
        }
        return seekEnd;
    }

    private Map<ResponseType, Long> processChunk(FileChannel fileChannel, long seekStart, long limit) throws IOException{
        Map<ResponseType, Long> freq = new HashMap<>();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, seekStart, limit);
        byte[] bytes = new byte[(int)limit];
        buffer.get(bytes);
        buffer.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = LogParser.parseLogEntryUsingStrings(line);
                freq.merge(entry.statusCode(), 1L, Long::sum);
            }
        }
        return freq;
    }

    private void printResults(Map<ResponseType, Long> codeFreq) {
        int success = 0, clientSideFailed = 0, serverSideFailed = 0, invalid = 0, total = 0;
        for(Entry<ResponseType,Long> entry : codeFreq.entrySet()) {
            long count = entry.getValue();
            switch(entry.getKey()) {
                case SUCCESS:
                case CREATED:
                    success += count;
                    break;
                case NOT_FOUND:
                case UNAUTHORIZED:
                case BAD_REQUEST:
                    clientSideFailed += count;
                    break;
                case INTERNAL_SERVER_ERROR:
                    serverSideFailed += count;
                    break;
                case UNKNOWN:
                default:
                    invalid += count;
            }
        }
        total = success + clientSideFailed + serverSideFailed + invalid;

        System.out.printf("%-20s %5s%n", "STATUS", "COUNT");
        System.out.println("-------------------- -----");
        System.out.printf("%-20s %5d%n", "TOTAL", total);
        System.out.printf("%-20s %5d%n", "SUCCESS", success);
        System.out.printf("%-20s %5d%n", "CLIENT_SIDE_FAIL", clientSideFailed);
        System.out.printf("%-20s %5d%n", "SERVER_SIDE_FAIL", serverSideFailed);
        System.out.printf("%-20s %5d%n", "INVALID", invalid);
    }

}
