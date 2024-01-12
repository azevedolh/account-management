package br.com.teste.accountmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequestDTO {

    @NotNull
    private Long destination;

    @NotNull
    private BigDecimal amount;
}
