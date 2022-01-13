package uk.gov.dwp.jsa.notification.service.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.service.notify.NotificationClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NotificationClientConfigTest {
    private static final String API_KEY = "API_KEY";
    private static final String NOTIFICATION_URL = "NOTIFICATION_URL";
    private static final String PROXY_HOST = "HOST";
    private static final String PROXY_PORT = "1234";
    @Mock
    private NotificationProperties notificationProperties;

    private NotificationClientConfig config;
    private NotificationClient notificationClient;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void createsNotificationClientBean() {
        givenAConfig();
        whenIGetTheNotificationClient();
        thenTheClientIsCreated();
    }

    private void givenAConfig() {
        config = new NotificationClientConfig();
        when(notificationProperties.getApiKey()).thenReturn(API_KEY);
        when(notificationProperties.getProxyHost()).thenReturn(PROXY_HOST);
        when(notificationProperties.getProxyPort()).thenReturn(PROXY_PORT);
        when(notificationProperties.getNotificationUrl()).thenReturn(NOTIFICATION_URL);
    }

    private void whenIGetTheNotificationClient() {
        notificationClient = config.notificationClient(notificationProperties);
    }

    private void thenTheClientIsCreated() {
        assertThat(notificationClient.getApiKey(), is(API_KEY));
        assertThat(notificationClient.getBaseUrl(), is(NOTIFICATION_URL));
        assertThat(notificationClient.getProxy(), is(expectedProxy()));
    }

    private Proxy expectedProxy() {
        SocketAddress socketAddress =
                new InetSocketAddress(PROXY_HOST, Integer.parseInt(PROXY_PORT));
        return new Proxy(Proxy.Type.HTTP, socketAddress);
    }

}
