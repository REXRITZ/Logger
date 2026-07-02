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
        "(?<method>GET|POST|PUT|DELETE)\\s" +
        "\"(?<endpoint>[^\"]+)\"\\s" +
        "(?<code>\\d{3})\\s*$";

    public LogParser() {
        this.pattern = Pattern.compile(regexp);
    }

    public LogEntry parseLogEntryUsingRegex(String logLine) {
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

    public static LogEntry parseLogEntryUsingStrings(String logLine) {
        /*
            log entry format:
            "%s [%s] %s \"%s\" %s\n", ip, timestamp, method, endpoint, code
         */

        // TODO: Add more robust failure checks
        try {
            String line = logLine.trim();

            int ipEnd = line.indexOf(' ');
            String ip = line.substring(0, ipEnd);

            int tspStart = line.indexOf('[', ipEnd) + 1;
            int tspEnd = line.indexOf(']', tspStart);
            Instant timestamp = Instant.parse(line.substring(tspStart, tspEnd));

            int mStart = tspEnd + 2;
            int mEnd = line.indexOf(' ', mStart);
            RequestType type = RequestType.valueOf(line.substring(mStart, mEnd));

            int eStart = line.indexOf('"', tspEnd) + 1;
            int eEnd = line.indexOf('"', eStart);
            String endpoint = line.substring(eStart, eEnd);

            int codeStart = eEnd + 2;
            String codeStr = line.substring(codeStart, codeStart + 3);
            ResponseType statusCode = ResponseType.parse(codeStr);

            return new LogEntry(ip, timestamp, type, endpoint, statusCode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid log entry: " + logLine);
        }
    }
}
