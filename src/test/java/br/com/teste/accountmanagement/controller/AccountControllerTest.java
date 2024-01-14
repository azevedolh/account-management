package br.com.teste.accountmanagement.controller;

import br.com.teste.accountmanagement.dto.request.CreateAccountRequestDTO;
import br.com.teste.accountmanagement.dto.response.AccountResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageResponseDTO;
import br.com.teste.accountmanagement.dto.response.PageableResponseDTO;
import br.com.teste.accountmanagement.service.AccountService;
import br.com.teste.accountmanagement.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    AccountService accountService;

    @InjectMocks
    AccountController controller;

    MockMvc mockMvc;

    @BeforeEach()
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testShouldReturnStatusOkAndAPageableWhenGetIsCalled() throws Exception {
        PageableResponseDTO pageable = TestUtils.generatePageable();
        AccountResponseDTO accountResponse = AccountResponseDTO.builder().id(1L).build();
        PageResponseDTO response = PageResponseDTO.builder()
                ._pageable(pageable)
                ._content(Arrays.asList(accountResponse))
                .build();

        when(accountService.getAccounts(anyLong(), anyInt(), anyInt(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/1/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._pageable._limit", equalTo(10)))
                .andExpect(jsonPath("$._pageable._offset", equalTo(0)))
                .andExpect(jsonPath("$._pageable._pageNumber", equalTo(1)))
                .andExpect(jsonPath("$._pageable._pageElements", equalTo(1)))
                .andExpect(jsonPath("$._pageable._totalPages", equalTo(1)))
                .andExpect(jsonPath("$._pageable._totalElements", equalTo(1)))
                .andExpect(jsonPath("$._pageable._moreElements", equalTo(false)))
                .andExpect(jsonPath("$._content", hasSize(1)));
    }

    @Test
    void testShouldReturnStatusCreatedWhenCorrectCallIsMadeToPostMethod() throws Exception {
        AccountResponseDTO response = AccountResponseDTO.builder().id(1L).build();
        CreateAccountRequestDTO request = CreateAccountRequestDTO.builder()
                .agency("1234")
                .balance(new BigDecimal(10))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        when(accountService.create(any(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers/1/accounts")
                        .characterEncoding("UTF-8")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }
}