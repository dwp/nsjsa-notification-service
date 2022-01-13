package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.gov.service.notify.NotificationClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

@Configuration
@Profile("aws")
public class NotificationClientConfig {
    @Bean
    public NotificationClient notificationClient(final NotificationProperties notificationProperties) {
        final SocketAddress socketAddress =
                new InetSocketAddress(
                        notificationProperties.getProxyHost(),
                        Integer.parseInt(notificationProperties.getProxyPort()));
        final Proxy proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
        return new NotificationClient(
                notificationProperties.getApiKey(),
                notificationProperties.getNotificationUrl(),
                proxy);
    }

}
