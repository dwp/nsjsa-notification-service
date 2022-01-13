package uk.gov.dwp.jsa.notification.service.controllers;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.jsa.adaptors.http.api.*;
import uk.gov.dwp.jsa.notification.service.AppInfo;
import uk.gov.dwp.jsa.notification.service.config.WithVersionUriComponentsBuilder;
import uk.gov.dwp.jsa.notification.service.services.NotificationService;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

    private static final String MAIL_RESPONSE = "{\"content\":{\"body\":\"#Dear person\\r\\n\\r\\nYour online application for Jobseeker...\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"New style Jobseeker\\u2019s Allowance \\u2013 application received\"},\"id\":\"a8e03f45-3900-4c74-bfec-d2306d2eeb2e\",\"reference\":\"8649139a-7774-44eb-9c0b-326927415755\",\"scheduled_for\":null,\"template\":{\"id\":\"def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"version\":28},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/a8e03f45-3900-4c74-bfec-d2306d2eeb2e\"}";
    private static final String MAIL_CLAIM_COUNT_RESPONSE = "{\"content\":{\"body\":\"The following details the statistics for the 03/01/2019\\r\\n\\r\\n#Online Claims\\r\\n\\r\\n^22\\r\\n\\r\\n#Assisted Digital Claims\\r\\n^22\\r\\n\\r\\n#Total Claim Count\\r\\n^22\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"Daily Claims Submitted \\u2013 03/01/2019\"},\"id\":\"6caf33eb-9cd1-4440-914c-5fa852701f56\",\"reference\":\"1da8b941-7583-484c-9191-16e08b6f2ee1\",\"scheduled_for\":null,\"template\":{\"id\":\"989b6ef9-5ac7-4e8a-814e-c0e99d3195bc\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/989b6ef9-5ac7-4e8a-814e-c0e99d3195bc\",\"version\":7},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/6caf33eb-9cd1-4440-914c-5fa852701f56\"}";
    private static final String SMS_RESPONSE = "{\"content\":{\"body\":\"Dear person. Following up on your New Style JSA application ...\",\"from_number\":\"GOVUK\"},\"id\":\"e6e93f93-c156-4ce1-9c1b-463e6156e7de\",\"reference\":null,\"scheduled_for\":null,\"template\":{\"id\":\"cfc1713a-ba70-4b22-98e5-bf55762cea9c\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/cfc1713a-ba70-4b22-98e5-bf55762cea9c\",\"version\":4},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/e6e93f93-c156-4ce1-9c1b-463e6156e7de\"}\n";
    private static final String MAIL_CLAIM_STATS_RESPONSE = "{\"content\":{\"body\":\"Total Number Of Claims 0\\r\\nTotal Number Of Claims Open 0\\r\\nOldest Claim Open 2019-04-08T12:52\\r\\nTotal Number Of Claims Closed 0\\r\\nTotal Number Of Claims In Day Closed In 24hr 0\\r\\nTotal Number Of Claims In Day Closed In 48hr 0\\r\\nTotal Number Of Claims In Week 0\\r\\nTotal Number Of Claims In Week Closed In 24hr 0\\r\\nTotal Number Of Claims In Week Closed In 48hr 0\",\"from_email\":\"new.style.job.seekers.allowance@notifications.service.gov.uk\",\"subject\":\"Claim Statistics Email\"},\"id\":\"680fca07-1569-494e-9f15-93f4bc8f9854\",\"reference\":\"d5b480bb-6a1e-4bb6-97d2-eb49f4a93a51\",\"scheduled_for\":null,\"template\":{\"id\":\"c41c28b6-cd2c-4212-a528-6e3cd56119a9\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/c41c28b6-cd2c-4212-a528-6e3cd56119a9\",\"version\":7},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/680fca07-1569-494e-9f15-93f4bc8f9854\"}";
    private static final String PROGRESS_MAIL_RESPONSE = "{\"content\":{\"body\":\"#Dear person\\r\\n\\r\\nWe’ve started to process your New Style Jobseeker’s Allowance (JSA) application.\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"New style Jobseeker\\u2019s Allowance \\u2013 application received\"},\"id\":\"a8e03f45-3900-4c74-bfec-d2306d2eeb2e\",\"reference\":\"8649139a-7774-44eb-9c0b-326927415755\",\"scheduled_for\":null,\"template\":{\"id\":\"7f64a2f7-9143-4143-98d0-316cf5fe21b6\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/7f64a2f7-9143-4143-98d0-316cf5fe21b6\",\"version\":28},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/a8e03f45-3900-4c74-bfec-d2306d2eeb2e\"}";
    private static final String PROGRESS_SMS_RESPONSE = "{\"content\":{\"body\":\"Dear person.  We've started to process your application. We will contact you if we have any questions.\",\"from_number\":\"GOVUK\"},\"id\":\"e6e93f93-c156-4ce1-9c1b-463e6156e7de\",\"reference\":null,\"scheduled_for\":null,\"template\":{\"id\":\"37152dcb-6fd4-4e23-8991-7b9c3b703b05\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/37152dcb-6fd4-4e23-8991-7b9c3b703b05\",\"version\":4},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/e6e93f93-c156-4ce1-9c1b-463e6156e7de\"}\n";

    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private NotificationRequest request;
    @Mock
    private SubmittedClaimsTally submittedClaimsTallyRequest;
    @Mock
    private AppInfo appInfo;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ClaimStats claimStats;

    private NotificationController notificationController;

    private ResponseEntity<ApiResponse<String>> response;

    @Before
    public void setup() {
        when(appInfo.getVersion()).thenReturn(StringUtils.EMPTY);
        notificationController = new NotificationController(
                new WithVersionUriComponentsBuilder(appInfo), notificationService);
    }

    @Test
    public void testThatICanSuccessfullySendAMail() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeHaveAMailResponse();
        whenISendTheMail();
        ThenIExpectTheMailResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendAMailClaimCount() throws NotificationClientException {
        givenWeHaveAMailClaimCountResponse();
        whenISendTheMailClaimCount();
        ThenIExpectTheMailClaimCountResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendASms() throws NotificationClientException {
        givenWeHaveASmsResponse();
        whenISendTheSms();
        ThenIExpectTheSmsResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendAMailClaimStats() throws NotificationClientException {
        givenWeHaveAMailClaimStatsResponse();
        whenISendTheMailClaimStats();
        ThenIExpectTheMailClaimStatsResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendAProgressEmail() throws NotificationClientException {
        givenWeHaveAMailProgressResponse();
        whenISendTheProgressMail();
        ThenIExpectTheProgressMailResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendAProgressSms() throws NotificationClientException {
        givenWeHaveASmsProgressResponse();
        whenISendTheProgressSms();
        ThenIExpectTheProgressSmsResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendASuccessEmail() throws NotificationClientException {
        givenWeHaveAMailProgressResponse();
        whenISendTheProgressMail();
        ThenIExpectTheSuccessMailResponseToBeCorrect();
    }

    @Test
    public void testThatICanSuccessfullySendASuccessSms() throws NotificationClientException {
        givenWeHaveASmsProgressResponse();
        whenISendTheProgressSms();
        ThenIExpectTheProgressSmsResponseToBeCorrect();
    }

    private void givenWeHaveAMailResponse() throws NotificationClientException, ExecutionException, InterruptedException {
        when(notificationService.sendMail(request)).thenReturn(new SendEmailResponse(MAIL_RESPONSE));
    }
    private void givenWeHaveASmsResponse() throws NotificationClientException {
        when(notificationService.sendSms(request)).thenReturn(new SendSmsResponse(SMS_RESPONSE));
    }
    private void givenWeHaveAMailClaimCountResponse() throws NotificationClientException {
        when(notificationService.sendClaimCountMail(submittedClaimsTallyRequest)).thenReturn(new SendEmailResponse(MAIL_CLAIM_COUNT_RESPONSE));
    }
    private void givenWeHaveAMailClaimStatsResponse() throws NotificationClientException {
        when(notificationService.sendClaimStatsMail(claimStats)).thenReturn(new SendEmailResponse(MAIL_CLAIM_STATS_RESPONSE));
    }

    private void givenWeHaveASmsProgressResponse() throws NotificationClientException {
        when(notificationService.sendProgressSMS(request)).thenReturn(new SendSmsResponse(PROGRESS_SMS_RESPONSE));
    }

    private void givenWeHaveAMailProgressResponse() throws NotificationClientException {
        when(notificationService.sendProgressMail(request)).thenReturn(new SendEmailResponse(PROGRESS_MAIL_RESPONSE));
    }

    private void givenWeHaveASmsSuccessResponse() throws NotificationClientException, ExecutionException, InterruptedException {
        when(notificationService.sendClaimSuccessSms(request)).thenReturn(new SendSmsResponse(PROGRESS_SMS_RESPONSE));
    }

    private void givenWeHaveAMailSuccessResponse() throws NotificationClientException, ExecutionException, InterruptedException {
        when(notificationService.sendClaimSuccessMail(request)).thenReturn(new SendEmailResponse(PROGRESS_MAIL_RESPONSE));
    }

    private void whenISendTheMail() throws NotificationClientException, ExecutionException, InterruptedException {
        response = notificationController.sendMail(servletRequest, request);
    }
    private void whenISendTheSms() throws NotificationClientException {
        response = notificationController.sendSms(servletRequest, request);
    }
    private void whenISendTheMailClaimCount() throws NotificationClientException {
        response = notificationController.sendClaimCountMail(servletRequest, submittedClaimsTallyRequest);
    }
    private void whenISendTheMailClaimStats() throws NotificationClientException {
        response = notificationController.sendClaimStatsMail(servletRequest, claimStats);
    }
    private void whenISendTheProgressSms() throws NotificationClientException {
        response = notificationController.sendClaimProgressSms(servletRequest, request);
    }

    private void whenISendTheProgressMail() throws NotificationClientException {
        response = notificationController.sendClaimProgressMail(servletRequest, request);
    }

    private void whenISendTheSuccessSms() throws NotificationClientException, ExecutionException, InterruptedException {
        response = notificationController.sendClaimSuccessSms(servletRequest, request);
    }

    private void whenISendSuccessMail() throws NotificationClientException, ExecutionException, InterruptedException {
        response = notificationController.sendClaimSuccessMail(servletRequest, request);
    }

    private void ThenIExpectTheMailResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }
    private void ThenIExpectTheSmsResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }
    private void ThenIExpectTheMailClaimCountResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }
    private void ThenIExpectTheMailClaimStatsResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }

    private void ThenIExpectTheProgressMailResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }

    private void ThenIExpectTheSuccessMailResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }
    private void ThenIExpectTheProgressSmsResponseToBeCorrect() {
        ApiSuccess<String> apiSuccess = response.getBody().getSuccess().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(apiSuccess.getPath().getPath(), containsString("/nsjsa/"));
        assertThat(apiSuccess.getData(), instanceOf(String.class));
    }
}
