package br.com.teste.accountmanagement.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {

    private PageableResponseDTO _pageable;
    private List<T> _content;
}
