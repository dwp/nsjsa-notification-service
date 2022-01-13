package uk.gov.dwp.jsa.notification.service.services.evidence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.CurrentWork;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PensionDetail;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.PreviousWork;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EvidenceFactoryTest {

    private static final List<PensionDetail> EMPTY_CURRENT_PENSIONS = new ArrayList<>();
    private static final List<PensionDetail> EMPTY_DEFERRED_PENSIONS = new ArrayList<>();
    private static final List<PensionDetail> EMPTY_FUTURE_PENSIONS = new ArrayList<>();
    private static final PensionDetail PENSION = new PensionDetail();
    private static final List<PensionDetail> CURRENT_PENSIONS = Arrays.asList(PENSION);
    private static final PreviousWork PREVIOUS_JOB = new PreviousWork();
    private static final PreviousWork PREVIOUS_JOB_WITH_FUTURE_PAYMENT = new PreviousWork(null, false, null, null, null, null, null, null, true);
    private static final List<PreviousWork> PREVIOUS_WORK = Arrays.asList(PREVIOUS_JOB);
    private static final List<PreviousWork> PREVIOUS_WORK_WITH_FUTURE_PAYMENT = Arrays.asList(PREVIOUS_JOB_WITH_FUTURE_PAYMENT);
    private static final List<PreviousWork> EMPTY_PREVIOUS_WORK = new ArrayList<>();
    private static final List<CurrentWork> EMPTY_CURRENT_WORK = new ArrayList<>();
    private static final CurrentWork SHORT_TERM_PAID_WORK = new CurrentWork(null, false, null, null, false, false, null, "WEEKLY", false, false, 1);
    private static final CurrentWork LONG_TERM_PAID_WORK = new CurrentWork(null, false, null, null, false, false, null, "MONTHLY", false, false, 1);
    private static final List<CurrentWork> SHORT_TERM_PAID_CURRENT_WORK = Arrays.asList(SHORT_TERM_PAID_WORK);
    private static final List<CurrentWork> LONG_TERM_PAID_CURRENT_WORK = Arrays.asList(LONG_TERM_PAID_WORK);
    private static final BankDetails BANK_DETAILS = new BankDetails();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Circumstances circumstances;

    private BankDetails bankDetails;
    private EvidenceFactory factory;
    private Evidence evidence;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }

    @Test
    public void evidenceHasFuturePayments() {
        givenAFactory();
        when(circumstances.getPreviousWork()).thenReturn(PREVIOUS_WORK_WITH_FUTURE_PAYMENT);
        whenIGetEvidence();
        thenTheEvidenceHasFuturePayments();
    }

    @Test
    public void evidenceHasNoFuturePayments() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasNoFuturePayments();
    }

    @Test
    public void evidenceHasLongTermPaidJobs() {
        givenAFactory();
        when(circumstances.getCurrentWork()).thenReturn(LONG_TERM_PAID_CURRENT_WORK);
        whenIGetEvidence();
        thenTheEvidenceHasLongTermPaidJobs();
    }

    @Test
    public void evidenceHasNoLongTermPaidJobs() {
        givenAFactory();
        when(circumstances.getCurrentWork()).thenReturn(SHORT_TERM_PAID_CURRENT_WORK);
        whenIGetEvidence();
        thenTheEvidenceHasNoLongTermPaidJobs();
    }

    @Test
    public void evidenceHasShortTermPaidJobs() {
        givenAFactory();
        when(circumstances.getCurrentWork()).thenReturn(SHORT_TERM_PAID_CURRENT_WORK);
        whenIGetEvidence();
        thenTheEvidenceHasShortTermPaidJobs();
    }

    @Test
    public void evidenceHasNoShortTermPaidJobs() {
        givenAFactory();
        when(circumstances.getCurrentWork()).thenReturn(LONG_TERM_PAID_CURRENT_WORK);
        whenIGetEvidence();
        thenTheEvidenceHasNoShortTermPaidJobs();
    }

    @Test
    public void evidenceHasP45() {
        givenAFactory();
        when(circumstances.getPreviousWork()).thenReturn(PREVIOUS_WORK);
        whenIGetEvidence();
        thenTheEvidenceHasP45();
    }

    @Test
    public void evidenceHasNoP45() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasNoP45();
    }

    @Test
    public void evidenceAlwaysHasPrimaryId() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasPrimaryId();
    }

    @Test
    public void evidenceAlwaysHasSecondaryId() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasSecondaryId();
    }

    @Test
    public void evidenceHasJuryService() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasJuryService();
    }

    @Test
    public void evidenceHasNoJuryService() {
        givenAFactory();
        when(circumstances.getJuryService()).thenReturn(null);
        whenIGetEvidence();
        thenTheEvidenceHasNoJuryService();
    }

    @Test
    public void evidenceHasPensions() {
        givenAFactory();
        when(circumstances.getPensions().getCurrent()).thenReturn(CURRENT_PENSIONS);
        whenIGetEvidence();
        thenTheEvidenceHasPensions();
    }

    @Test
    public void evidenceHasNoPensions() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasNoPensions();
    }

    @Test
    public void evidenceHasNoBankDetailsProvided() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasNoBankDetailsProvided();
    }

    @Test
    public void evidenceHasBankDetailsProvided() {
        givenAFactory();
        bankDetails = BANK_DETAILS;
        whenIGetEvidence();
        thenTheEvidenceHasBankDetailsProvided();
    }

    @Test
    public void evidenceHasClaimStartDateInPast() {
        givenAFactory();
        when(circumstances.getClaimStartDate()).thenReturn(LocalDate.now().minusDays(1));
        whenIGetEvidence();
        thenTheEvidenceHasClaimStartDateInThePast();
    }

    @Test
    public void evidenceHasClaimStartDateNotInThePast() {
        givenAFactory();
        whenIGetEvidence();
        thenTheEvidenceHasClaimStartDateNotInThePast();
    }

    private void givenAFactory() {
        LocalDate now = LocalDate.now();
        factory = new EvidenceFactory();
        when(circumstances.getPensions().getCurrent()).thenReturn(EMPTY_CURRENT_PENSIONS);
        when(circumstances.getPensions().getDeferred()).thenReturn(EMPTY_DEFERRED_PENSIONS);
        when(circumstances.getPensions().getFuture()).thenReturn(EMPTY_FUTURE_PENSIONS);
        when(circumstances.getPreviousWork()).thenReturn(EMPTY_PREVIOUS_WORK);
        when(circumstances.getCurrentWork()).thenReturn(EMPTY_CURRENT_WORK);
        when(circumstances.getDateOfClaim()).thenReturn(now);
        when(circumstances.getClaimStartDate()).thenReturn(now);
    }

    private void whenIGetEvidence() {
        evidence = factory.create(circumstances, bankDetails);
    }

    private void thenTheEvidenceHasPensions() {
        assertThat(evidence.isPensions(), is(true));
    }

    private void thenTheEvidenceHasNoPensions() {
        assertThat(evidence.isPensions(), is(false));
    }

    private void thenTheEvidenceHasJuryService() {
        assertThat(evidence.isJuryService(), is(true));
    }

    private void thenTheEvidenceHasNoJuryService() {
        assertThat(evidence.isJuryService(), is(false));
    }

    private void thenTheEvidenceHasSecondaryId() {
        assertThat(evidence.isSecondryId(), is(true));
    }

    private void thenTheEvidenceHasPrimaryId() {
        assertThat(evidence.isPrimaryId(), is(true));
    }

    private void thenTheEvidenceHasNoP45() {
        assertThat(evidence.isP45(), is(false));
    }

    private void thenTheEvidenceHasP45() {
        assertThat(evidence.isP45(), is(true));
    }

    private void thenTheEvidenceHasNoShortTermPaidJobs() {
        assertThat(evidence.isShortTermPaidJobs(), is(false));
    }

    private void thenTheEvidenceHasShortTermPaidJobs() {
        assertThat(evidence.isShortTermPaidJobs(), is(true));
    }

    private void thenTheEvidenceHasNoLongTermPaidJobs() {
        assertThat(evidence.isLongTermPaidJobs(), is(false));
    }

    private void thenTheEvidenceHasLongTermPaidJobs() {
        assertThat(evidence.isLongTermPaidJobs(), is(true));
    }

    private void thenTheEvidenceHasFuturePayments() {
        assertThat(evidence.isFuturePayments(), is(true));
    }

    private void thenTheEvidenceHasNoFuturePayments() {
        assertThat(evidence.isFuturePayments(), is(false));
    }

    private void thenTheEvidenceHasNoBankDetailsProvided() {
        assertThat(evidence.isBankDetailsNotProvided(), is(true));
    }
    private void thenTheEvidenceHasBankDetailsProvided() {
        assertThat(evidence.isBankDetailsNotProvided(), is(false));
    }

    private void thenTheEvidenceHasClaimStartDateNotInThePast() {
        assertThat(evidence.isClaimStartDateInPast(), is(false));
    }
    private void thenTheEvidenceHasClaimStartDateInThePast() {
        assertThat(evidence.isClaimStartDateInPast(), is(true));
    }
}
