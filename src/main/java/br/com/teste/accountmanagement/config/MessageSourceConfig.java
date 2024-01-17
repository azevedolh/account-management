package br.com.teste.accountmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    private static final String DEFAULT_BUNDLE_PATH = "classpath:messages/messages";
    private static final String DEFAULTS_PATH = "classpath:messages/defaults";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Bean
    public static ReloadableResourceBundleMessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames(DEFAULT_BUNDLE_PATH, DEFAULTS_PATH);
        messageSource.setDefaultEncoding(DEFAULT_ENCODING);

        return messageSource;
    }
}
