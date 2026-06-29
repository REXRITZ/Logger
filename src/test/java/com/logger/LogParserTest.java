package com.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.logger.model.LogEntry;
import com.logger.model.RequestType;
import com.logger.model.ResponseType;
import com.logger.parser.LogParser;

public class LogParserTest {

    @Test
    void parseLogEntry_Success() {
        LogParser parser = new LogParser();

        final String testLog = "198.51.100.12 [2026-06-26T16:06:59.534169212Z] POST \"/checkout\" 401\n";

        LogEntry actualEntry = parser.parseLogEntry(testLog);

        LogEntry expectedEntry = new LogEntry(
            "198.51.100.12",
            Instant.parse("2026-06-26T16:06:59.534169212Z"),
            RequestType.POST,
            "/checkout",
            ResponseType.UNAUTHORIZED
        );

        assertEquals(expectedEntry, actualEntry);
    }

    @Test
    void parseLogEntry_ShouldThrowException_Fail() {
        LogParser parser = new LogParser();
        final String testLog = "This is an invalid log!";
        assertThrows(IllegalArgumentException.class, () -> parser.parseLogEntry(testLog));
    }
}
