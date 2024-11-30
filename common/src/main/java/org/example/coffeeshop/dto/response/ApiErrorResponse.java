package org.example.coffeeshop.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiErrorResponse {

    private int code;
    private String message;
    private String path;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiErrorResponse(int code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
    }
}
