package uk.gov.ea.datareturns.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

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
        return new LocalValidatorFactoryBean();
    }
}
