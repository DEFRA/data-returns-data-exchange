package uk.gov.defra.datareturns.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


/**
 * JSR-303 Validator configuration
 *
 * @author Sam Gardner-Dell
 */
@Configuration
public class ValidatorConfiguration {

    /**
     * Bean record for hibernate validator.
     *
     * @return a singleton instance of the {@link LocalValidatorFactoryBean} to be shared application-wide.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Validator validator() {
        final LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(validationMessageSource());
        return validatorFactoryBean;
    }

    /**
     * Configure default message sources for validation
     *
     * @return a preconfigured {@link MessageSource} for use with validation
     */
    @Bean
    public MessageSource validationMessageSource() {
        final ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        bean.setBasenames("classpath:messages", "classpath:ValidationMessages");
        bean.setDefaultEncoding("UTF-8");
        return bean;
    }


    /**
     * Configure default message sources for validation
     *
     * @return a preconfigured {@link MessageSource} for use with validation
     */
    @Bean
    public MessageSourceAccessor validationMessageSourceAccessor() {
        return new MessageSourceAccessor(validationMessageSource());
    }
}
