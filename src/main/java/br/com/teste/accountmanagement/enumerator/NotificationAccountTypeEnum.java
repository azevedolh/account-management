package br.com.teste.accountmanagement.enumerator;

public enum NotificationAccountTypeEnum {
    ORIGIN(1l, "ORIGEM"),
    DESTINATION(2l, "DESTINO");

    private final Long code;
    private final String description;

    NotificationAccountTypeEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
