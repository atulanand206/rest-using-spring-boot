package com.atul.gitbook.learn.users.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UserError {

    public static final String ISO8601PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

    @JsonProperty("status")
    private int fStatus;

    @JsonProperty("error")
    private String fError;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = ISO8601PATTERN, timezone = "UTC")
    private Instant fTimestamp;

    @JsonProperty("path")
    private String fPath;

    @JsonProperty("message")
    private String fMessage;

    public UserError(int status, String error, Instant timestamp, String path, String message) {
        this.fStatus = status;
        this.fError = error;
        this.fTimestamp = timestamp;
        this.fPath = path;
        this.fMessage = message;
    }
}
