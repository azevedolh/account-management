package br.com.teste.accountmanagement.util;

import br.com.teste.accountmanagement.config.MessageSourceConfig;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

    public static String getMessage(String message, String... dynamicValues) {
        return MessageSourceConfig.messageSource().getMessage(message, dynamicValues, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String message, Locale locale, String... dynamicValues) {
        return MessageSourceConfig.messageSource().getMessage(message, dynamicValues, locale);
    }
}
