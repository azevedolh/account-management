package br.com.teste.accountmanagement.integration;

import br.com.teste.accountmanagement.dto.request.CreateCustomerRequestDTO;
import br.com.teste.accountmanagement.exception.CustomBusinessException;
import br.com.teste.accountmanagement.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomExceptionHandlerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @InjectMocks
    ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    void testShouldReturnCorrectErrorStructureWhenCustomBusinessExceptionThrown() throws Exception {
        CreateCustomerRequestDTO requestDTO = CreateCustomerRequestDTO.builder()
                .name("teste")
                .document("123456789")
                .documentType("ERROR")
                .address("Rua do teste")
                .password("1234567")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        String jsonResponse = mockMvc.perform(post("/api/v1/customers")
                        .characterEncoding("UTF-8")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof CustomBusinessException,
                        "Should be true"
                ))
                .andReturn()
                .getResponse()
                .getContentAsString();

        validateStructure(jsonResponse);
    }

    @Test
    void testShouldReturnCorrectErrorStructureWhenMethodArgumentNotValidExceptionThrown() throws Exception {
        CreateCustomerRequestDTO requestDTO = CreateCustomerRequestDTO.builder()
                .document("123456789")
                .documentType("PF")
                .address("Rua do teste")
                .password("1234567")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDTO);
        String jsonResponse = mockMvc.perform(post("/api/v1/customers")
                        .characterEncoding("UTF-8")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof MethodArgumentNotValidException,
                        "Should be true"
                ))
                .andReturn()
                .getResponse()
                .getContentAsString();

        validateStructure(jsonResponse);
    }

    private void validateStructure(String jsonResponse) {
        assertTrue(jsonResponse.contains("timestamp"), "should be true");
        assertTrue(jsonResponse.contains("httpStatus"), "should be true");
        assertTrue(jsonResponse.contains("errorCode"), "should be true");
        assertTrue(jsonResponse.contains("title"), "should be true");
        assertTrue(jsonResponse.contains("message"), "should be true");
        assertTrue(jsonResponse.contains("details"), "should be true");
    }
}
