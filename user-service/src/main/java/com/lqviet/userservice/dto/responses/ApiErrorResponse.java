package com.lqviet.userservice.dto.responses;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
}
