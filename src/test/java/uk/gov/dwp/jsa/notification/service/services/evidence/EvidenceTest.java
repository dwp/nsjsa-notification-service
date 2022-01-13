package uk.gov.dwp.jsa.notification.service.services.evidence;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EvidenceTest {
    public static final boolean JURY_SERVICE = true;
    public static final boolean SHORT_TERM_PAID_JOBS = true;
    public static final boolean LONG_TERM_PAID_JOBS = true;
    public static final boolean P_45 = true;
    public static final boolean FUTURE_PAYMENTS = true;
    public static final boolean PENSIONS = true;
    public static final boolean CLAIM_START_DATE_IN_PAST = true;
    public static final boolean BANK_DETAILS_NOT_PROVIDED = true;
    public static final boolean PRIMARY_ID = true;
    public static final boolean SECONDRY_ID = true;

    @Test
    public void constructorSetsFields() {
        final Evidence evidence = new Evidence(
                JURY_SERVICE,
                SHORT_TERM_PAID_JOBS,
                LONG_TERM_PAID_JOBS,
                P_45,
                FUTURE_PAYMENTS,
                PENSIONS,
                CLAIM_START_DATE_IN_PAST,
                BANK_DETAILS_NOT_PROVIDED,
                PRIMARY_ID,
                SECONDRY_ID);
        assertThat(evidence.isJuryService(), is(JURY_SERVICE));
        assertThat(evidence.isShortTermPaidJobs(), is(SHORT_TERM_PAID_JOBS));
        assertThat(evidence.isLongTermPaidJobs(), is(LONG_TERM_PAID_JOBS));
        assertThat(evidence.isP45(), is(P_45));
        assertThat(evidence.isFuturePayments(), is(FUTURE_PAYMENTS));
        assertThat(evidence.isPensions(), is(PENSIONS));
        assertThat(evidence.isPensions(), is(CLAIM_START_DATE_IN_PAST));
        assertThat(evidence.isPensions(), is(BANK_DETAILS_NOT_PROVIDED));
        assertThat(evidence.isPrimaryId(), is(PRIMARY_ID));
        assertThat(evidence.isSecondryId(), is(SECONDRY_ID));
    }
}
