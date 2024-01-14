package br.com.teste.accountmanagement.enumerator;

public enum TransactionStatusEnum {
    EFETIVADO(1l, "Transação Efetivada"),
    ANULADO(2l, "Transação Anulada");

    private final Long code;
    private final String description;

    TransactionStatusEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
