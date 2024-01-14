package br.com.teste.accountmanagement.exception;

import br.com.teste.accountmanagement.dto.response.ApiErrorResponseDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({CustomBusinessException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleCustomException(CustomBusinessException ex, WebRequest request) {
        ApiErrorResponseDTO apiErrorResponseDTO = new ApiErrorResponseDTO(
                ex.getHttpStatus() == null ? HttpStatus.BAD_REQUEST : ex.getHttpStatus(),
                1000,
                "ERROR",
                ex.getLocalizedMessage(),
                ex.getDetails()
        );

        return new ResponseEntity<>(apiErrorResponseDTO, new HttpHeaders(), apiErrorResponseDTO.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleException(MethodArgumentNotValidException ex, WebRequest request) {
        ApiErrorResponseDTO apiErrorResponseDTO = new ApiErrorResponseDTO(
                ex.getStatusCode() == null ? HttpStatus.BAD_REQUEST : (HttpStatus) ex.getStatusCode(),
                2000,
                "ERROR",
                ex.getBody().getDetail(),
                ex.getDetailMessageArguments()
        );

        return new ResponseEntity<>(apiErrorResponseDTO, new HttpHeaders(), apiErrorResponseDTO.getHttpStatus());
    };
}
