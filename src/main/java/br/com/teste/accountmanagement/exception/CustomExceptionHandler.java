package br.com.teste.accountmanagement.exception;

import br.com.teste.accountmanagement.dto.ApiErrorResponseDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({CustomBusinessException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleException(CustomBusinessException ex, WebRequest request) {
        ApiErrorResponseDTO apiErrorResponseDTO = new ApiErrorResponseDTO(
                ex.getHttpStatus() == null ? HttpStatus.BAD_REQUEST : ex.getHttpStatus(),
                1000,
                ex.getLocalizedMessage(),
                null,
                ex.getDetails()
        );

        return new ResponseEntity<>(apiErrorResponseDTO, new HttpHeaders(), apiErrorResponseDTO.getHttpStatus());
    }
}
