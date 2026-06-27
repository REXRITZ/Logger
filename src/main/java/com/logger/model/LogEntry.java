package com.logger.model;

import java.time.Instant;

public record LogEntry(
    String ip,
    Instant timestamp,
    RequestType requestType,
    String endpoint,
    ResponseType statusCode
) {}
