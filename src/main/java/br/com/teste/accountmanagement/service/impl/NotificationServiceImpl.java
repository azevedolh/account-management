package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.NotificationRequestDTO;
import br.com.teste.accountmanagement.dto.response.NotificationResponseDTO;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.service.NotificationService;
import br.com.teste.accountmanagement.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService {

    private RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String NOTIFICATION_SERVICE_URL;

    @Autowired
    public NotificationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(Long customerId, String message) throws CustomBusinessException {
        NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder()
                .sendTo(customerId)
                .message(message)
                .build();

        ResponseEntity<NotificationResponseDTO> response =
                restTemplate.postForEntity(
                        NOTIFICATION_SERVICE_URL,
                        notificationRequestDTO,
                        NotificationResponseDTO.class
                );

        if (response.getStatusCode() != HttpStatus.OK) {
            log.info("erro no envio da notificação. userId: " + customerId + ", mensagem: " + message);
            String errorMessage = MessageUtil.getMessage("notification.send.error");
            String errorDetails = MessageUtil.getMessage("notification.send.details", customerId.toString());
            throw new CustomBusinessException(errorMessage, errorDetails);
        }

        log.info("SUCESSO NO ENVIO DA NOTIFICACAO: userId: " + customerId + ", mensagem: " + message);
    }
}
