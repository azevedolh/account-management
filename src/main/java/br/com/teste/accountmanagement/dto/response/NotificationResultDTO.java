package br.com.teste.accountmanagement.dto.response;

import br.com.teste.accountmanagement.enumerator.NotificationAccountTypeEnum;
import br.com.teste.accountmanagement.enumerator.NotificationStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResultDTO {
    private NotificationAccountTypeEnum accountType;
    private NotificationStatusEnum notificationStatus;
    private String message;
}
