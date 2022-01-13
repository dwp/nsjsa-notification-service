package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.gov.service.notify.NotificationClient;

@Configuration
@Profile("!aws")
public class LocalNotificationClientConfig {
    @Bean
    public NotificationClient notificationClient(final NotificationProperties notificationProperties) {
        return new NotificationClient(notificationProperties.getApiKey(), notificationProperties.getNotificationUrl());
    }

}
