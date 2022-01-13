package uk.gov.dwp.jsa.notification.service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SmsRequest {

    public static final String FIRST_NAME = "first_name";

    private String templateId;
    private String phoneNumber;
    private Map<String, String> personalisation;
    private UUID reference;
    private String smsSenderId;

    public SmsRequest(final SmsRequestBuilder builder) {
        this.personalisation = new HashMap<>();
        this.templateId = builder.templateId;
        this.phoneNumber = builder.phoneNumber;
        this.personalisation.put(FIRST_NAME, builder.firstName);
        this.reference = builder.reference;
        this.smsSenderId = builder.smsSenderId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public UUID getReference() {
        return reference;
    }

    public String getSmsSenderId() {
        return smsSenderId;
    }

    public static class SmsRequestBuilder {
        private String templateId;
        private String phoneNumber;
        private String firstName;
        private UUID reference;
        private String smsSenderId;

        public SmsRequestBuilder(final String templateId, final String phoneNumber,
                                 final String firstName, final UUID reference) {
            this.templateId = templateId;
            this.phoneNumber = phoneNumber;
            this.firstName = firstName;
            this.reference = reference;
        }

        public SmsRequestBuilder withSmsSenderId(final String smsSenderId) {
            this.smsSenderId = smsSenderId;
            return this;
        }

        public SmsRequest build() {
            return new SmsRequest(this);
        }
    }
}
