package br.com.teste.accountmanagement.enumerator;

public enum OperationEnum {
    DEBITO(1l, "Débito"),
    CREDITO(2l, "Crédito");

    private final Long code;
    private final String description;

    OperationEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
