package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * Property configuration holder for AWS SSM.
 */
@Configuration
@ConfigurationProperties(prefix = "notification.aws.ssm")
public class NotificationAwsSsmProperties {
    private String host;
    private String prefix;
    private String region;
    private String dailyClaimStatsMailingListKey;

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getDailyClaimStatsMailingListKey() {
        return dailyClaimStatsMailingListKey;
    }

    public void setDailyClaimStatsMailingListKey(final String dailyClaimStatsMailingListKey) {
        this.dailyClaimStatsMailingListKey = dailyClaimStatsMailingListKey;
    }
}
