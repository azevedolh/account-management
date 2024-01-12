package br.com.teste.accountmanagement.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewTransactionResponseDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String status;
    private List<NotificationResultDTO> notificationResult;
}
