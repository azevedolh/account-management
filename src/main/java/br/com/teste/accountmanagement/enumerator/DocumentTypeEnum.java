package br.com.teste.accountmanagement.enumerator;

public enum DocumentTypeEnum {
    PF(1l, "Pessoa Fisica"),
    PJ(2l, "Pessoa Juridica");

    private final Long code;
    private final String description;

    public Long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    DocumentTypeEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
