package uk.gov.dwp.jsa.notification.service.services;

import uk.gov.dwp.jsa.adaptors.http.api.SubmittedClaimsTally;
import uk.gov.dwp.jsa.notification.service.utils.SimpleI8NDateFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MailCountRequest {

    public static final String TALLY_DATE = "tallyDate";
    public static final String ONLINE_CLAIM_COUNT = "onlineClaimCount";
    public static final String ASSISTED_DIGITAL_CLAIM_COUNT = "assistedDigitalClaimCount";
    public static final String TOTAL_COUNT = "totalCount";

    private String templateId;
    private String emailAddress;
    private Map<String, String> personalisation;
    private UUID reference;
    private String emailReplyToId;

    public MailCountRequest(final MailCountRequestBuilder builder) {
        this.personalisation = new HashMap<>();
        this.templateId = builder.templateId;
        this.emailAddress = builder.emailAddress;
        this.personalisation.put(TALLY_DATE,
                new SimpleI8NDateFormat(Locale.getDefault()).format(builder.submittedClaimsTally.getTallyDate()));
        this.personalisation.put(ONLINE_CLAIM_COUNT,
            String.valueOf(
                Optional.ofNullable(builder.submittedClaimsTally.getOnlineClaimCount()).orElse(0L)));
        this.personalisation.put(ASSISTED_DIGITAL_CLAIM_COUNT,
            String.valueOf(
                Optional.ofNullable(builder.submittedClaimsTally.getAssistedDigitalClaimCount()).orElse(0L)));
        this.personalisation.put(TOTAL_COUNT,
                String.valueOf(Optional.ofNullable(builder.submittedClaimsTally.getTotalCount()).orElse(0L)));
        this.reference = builder.reference;
        this.emailReplyToId = builder.emailReplyToId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public UUID getReference() {
        return reference;
    }

    public String getEmailReplyToId() {
        return emailReplyToId;
    }

    public static class MailCountRequestBuilder {
        private String templateId;
        private String emailAddress;
        private SubmittedClaimsTally submittedClaimsTally;
        private UUID reference;
        private String emailReplyToId;

        public MailCountRequestBuilder(final String templateId, final String emailAddress,
                                       final SubmittedClaimsTally submittedClaimsTally, final UUID reference) {
            this.templateId = templateId;
            this.emailAddress = emailAddress;
            this.submittedClaimsTally = submittedClaimsTally;
            this.reference = reference;
        }

        public MailCountRequestBuilder withEmailReplyToId(final String emailReplyToId) {
            this.emailReplyToId = emailReplyToId;
            return this;
        }

        public MailCountRequest build() {
            return new MailCountRequest(this);
        }
    }
}
