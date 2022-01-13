package uk.gov.dwp.jsa.notification.service.services.evidence;

public class Evidence {
    private boolean juryService;
    private boolean shortTermPaidJobs;
    private boolean longTermPaidJobs;
    private boolean p45;
    private boolean futurePayments;
    private boolean pensions;
    private boolean primaryId;
    private boolean secondryId;
    private boolean claimStartDateInPast;
    private boolean bankDetailsNotProvided;

    public Evidence() {
    }

    public Evidence(
            final boolean juryService,
            final boolean shortTermPaidJobs,
            final boolean longTermPaidJobs,
            final boolean p45,
            final boolean futurePayments,
            final boolean pensions,
            final boolean claimStartDateInPast,
            final boolean bankDetailsNotProvided,
            final boolean primaryId,
            final boolean secondryId) {
        this.juryService = juryService;
        this.shortTermPaidJobs = shortTermPaidJobs;
        this.longTermPaidJobs = longTermPaidJobs;
        this.p45 = p45;
        this.futurePayments = futurePayments;
        this.pensions = pensions;
        this.claimStartDateInPast = claimStartDateInPast;
        this.bankDetailsNotProvided = bankDetailsNotProvided;
        this.primaryId = primaryId;
        this.secondryId = secondryId;
    }

    public boolean isJuryService() {
        return juryService;
    }

    public boolean isShortTermPaidJobs() {
        return shortTermPaidJobs;
    }

    public boolean isLongTermPaidJobs() {
        return longTermPaidJobs;
    }

    public boolean isP45() {
        return p45;
    }

    public boolean isFuturePayments() {
        return futurePayments;
    }

    public boolean isPensions() {
        return pensions;
    }

    public boolean isClaimStartDateInPast() {
        return claimStartDateInPast;
    }

    public boolean isBankDetailsNotProvided() {
        return bankDetailsNotProvided;
    }

    public boolean isPrimaryId() {
        return primaryId;
    }

    public boolean isSecondryId() {
        return secondryId;
    }
}
