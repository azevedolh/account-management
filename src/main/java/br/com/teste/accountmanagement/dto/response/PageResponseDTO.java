package br.com.teste.accountmanagement.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO {

    private PageableResponseDTO _pageable;
    private List<?> _content;
}
