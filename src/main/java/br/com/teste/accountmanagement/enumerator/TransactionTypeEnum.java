package br.com.teste.accountmanagement.enumerator;

public enum TransactionTypeEnum {
    DEBITO(1l, "Débito"),
    CREDITO(2l, "Crédito");

    private final Long code;
    private final String description;

    public Long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    TransactionTypeEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
