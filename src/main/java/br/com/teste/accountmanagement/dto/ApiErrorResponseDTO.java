package br.com.teste.accountmanagement.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class ApiErrorResponseDTO {
    private final String timestamp = Instant.now().toString();
    private HttpStatus httpStatus;
    private int errorCode;
    private String title;
    private String message;
    private Object details;

    public ApiErrorResponseDTO(HttpStatus httpStatus, int errorCode, String title, String message, Object details) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.title = title;
        this.message = message;
        this.details = details;
    }
}
