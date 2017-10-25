package uk.gov.ea.datareturns.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The {@link SpringApplicationContextProvider} class provides access to the spring {@link ApplicationContext} from outside of the spring
 * managed scope.
 *
 * Not recommended - use only where absolutely necessary!
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SpringApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    /**
     * Retrieve the spring {@link ApplicationContext}
     *
     * @return the spring {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }
}
