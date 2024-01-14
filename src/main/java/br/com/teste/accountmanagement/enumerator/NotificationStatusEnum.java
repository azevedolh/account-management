package br.com.teste.accountmanagement.enumerator;

public enum NotificationStatusEnum {
    SENT(1l, "SUCESSO"),
    ERROR(2l, "ERRO");

    private final Long code;
    private final String description;

    public String getDescription() {
        return description;
    }

    NotificationStatusEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
