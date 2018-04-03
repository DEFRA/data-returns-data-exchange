package uk.gov.defra.datareturns.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The {@link SpringApplicationContextProvider} class provides access to the spring {@link ApplicationContext} from outside of the spring
 * managed scope.
 * <p>
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

    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    @Override
    public void setApplicationContext(final ApplicationContext ctx) {
        context = ctx;
    }
}
