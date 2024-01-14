package br.com.teste.accountmanagement.enumerator;

import java.util.Arrays;

public enum DocumentTypeEnum {
    PF(1l, "Pessoa Fisica"),
    PJ(2l, "Pessoa Juridica");

    private final Long code;
    private final String description;

    public static Boolean isValid(String documentType) {
        return Arrays.stream(DocumentTypeEnum.values()).anyMatch(item -> item.name().equals(documentType));
    }

    DocumentTypeEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
