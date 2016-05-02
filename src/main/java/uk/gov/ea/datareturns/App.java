package uk.gov.ea.datareturns;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
public class App {
    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	SpringApplication.run(App.class, args);
    }

    
    @Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public LocalValidatorFactoryBean validator() {
    	return new LocalValidatorFactoryBean();
    }
}