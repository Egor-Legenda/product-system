package com.example.productsystem.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    /* Вспомогательный метод для создания стандартизированного ответа об ошибке */
    public static Object createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }
}
