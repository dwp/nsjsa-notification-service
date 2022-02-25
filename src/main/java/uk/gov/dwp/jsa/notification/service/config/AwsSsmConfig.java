package uk.gov.dwp.jsa.notification.service.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Property placeholder for AWS SSM values.
 */
@Profile({"aws", "local", "local_test"})
@Configuration
public class AwsSsmConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSsmConfig.class);

    @Value("${notification.aws.ssm.region}")
    private String region;

    @Value("${notification.aws.ssm.host}")
    private String ssmHost;

    @Bean
    public AWSSimpleSystemsManagement ssmClient() {
        final AWSSimpleSystemsManagementClientBuilder builder = AWSSimpleSystemsManagementClientBuilder.standard();

        //If the host is blank then just set the region, else set the host to allow local stack setup
        if (StringUtils.isBlank(ssmHost)) {
            LOGGER.debug("AWS SSM host is blank, setting only region to {}", region);
            builder.setRegion(region);
        } else {
            LOGGER.debug("Setting AWS SSM host to {}", ssmHost);
            builder.withRequestHandlers(new RequestHandler2() { });
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                    ssmHost, region));
            builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")));
        }
        return builder.build();
    }
}
