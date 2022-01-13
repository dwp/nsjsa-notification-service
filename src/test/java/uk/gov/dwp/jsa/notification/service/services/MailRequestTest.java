package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.jsa.notification.service.services.evidence.Evidence;
import uk.gov.dwp.jsa.notification.service.utils.SimpleI8NDateFormat;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MailRequestTest {

    private static final String SOME_TEMPLATE_ID = "some template Id";
    private static final String SOME_EMAIL_ADDRESS = "some email address";
    private static final String SOME_FIRST_NAME = "some first name";
    private static final String SOME_LAST_NAME = "some last name";
    private static final LocalDate SOME_CLAIM_SUBMITTED_DATE = LocalDate.of(2000,1,1);
    private static final String EXPECTED_CLAIM_SUBMITTED_DATE =
            new SimpleI8NDateFormat(Locale.getDefault()).format(SOME_CLAIM_SUBMITTED_DATE);
    private static final UUID SOME_REFERENCE = UUID.randomUUID();
    private static final String SOME_EMAIL_REPLY_ID = "some email reply id";
    private static final LocalDate SOME_CLAIM_START_DATE =  LocalDate.of(2000,1,1);
    private static final String EXPECTED_CLAIM_START_DATE =
            new SimpleI8NDateFormat(Locale.getDefault()).format(SOME_CLAIM_START_DATE);

    private Evidence evidence;
    private MailRequest request;

    @Test
    public void testThatWeCanBuildAMailRequestWithAllOptionalAndRequiredFields() {
        givenWeHaveEvidenceWithAMixedSetOfFlags();
        whenWeBuildTheMailRequest();
        thenWeExpectTheRequestToBeCorrect();
    }

    @Test
    public void testThatAnySelectedIsFalseWhenAllFlagsAreNotSet() {
        givenWeHaveEvidenceWithNoFlagsSet();
        whenWeBuildTheMailRequest();
        thenWeExpectThatAnySelectedIs(false);
    }

    @Test
    public void testThatAnySelectedIsTrueWhenOneFlagIsSet() {
        givenWeHaveEvidenceWithOneFlagSet();
        whenWeBuildTheMailRequest();
        thenWeExpectThatAnySelectedIs(true);
    }

    private void givenWeHaveEvidenceWithAMixedSetOfFlags() {
        evidence = new Evidence(
                true, false,
                true, false,
                true, false,
                true,false,
                true, true);
    }
    private void givenWeHaveEvidenceWithNoFlagsSet() {
        evidence = new Evidence();
    }
    private void givenWeHaveEvidenceWithOneFlagSet() {
        evidence = new Evidence();
        ReflectionTestUtils.setField(evidence, "shortTermPaidJobs", true);
    }

    private void whenWeBuildTheMailRequest() {
        request = new MailRequest.MailRequestBuilder(
                SOME_TEMPLATE_ID, SOME_EMAIL_ADDRESS,
                SOME_FIRST_NAME, SOME_LAST_NAME,
                SOME_CLAIM_SUBMITTED_DATE, SOME_CLAIM_START_DATE, evidence,
                SOME_REFERENCE)
                    .withEmailReplyToId(SOME_EMAIL_REPLY_ID).build();
    }

    private void thenWeExpectTheRequestToBeCorrect() {
        assertThat(request.getTemplateId(), is(SOME_TEMPLATE_ID));
        assertThat(request.getEmailAddress(), is(SOME_EMAIL_ADDRESS));
        assertThat(request.getPersonalisation().get(MailRequest.FIRST_NAME), is(SOME_FIRST_NAME));
        assertThat(request.getPersonalisation().get(MailRequest.LAST_NAME), is(SOME_LAST_NAME));
        assertThat(request.getPersonalisation().get(MailRequest.CLAIM_SUBMITTED_DATE), is(EXPECTED_CLAIM_SUBMITTED_DATE));
        assertThat(request.getPersonalisation().get(MailRequest.CLAIM_START_DATE), is(EXPECTED_CLAIM_START_DATE));
        assertThat(request.getPersonalisation().get(MailRequest.JURY_SERVICE), is("true"));
        assertThat(request.getPersonalisation().get(MailRequest.CURRENT_WORK_WEEKLY), is("false"));
        assertThat(request.getPersonalisation().get(MailRequest.CURRENT_WORK_MONTHLY), is("true"));
        assertThat(request.getPersonalisation().get(MailRequest.PREVIOUS_EMPLOYMENT), is("false"));
        assertThat(request.getPersonalisation().get(MailRequest.PREVIOUS_EMPLOYMENT_EXPECTING), is("true"));
        assertThat(request.getPersonalisation().get(MailRequest.ANY_PENSION), is("false"));
        assertThat(request.getPersonalisation().get(MailRequest.CLAIM_START_DATE_IN_PAST), is("true"));
        assertThat(request.getPersonalisation().get(MailRequest.BANK_DETAILS_NOT_PROVIDED), is("false"));
        assertThat(request.getReference(), is(SOME_REFERENCE));
        assertThat(request.getEmailReplyToId(), is(SOME_EMAIL_REPLY_ID));
    }
    private void thenWeExpectThatAnySelectedIs(final boolean state) {
        assertThat(request.getPersonalisation().get(MailRequest.ANY_SELECTED), is(String.valueOf(state)));
    }
}
