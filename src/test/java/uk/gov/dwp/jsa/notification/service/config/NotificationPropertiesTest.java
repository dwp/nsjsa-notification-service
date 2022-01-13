package uk.gov.dwp.jsa.notification.service.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local_test")
public class NotificationPropertiesTest {

    @Autowired
    private NotificationProperties notificationProperties;

    @Test
    public void testPropertiesShouldNotBeNull() {
        assertNotNull("Should not be null", notificationProperties.getApiKey());
        assertNotNull("Should not be null", notificationProperties.getSmsTemplateIdEnglish());
        assertNotNull("Should not be null", notificationProperties.getSmsTemplateIdWelsh());
        assertNotNull("Should not be null", notificationProperties.getMailTemplateIdEnglish());
        assertNotNull("Should not be null", notificationProperties.getMailTemplateIdWelsh());
        assertNotNull("Should not be null", notificationProperties.getMailCountAddress());
        assertNotNull("Should not be null", notificationProperties.getMailCountTemplateId());
        assertNotNull("Should not be null", notificationProperties.getNotificationUrl());
        assertNotNull("Should not be null", notificationProperties.getProxyHost());
        assertNotNull("Should not be null", notificationProperties.getProxyPort());
        assertNotNull("Should not be null", notificationProperties.getMailStatsTemplateId());
        assertNotNull("Should not be null", notificationProperties.getMailStatsAddress());
        assertNotNull("Should not be null", notificationProperties.getMailProgressTemplateIdEnglish());
        assertNotNull("Should not be null", notificationProperties.getMailProgressTemplateIdWelsh());
        assertNotNull("Should not be null", notificationProperties.getSmsProgressTemplateIdEnglish());
        assertNotNull("Should not be null", notificationProperties.getSmsProgressTemplateIdWelsh());
    }
}
