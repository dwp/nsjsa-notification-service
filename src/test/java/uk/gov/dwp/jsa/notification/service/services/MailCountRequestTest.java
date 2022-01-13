package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;
import uk.gov.dwp.jsa.adaptors.http.api.SubmittedClaimsTally;
import uk.gov.dwp.jsa.notification.service.utils.SimpleI8NDateFormat;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MailCountRequestTest {

    private static final String SOME_TEMPLATE_ID = "some template Id";
    private static final String SOME_EMAIL_ADDRESS = "some email address";
    private static final LocalDate SOME_TALLY_DATE = LocalDate.of(2000,1,1);
    private static final String EXPECTED_TALLY_DATE =
            new SimpleI8NDateFormat(Locale.getDefault()).format(SOME_TALLY_DATE);
    private static final UUID SOME_REFERENCE = UUID.randomUUID();
    private static final String SOME_EMAIL_REPLY_ID = "some email reply id";

    private MailCountRequest request;
    private SubmittedClaimsTally submittedClaimsTally;

    @Test
    public void testThatWeCanBuildAMailCountRequestWithAllOptionalAndRequiredFields() {
        givenWeHaveSubmittedClaimsTally();
        whenWeBuildTheMailCountRequest();
        thenWeExpectTheRequestToBeCorrect("1", "2", "3");
    }

    @Test
    public void testThatWeCanBuildAMailCountRequestWithTheRequiredFieldsThatAreNull() {
        givenWeHaveSubmittedClaimsTallyWithNullValues();
        whenWeBuildTheMailCountRequest();
        thenWeExpectTheRequestToBeCorrect("0","0" ,"0");
    }


    private void givenWeHaveSubmittedClaimsTally() {
        submittedClaimsTally = new SubmittedClaimsTally(SOME_TALLY_DATE, 1L, 2L, 3L);
    }
    private void givenWeHaveSubmittedClaimsTallyWithNullValues() {
        submittedClaimsTally = new SubmittedClaimsTally(SOME_TALLY_DATE, null, null, null);
    }

    private void whenWeBuildTheMailCountRequest() {
        request = new MailCountRequest.MailCountRequestBuilder(
                SOME_TEMPLATE_ID,
                SOME_EMAIL_ADDRESS,
                submittedClaimsTally,
                SOME_REFERENCE)
                    .withEmailReplyToId(SOME_EMAIL_REPLY_ID).build();
    }

    private void thenWeExpectTheRequestToBeCorrect(final String onlineClaimCount, final String assistedDigitalClaimCount, final String totalCount) {
        assertThat(request.getTemplateId(), is(SOME_TEMPLATE_ID));
        assertThat(request.getEmailAddress(), is(SOME_EMAIL_ADDRESS));
        assertThat(request.getPersonalisation().get(MailCountRequest.TALLY_DATE), is(EXPECTED_TALLY_DATE));
        assertThat(request.getPersonalisation().get(MailCountRequest.ONLINE_CLAIM_COUNT), is(onlineClaimCount));
        assertThat(request.getPersonalisation().get(MailCountRequest.ASSISTED_DIGITAL_CLAIM_COUNT), is(assistedDigitalClaimCount));
        assertThat(request.getPersonalisation().get(MailCountRequest.TOTAL_COUNT), is(totalCount));
        assertThat(request.getReference(), is(SOME_REFERENCE));
        assertThat(request.getEmailReplyToId(), is(SOME_EMAIL_REPLY_ID));
    }
}
