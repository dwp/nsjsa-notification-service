package uk.gov.dwp.jsa.notification.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppInfo {

    private final String version;

    public AppInfo(@Value("${app.version}") final String version) {
        if (StringUtils.isEmpty(version)) {
            this.version = "unknown";
        } else {
            this.version = "v" + version;
        }
    }

    public String getVersion() {
        return this.version;
    }
}
