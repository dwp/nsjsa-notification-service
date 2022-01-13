package uk.gov.dwp.jsa.notification.service.services.evidence;

import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PreviousWork;

import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static uk.gov.dwp.jsa.notification.service.services.evidence.PaymentFrequency.FORTNIGHTLY;
import static uk.gov.dwp.jsa.notification.service.services.evidence.PaymentFrequency.FOURWEEKLY;
import static uk.gov.dwp.jsa.notification.service.services.evidence.PaymentFrequency.MONTHLY;
import static uk.gov.dwp.jsa.notification.service.services.evidence.PaymentFrequency.WEEKLY;

@Component
public class EvidenceFactory {

    public Evidence create(final Circumstances circumstances, final BankDetails bankDetails) {
        final boolean juryService = hasBeenOnJuryService(circumstances);
        final boolean shortTermPaidJobs = hasShortTermPaidWork(circumstances);
        final boolean longTermPaidJobs = hasLongTermPaidWork(circumstances);
        final boolean p45 = hasPreviousWork(circumstances);
        final boolean futurePayments = hasFuturePayments(circumstances);
        final boolean pensions = hasPensions(circumstances);
        final boolean claimStartDateInPast = hasClaimStartDateInPast(circumstances);
        final boolean bankDetailsNotProvided = hasBankDetailsNotProvided(bankDetails);
        final boolean primaryId = true;
        final boolean secondryId = true;

        return new Evidence(juryService,
                            shortTermPaidJobs,
                            longTermPaidJobs,
                            p45,
                            futurePayments,
                            pensions,
                            claimStartDateInPast,
                            bankDetailsNotProvided,
                            primaryId,
                            secondryId);
    }

    private boolean hasFuturePayments(final Circumstances circumstances) {
        List<PreviousWork> previousWorkList = circumstances.getPreviousWork();
        return previousWorkList.stream()
                .anyMatch(t -> TRUE.equals(t.isPaymentExpected()));
    }

    private boolean hasPreviousWork(final Circumstances circumstances) {
        return !circumstances.getPreviousWork().isEmpty();
    }

    private boolean hasLongTermPaidWork(final Circumstances circumstances) {
        List<CurrentWork> currentWorkList = circumstances.getCurrentWork();
        return currentWorkList.stream()
                .anyMatch(t -> MONTHLY.name().equals(t.getPaymentFrequency())
                        || FOURWEEKLY.name().equals(t.getPaymentFrequency()));
    }

    private boolean hasShortTermPaidWork(final Circumstances circumstances) {
        List<CurrentWork> currentWorkList = circumstances.getCurrentWork();
        return currentWorkList.stream()
                .anyMatch(t -> WEEKLY.name().equals(t.getPaymentFrequency())
                        || FORTNIGHTLY.name().equals(t.getPaymentFrequency()));
    }

    private boolean hasBeenOnJuryService(final Circumstances circumstances) {
        return !Objects.isNull(circumstances.getJuryService());
    }

    private boolean hasPensions(final Circumstances circumstances) {
        boolean hasCurrentPension = !circumstances.getPensions().getCurrent().isEmpty();
        boolean hasDeferredPension = !circumstances.getPensions().getDeferred().isEmpty();
        boolean hasFuturePension = !circumstances.getPensions().getFuture().isEmpty();
        return hasCurrentPension || hasDeferredPension || hasFuturePension;
    }

    private boolean hasClaimStartDateInPast(final Circumstances circumstances) {
        return circumstances.getClaimStartDate().isBefore(circumstances.getDateOfClaim());
    }

    private boolean hasBankDetailsNotProvided(final BankDetails bankDetails) {
        return Objects.isNull(bankDetails);
    }
}
