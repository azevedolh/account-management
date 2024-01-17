package br.com.teste.accountmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    private static final String DEFAULT_LOCALE = "pt-BR";

    @Bean
    public LocaleResolver localeResolver() {
        var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag(DEFAULT_LOCALE));
        return localeResolver;
    }

}
