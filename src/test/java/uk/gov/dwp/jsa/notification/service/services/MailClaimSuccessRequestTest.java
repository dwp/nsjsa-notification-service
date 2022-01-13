package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MailClaimSuccessRequestTest {
    private static final String SOME_TEMPLATE_ID = "some template Id";
    private static final String SOME_EMAIL_ADDRESS = "some email address";
    private static final String SOME_FIRST_NAME = "some first name";
    private static final String SOME_LAST_NAME = "some last name";
    private static final UUID SOME_REFERENCE = UUID.randomUUID();
    private static final String SOME_EMAIL_REPLY_ID = "some email reply id";

    private MailClaimSuccessRequest request;



    @Test
    public void testThatWeCanBuildMailProgressRequest () {
        whenWeBuildTheMailRequest();
        thenWeExpectTheRequestToBeCorrect();
    }



    private void whenWeBuildTheMailRequest() {
        request = new MailClaimSuccessRequest.MailClaimSuccessRequestBuilder(
                SOME_TEMPLATE_ID, SOME_EMAIL_ADDRESS,
                SOME_FIRST_NAME, SOME_LAST_NAME,
                SOME_REFERENCE).withEmailReplyToId(SOME_EMAIL_REPLY_ID).build();
    }

    private void thenWeExpectTheRequestToBeCorrect() {
        assertThat(request.getTemplateId(), is(SOME_TEMPLATE_ID));
        assertThat(request.getSuccessEmailAddress(), is(SOME_EMAIL_ADDRESS));
        assertThat(request.getPersonalisation().get(MailClaimSuccessRequest.FIRST_NAME), is(SOME_FIRST_NAME));
        assertThat(request.getPersonalisation().get(MailClaimSuccessRequest.LAST_NAME), is(SOME_LAST_NAME));
        assertThat(request.getSuccessReference(), is(SOME_REFERENCE));
        assertThat(request.getSuccessEmailToReplyTo(), is(SOME_EMAIL_REPLY_ID));
    }
}
