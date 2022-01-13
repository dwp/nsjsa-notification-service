package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.dwp.jsa.notification.service.AppInfo;


@Component
public class WithVersionUriComponentsBuilder extends ServletUriComponentsBuilder {

    private final AppInfo appInfo;

    public static final String VERSION_SPEL = "#{appInfo.version}";

    @Autowired
    public WithVersionUriComponentsBuilder(final AppInfo pAppInfo) {
        this.appInfo = pAppInfo;
    }

    private WithVersionUriComponentsBuilder(final WithVersionUriComponentsBuilder self) {
        this(self.appInfo);
    }

    @Override
    public UriComponentsBuilder path(final String pPath) {
        if (appInfo != null && appInfo.getVersion() != null) {
            super.path(pPath.replace(VERSION_SPEL, appInfo.getVersion()));
        } else {
            super.path(pPath);
        }
        return this;
    }

    @Override
    public ServletUriComponentsBuilder cloneBuilder() {
        return new WithVersionUriComponentsBuilder(this);
    }
}
