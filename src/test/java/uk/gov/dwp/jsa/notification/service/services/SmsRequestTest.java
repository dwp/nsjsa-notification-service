package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SmsRequestTest {

    private static final String SOME_TEMPLATE_ID = "some template Id";
    private static final String SOME_MOBILE_NUMBER = "some mobile number";
    private static final String SOME_NAME = "some name";
    private static final UUID SOME_REFERENCE = UUID.randomUUID();
    private static final String SOME_SMS_SENDER_ID = "some sms sender id";

    private SmsRequest request;

    @Test
    public void testThatWeCanBuildASmsRequestWithAllOptionalAndRequiredFields() {
        whenWeBuildTheSmsRequest();
        thenWeExpectTheRequestToBeCorrect();
    }

    private void whenWeBuildTheSmsRequest() {
        request = new SmsRequest.SmsRequestBuilder(
                SOME_TEMPLATE_ID,
                SOME_MOBILE_NUMBER,
                SOME_NAME,
                SOME_REFERENCE)
                    .withSmsSenderId(SOME_SMS_SENDER_ID).build();
    }

    private void thenWeExpectTheRequestToBeCorrect() {
        assertThat(request.getTemplateId(), is(SOME_TEMPLATE_ID));
        assertThat(request.getPhoneNumber(), is(SOME_MOBILE_NUMBER));
        assertThat(request.getPersonalisation().get(SmsRequest.FIRST_NAME), is(SOME_NAME));
        assertThat(request.getReference(), is(SOME_REFERENCE));
        assertThat(request.getSmsSenderId(), is(SOME_SMS_SENDER_ID));
    }
}
