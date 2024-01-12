package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.NotificationResponseDTO;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.service.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public void sendNotification(Long customerId) throws CustomBusinessException {
        ResponseEntity<NotificationResponseDTO> response =
                restTemplate.getForEntity(
                        "https://run.mocky.io/v3/9769bf3a-b0b6-477a-9ff5-91f63010c9d3",
                        NotificationResponseDTO.class
                );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomBusinessException("erro no envio da notificação.", "userId: " + customerId);
        }

        log.info("SUCESSO NO ENVIO DA NOTIFICACAO: " + response.getBody().toString());
    }
}
