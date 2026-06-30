package com.logger.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResponseType {
    SUCCESS("200"),
    CREATED("201"),
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    INTERNAL_SERVER_ERROR("500"),
    UNKNOWN("-1");

    private final String code;

    private static final Map<String, ResponseType> codeMap = 
            Arrays.stream(values())
                  .collect(Collectors.toMap(ResponseType::getCode, Function.identity()));

    ResponseType(String code) {
        this.code = code;
    }

    private String getCode() {
        return code;
    }

    public static ResponseType parse(String code) {
        return codeMap.getOrDefault(code, UNKNOWN);
    }
}
