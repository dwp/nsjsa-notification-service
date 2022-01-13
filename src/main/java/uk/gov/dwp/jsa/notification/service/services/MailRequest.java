package uk.gov.dwp.jsa.notification.service.services;

import uk.gov.dwp.jsa.notification.service.services.evidence.Evidence;
import uk.gov.dwp.jsa.notification.service.utils.SimpleI8NDateFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MailRequest {

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String CLAIM_SUBMITTED_DATE = "claimSubmittedDate";
    public static final String JURY_SERVICE = "juryService";
    public static final String CURRENT_WORK_WEEKLY = "currentWorkWeekly";
    public static final String CURRENT_WORK_MONTHLY = "currentWorkMonthly";
    public static final String PREVIOUS_EMPLOYMENT = "previousEmployment";
    public static final String PREVIOUS_EMPLOYMENT_EXPECTING = "previousEmploymentExpecting";
    public static final String ANY_PENSION = "anyPension";
    public static final String BANK_DETAILS_NOT_PROVIDED = "bankDetails";
    public static final String CLAIM_START_DATE_IN_PAST = "claimStartDateInPast";
    public static final String CLAIM_START_DATE = "claimStartDate";
    public static final String ANY_SELECTED = "anySelected";

    private String templateId;
    private String emailAddress;
    private Map<String, String> personalisation;
    private UUID reference;
    private String emailReplyToId;

    public MailRequest(final MailRequestBuilder builder) {
        this.personalisation = new HashMap<>();
        this.templateId = builder.templateId;
        this.emailAddress = builder.emailAddress;
        this.personalisation.put(FIRST_NAME, builder.firstName);
        this.personalisation.put(LAST_NAME, builder.lastName);
        this.personalisation.put(CLAIM_SUBMITTED_DATE,
                new SimpleI8NDateFormat(Locale.getDefault()).format(builder.claimSubmittedDate));
        this.personalisation.put(JURY_SERVICE, String.valueOf(builder.evidence.isJuryService()));
        this.personalisation.put(CURRENT_WORK_WEEKLY, String.valueOf(builder.evidence.isShortTermPaidJobs()));
        this.personalisation.put(CURRENT_WORK_MONTHLY, String.valueOf(builder.evidence.isLongTermPaidJobs()));
        this.personalisation.put(PREVIOUS_EMPLOYMENT, String.valueOf(builder.evidence.isP45()));
        this.personalisation.put(PREVIOUS_EMPLOYMENT_EXPECTING, String.valueOf(builder.evidence.isFuturePayments()));
        this.personalisation.put(ANY_PENSION, String.valueOf(builder.evidence.isPensions()));
        this.personalisation.put(CLAIM_START_DATE_IN_PAST, String.valueOf(builder.evidence.isClaimStartDateInPast()));
        this.personalisation.put(CLAIM_START_DATE,
                new SimpleI8NDateFormat(Locale.getDefault()).format(builder.claimStartDate));
        this.personalisation.put(BANK_DETAILS_NOT_PROVIDED,
                String.valueOf(builder.evidence.isBankDetailsNotProvided()));
        this.personalisation.put(ANY_SELECTED, String.valueOf(isAnySelected(builder.evidence)));
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

    private boolean isAnySelected(final Evidence evidence) {
        List<Boolean> list = Arrays.asList(
                evidence.isJuryService(), evidence.isShortTermPaidJobs(),
                evidence.isLongTermPaidJobs(), evidence.isP45(),
                evidence.isFuturePayments(), evidence.isPensions(),
                evidence.isClaimStartDateInPast(), evidence.isBankDetailsNotProvided());
        return list.contains(true);
    }

    public static class MailRequestBuilder {
        private String templateId;
        private String emailAddress;
        private String firstName;
        private String lastName;
        private LocalDate claimSubmittedDate;
        private LocalDate claimStartDate;
        private Evidence evidence;
        private UUID reference;
        private String emailReplyToId;

        public MailRequestBuilder(final String templateId, final String emailAddress,
                                  final String firstName, final String lastName, final LocalDate claimSubmittedDate,
                                  final LocalDate claimStartDate, final Evidence evidence,
                                  final UUID reference) {
            this.templateId = templateId;
            this.emailAddress = emailAddress;
            this.firstName = firstName;
            this.lastName = lastName;
            this.claimSubmittedDate = claimSubmittedDate;
            this.claimStartDate = claimStartDate;
            this.evidence = evidence;
            this.reference = reference;
        }

        public MailRequestBuilder withEmailReplyToId(final String emailReplyToId) {
            this.emailReplyToId = emailReplyToId;
            return this;
        }

        public MailRequest build() {
            return new MailRequest(this);
        }
    }
}
