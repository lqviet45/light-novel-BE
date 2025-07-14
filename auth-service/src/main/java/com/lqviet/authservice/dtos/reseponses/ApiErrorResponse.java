package com.lqviet.authservice.dtos.reseponses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Map<String, String> fieldErrors;
    private String details;
    private String path;
    private LocalDateTime timestamp;
}
