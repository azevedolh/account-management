package br.com.teste.accountmanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomBusinessException extends RuntimeException {
    private HttpStatus httpStatus;
    private Object details;

    public CustomBusinessException(String msg) {
        this(msg, msg);
    }

    public CustomBusinessException(String msg, Object details) {
        super(msg);
        this.details = details;
    }

    public CustomBusinessException(HttpStatus httpStatus, String msg) {
        this(httpStatus, msg, msg);
    }

    public CustomBusinessException(HttpStatus httpStatus, String msg, Object details) {
        super(msg);
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public CustomBusinessException(String msg, Exception e) {
        super(msg, e);
    }
}
