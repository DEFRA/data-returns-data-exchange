package uk.gov.defra.datareturns.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Spring security configuration
 *
 * @author Sam Gardner-Dell
 */
// FIXME: Prototype code - need to implement production ruleset
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityConfiguration {
    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };

    /**
     * Run the code associated with the given {@link Runnable} as a system user (effectively bypassing all security - use with caution!)
     *
     * @param invocable the {@link Runnable} to invoke without security - usually a lambda function.
     */
    public static void runAsSystemUser(final Runnable invocable) {
        final Authentication preInvocationAuthentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            final Authentication authentication = new PreAuthenticatedAuthenticationToken("system", null,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")));
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            invocable.run();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(preInvocationAuthentication);
        }
    }

    /**
     * Method-level security configuration
     *
     * @author Sam Gardner-Dell
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            final CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
            expressionHandler.setPermissionEvaluator(new PermissionEvaluatorImpl());
            return expressionHandler;
        }
//
//        @Override
//        public MethodSecurityMetadataSource methodSecurityMetadataSource() {
//            return super.methodSecurityMetadataSource();
//        }
    }

    /**
     * Web security configuration
     *
     * @author Sam Gardner-Dell
     */
    @Configuration
    @ConditionalOnWebApplication
    class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    //.antMatchers("/api/**").hasAnyRole("USER").anyRequest()
                    .antMatchers("/api/**").fullyAuthenticated().anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic();
        }

        @Inject
        public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("user").password("password").roles("USER")
                    .and()
                    .withUser("super").password("password").roles("USER", "SUPER_USER")
                    .and()
                    .withUser("admin").password("password").roles("USER", "SUPER_USER", "ADMIN");
        }
    }

    /**
     * Permission evaluator
     *
     * @author Sam Gardner-Dell
     */
    class PermissionEvaluatorImpl implements PermissionEvaluator {
        @Override
        public boolean hasPermission(final Authentication authentication, final Object targetDomainObject, final Object permission) {
            return true;
        }

        @Override
        public boolean hasPermission(final Authentication authentication, final Serializable targetId, final String targetType,
                                     final Object permission) {
            return true;
        }
    }

    class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
        private Object filterObject;
        private Object returnObject;
        private Object target;

        public CustomMethodSecurityExpressionRoot(final Authentication authentication) {
            super(authentication);
        }

        public boolean isMember(final Long id) {
            return true;
        }

        public boolean startsWithP(final String name) {
            return name.toLowerCase().startsWith("p");
        }

        @Override
        public Object getFilterObject() {
            return filterObject;
        }

        @Override
        public void setFilterObject(final Object filterObject) {
            this.filterObject = filterObject;
        }

        @Override
        public Object getReturnObject() {
            return returnObject;
        }

        @Override
        public void setReturnObject(final Object returnObject) {
            this.returnObject = returnObject;
        }

        public Object getThis() {
            return target;
        }

        public void setThis(final Object newTarget) {
            this.target = newTarget;
        }
    }

    public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
        private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        @Override
        protected MethodSecurityExpressionOperations createSecurityExpressionRoot(final Authentication authentication, final
        MethodInvocation invocation) {
            final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
            root.setThis(invocation.getThis());
            root.setPermissionEvaluator(getPermissionEvaluator());
            root.setTrustResolver(this.trustResolver);
            root.setRoleHierarchy(getRoleHierarchy());
            return root;
        }
    }
}
