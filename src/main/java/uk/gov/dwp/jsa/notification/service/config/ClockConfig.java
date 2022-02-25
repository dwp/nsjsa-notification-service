package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;


/**
 * Configuration for a clock instance.
 *
 * Having a clock instance in the application allows tests to easily inject their own fixed dates and times.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
