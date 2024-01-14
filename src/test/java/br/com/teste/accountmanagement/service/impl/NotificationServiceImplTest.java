package br.com.teste.accountmanagement.service.impl;

import br.com.teste.accountmanagement.dto.request.NotificationRequestDTO;
import br.com.teste.accountmanagement.dto.response.NotificationResponseDTO;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void ShouldSendNotificationWhenServiceIsAvailable() {
        NotificationResponseDTO response = NotificationResponseDTO.builder().messageSent(true).build();
        when(restTemplate.postForEntity(any(String.class), any(NotificationRequestDTO.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(response));

        ReflectionTestUtils.setField(notificationService, "NOTIFICATION_SERVICE_URL", "url-test");
        notificationService.sendNotification(1L, "Mensagem de teste");

        verify(restTemplate).postForEntity(any(String.class), any(NotificationRequestDTO.class), any(Class.class));
    }

    @Test
    void ShouldThrowExceptionWhenServiceIsNotAvailable() {
        NotificationResponseDTO response = NotificationResponseDTO.builder().messageSent(true).build();
        when(restTemplate.postForEntity(any(String.class), any(NotificationRequestDTO.class), any(Class.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        ReflectionTestUtils.setField(notificationService, "NOTIFICATION_SERVICE_URL", "url-test");

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> notificationService.sendNotification(1L, "Mensagem de teste"),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("erro no envio da notificação"), "Should be true");
        verify(restTemplate).postForEntity(any(String.class), any(NotificationRequestDTO.class), any(Class.class));
    }
}