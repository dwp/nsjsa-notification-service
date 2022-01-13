package uk.gov.dwp.jsa.notification.service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MailClaimProgressRequest {
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    private String progressTemplateId;
    private String progressEmailAddress;
    private Map<String, String> personalisation;
    private UUID progressReference;
    private String progressEmailToReplyTo;

    public MailClaimProgressRequest(final MailClaimProgressRequestBuilder builder) {
        this.personalisation = new HashMap<>();
        this.progressTemplateId = builder.templateId;
        this.progressEmailAddress = builder.emailAddress;
        this.personalisation.put(FIRST_NAME, builder.firstName);
        this.personalisation.put(LAST_NAME, builder.lastName);
        this.progressEmailToReplyTo = builder.emailReplyToId;
        this.progressReference = builder.reference;
    }

    public String getTemplateId() {
        return progressTemplateId;
    }

    public String getProgressEmailAddress() {
        return progressEmailAddress;
    }

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public UUID getProgressReference() {
        return progressReference;
    }

    public String getProgressEmailToReplyTo() {
        return progressEmailToReplyTo;
    }

    public static class MailClaimProgressRequestBuilder {
        private String templateId;
        private String emailAddress;
        private String firstName;
        private String lastName;
        private UUID reference;
        private String emailReplyToId;

        public MailClaimProgressRequestBuilder(final String templateId, final String emailAddress,
                                               final String firstName,
                                               final String lastName, final UUID reference) {
            this.templateId = templateId;
            this.emailAddress = emailAddress;
            this.firstName = firstName;
            this.lastName = lastName;
            this.reference = reference;
        }

        public MailClaimProgressRequestBuilder withEmailReplyToId(final String emailReplyToId) {
            this.emailReplyToId = emailReplyToId;
            return this;
        }

        public MailClaimProgressRequest build() {
            return new MailClaimProgressRequest(this);
        }
    }
}
