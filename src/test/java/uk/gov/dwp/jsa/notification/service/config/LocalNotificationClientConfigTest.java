package uk.gov.dwp.jsa.notification.service.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.service.notify.NotificationClient;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocalNotificationClientConfigTest {

    private static final String API_KEY = "API_KEY";
    private static final String API_BASE_NOTIFICATION_URL = "https://localhost";

    @Mock
    private NotificationProperties notificationProperties;

    private LocalNotificationClientConfig config;
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
        config = new LocalNotificationClientConfig();
        when(notificationProperties.getApiKey()).thenReturn(API_KEY);
        when(notificationProperties.getNotificationUrl()).thenReturn(API_BASE_NOTIFICATION_URL);
    }

    private void whenIGetTheNotificationClient() {
        notificationClient = config.notificationClient(notificationProperties);
    }

    private void thenTheClientIsCreated() {
        assertThat(notificationClient.getApiKey(), is(API_KEY));
        assertThat(notificationClient.getBaseUrl(), is(API_BASE_NOTIFICATION_URL));
    }

}
