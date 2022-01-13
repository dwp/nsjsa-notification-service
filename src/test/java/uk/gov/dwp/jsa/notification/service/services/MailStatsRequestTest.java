package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;
import uk.gov.dwp.jsa.adaptors.dto.claim.ClaimStatistics;
import uk.gov.dwp.jsa.adaptors.http.api.ClaimStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MailStatsRequestTest {

    private ClaimStats claimStats;
    private MailStatsRequest mailStatsRequest;

    private final LocalDateTime OLDEST_OPEN = LocalDateTime.now();

    @Test
    public void testThatWeCanBuildAMailStatsRequestWithAllOptionalAndRequiredFields() {
        givenWeHaveClaimStatsRequest();
        whenWeBuildTheMailStatsRequest();
        thenWeExpectTheRequestToBeCorrect();
    }

    @Test
    public void givenNullForOldestOpenClaim_thenOldestOpenIsEmptyString() {
        givenWeHaveClaimStatsRequestWithNoOldestOpenClaim();
        whenWeBuildTheMailStatsRequest();
        thenWeExpectTheRequestToBeCorrectAndOldestOpenToBeEmptyString();
    }

    private void givenWeHaveClaimStatsRequestWithNoOldestOpenClaim() {
        claimStats = new ClaimStats(new ClaimStatistics(
                1,
                1,
                null,
                1,
                66.67,
                100,
                1,
                1.5,
                1,
                1,
                1
        ));
    }

    private void givenWeHaveClaimStatsRequest() {
        claimStats = new ClaimStats(new ClaimStatistics(
                1,
                1,
                OLDEST_OPEN,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1
        ));
    }

    private void whenWeBuildTheMailStatsRequest() {
        mailStatsRequest = new MailStatsRequest.MailStatsRequestBuilder(claimStats).build();
    }

    private void thenWeExpectTheRequestToBeCorrect() {
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_RECEIVED_DAY), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.HEAD_OF_WORK), is("1"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MailStatsRequest.DATE_TIME_FORMAT);
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.OLDEST_OPEN), is(OLDEST_OPEN.format(formatter)));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_CLEARED_IN_DAY), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_DAY_IN_24HR), is("1.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_DAY_IN_48HR), is("1.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_WEEK_IN_24HR), is("1.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_WEEK_IN_48HR), is("1.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASE_OUTSIDE_48HR_KPI), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_OUTSIDE_24HR_KPI), is("1"));
    }

    private void thenWeExpectTheRequestToBeCorrectAndOldestOpenToBeEmptyString() {
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_RECEIVED_DAY), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.HEAD_OF_WORK), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.OLDEST_OPEN), is(""));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_CLEARED_IN_DAY), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_DAY_IN_24HR), is("66.67%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_DAY_IN_48HR), is("100.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_WEEK_IN_24HR), is("1.50%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.PERCENTAGE_CLOSED_WEEK_IN_48HR), is("1.00%"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASE_OUTSIDE_48HR_KPI), is("1"));
        assertThat(mailStatsRequest.getPersonalisation().get(MailStatsRequest.CASES_OUTSIDE_24HR_KPI), is("1"));
    }

}
