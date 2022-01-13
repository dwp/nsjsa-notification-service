package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import uk.gov.dwp.jsa.security.JWTConfigurer;
import uk.gov.dwp.jsa.security.TokenProvider;

/**
 * Spring Boot Security Configuration. {@inheritDoc}
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    private final Environment environment;

    public SecurityConfig(final TokenProvider tokenProvider, final Environment environment) {
        this.tokenProvider = tokenProvider;
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void configure(final HttpSecurity http)
            throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/prometheus").permitAll()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/**").denyAll()
                .antMatchers("/**").authenticated()
                .and()
                .apply(securityConfigurerAdapter());
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider, environment);
    }
}
