package org.example.tdlsb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private String error;
    private String path;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String message, int status, String path) {
        return ErrorResponse.builder().message(message)
                .status(status)
                .error(String.valueOf(status))
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
