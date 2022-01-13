package uk.gov.dwp.jsa.notification.service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MailClaimSuccessRequest {
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    private String successTemplateId;
    private String successEmailAddress;
    private Map<String, String> personalisation;
    private UUID successReference;
    private String successEmailToReplyTo;

    public MailClaimSuccessRequest(final MailClaimSuccessRequestBuilder builder) {
        this.personalisation = new HashMap<>();
        this.successTemplateId = builder.templateId;
        this.successEmailAddress = builder.emailAddress;
        this.personalisation.put(FIRST_NAME, builder.firstName);
        this.personalisation.put(LAST_NAME, builder.lastName);
        this.successEmailToReplyTo = builder.emailReplyToId;
        this.successReference = builder.reference;
    }

    public String getTemplateId() {
        return successTemplateId;
    }

    public String getSuccessEmailAddress() {
        return successEmailAddress;
    }

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public UUID getSuccessReference() {
        return successReference;
    }

    public String getSuccessEmailToReplyTo() {
        return successEmailToReplyTo;
    }

    public static class MailClaimSuccessRequestBuilder {
        private String templateId;
        private String emailAddress;
        private String firstName;
        private String lastName;
        private UUID reference;
        private String emailReplyToId;

        public MailClaimSuccessRequestBuilder(final String templateId, final String emailAddress,
                                               final String firstName,
                                               final String lastName, final UUID reference) {
            this.templateId = templateId;
            this.emailAddress = emailAddress;
            this.firstName = firstName;
            this.lastName = lastName;
            this.reference = reference;
        }

        public MailClaimSuccessRequestBuilder withEmailReplyToId(final String emailReplyToId) {
            this.emailReplyToId = emailReplyToId;
            return this;
        }

        public MailClaimSuccessRequest build() {
            return new MailClaimSuccessRequest(this);
        }
    }

}
