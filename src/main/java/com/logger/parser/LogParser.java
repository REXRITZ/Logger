package com.logger.parser;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.logger.model.LogEntry;
import com.logger.model.RequestType;
import com.logger.model.ResponseType;

public class LogParser {
    
    private final Pattern pattern;
    private final String regexp =
        "^(?<ip>\\d{1,3}(?:\\.\\d{1,3}){3})\\s" +
        "\\[(?<timestamp>[^\\]]+)\\]\\s" +
        "(?<method>GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)\\s" +
        "\"(?<endpoint>[^\"]+)\"\\s" +
        "(?<code>\\d{3})\\s*$";

    public LogParser() {
        this.pattern = Pattern.compile(regexp);
    }

    public LogEntry parseLogEntry(String logLine) {
        /*
            log entry format:
            "%s [%s] %s \"%s\" %s\n", ip, timestamp, method, endpoint, code
         */
        Matcher matcher = pattern.matcher(logLine.trim());
        
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid log entry: " + logLine);
        }
        
        return new LogEntry(
            matcher.group("ip"),
            Instant.parse(matcher.group("timestamp")),
            RequestType.valueOf(matcher.group("method")),
            matcher.group("endpoint"),
            ResponseType.parse(matcher.group("code"))
        );
    }
}
