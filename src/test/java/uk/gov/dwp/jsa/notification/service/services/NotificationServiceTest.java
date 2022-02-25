package uk.gov.dwp.jsa.notification.service.services;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.ClaimStatistics;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.LanguagePreference;
import uk.gov.dwp.jsa.adaptors.http.api.ClaimStats;
import uk.gov.dwp.jsa.adaptors.http.api.NotificationRequest;
import uk.gov.dwp.jsa.adaptors.http.api.SubmittedClaimsTally;
import uk.gov.dwp.jsa.notification.service.config.NotificationAwsSsmProperties;
import uk.gov.dwp.jsa.notification.service.config.NotificationProperties;
import uk.gov.dwp.jsa.notification.service.exceptions.ClaimantByIdNotFoundException;
import uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary;
import uk.gov.dwp.jsa.notification.service.services.csv.DailyClaimStatsSummaryCsvCreator;
import uk.gov.dwp.jsa.notification.service.services.evidence.Evidence;
import uk.gov.dwp.jsa.notification.service.services.evidence.EvidenceFactory;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    private static final String SOME_NUMBER = "some number";
    private static final String SOME_EMAIL_ADDRESS = "some email address";
    private static final String SOME_FIRST_NAME = "some first name";
    private static final String SOME_SMS_TEMPLATE_ID_ENGLISH = "some sms template id for english";
    private static final String SOME_SMS_TEMPLATE_ID_WELSH = "some sms template id for welsh";
    private static final String SOME_MAIL_TEMPLATE_ID = "some mail template id";
    private static final String STATS_MAIL_TEMPLATE_ID = "stats mail template id";
    private static final String SOME_PROGRESS_SMS_TEMPLATE_ID_ENGLISH = "some sms template id for english";
    private static final String SOME_PROGRESS_SMS_TEMPLATE_ID_WELSH = "some sms template id for welsh";
    private static final String SOME_PROGRESS_MAIL_TEMPLATE_ID_ENGLISH = "some mail template id";
    private static final String SOME_PROGRESS_MAIL_TEMPLATE_ID_WELSH = "stats mail template id";
    private static final String SOME_SUCCESS_SMS_TEMPLATE_ID_ENGLISH = "some sms template id for english";
    private static final String SOME_SUCCESS_SMS_TEMPLATE_ID_WELSH = "some sms template id for welsh";
    private static final String SOME_SUCCESS_MAIL_TEMPLATE_ID_ENGLISH = "some mail template id";
    private static final String SOME_SUCCESS_MAIL_TEMPLATE_ID_WELSH = "stats mail template id";
    private static final String STATS_EMAIL_ADDRESS = "stats email address";
    private static final String MAIL_RESPONSE_ONE = "{\"content\":{\"body\":\"#_RESPONSE_ONE_Dear person\\r\\n\\r\\nYour online application for Jobseeker...\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"New style Jobseeker\\u2019s Allowance \\u2013 application received\"},\"id\":\"a8e03f45-3900-4c74-bfec-d2306d2eeb2e\",\"reference\":\"8649139a-7774-44eb-9c0b-326927415755\",\"scheduled_for\":null,\"template\":{\"id\":\"def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"version\":28},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/a8e03f45-3900-4c74-bfec-d2306d2eeb2e\"}";
    private static final String MAIL_RESPONSE_TWO = "{\"content\":{\"body\":\"#_RESPONSE_TWO_Dear person\\r\\n\\r\\nYour online application for Jobseeker...\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"New style Jobseeker\\u2019s Allowance \\u2013 application received\"},\"id\":\"a8e03f45-3900-4c74-bfec-d2306d2eeb2e\",\"reference\":\"8649139a-7774-44eb-9c0b-326927415755\",\"scheduled_for\":null,\"template\":{\"id\":\"def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"version\":28},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/a8e03f45-3900-4c74-bfec-d2306d2eeb2e\"}";

    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    private static final LocalDate CLAIM_START_DATE = LocalDate.now();
    private static final LanguagePreference ENGLISH_CONTACT_PREFERENCE =
            new LanguagePreference(false, false);
    private static final LanguagePreference WELSH_CONTACT_PREFERENCE =
            new LanguagePreference(true, true);

    @Mock
    private NotificationProperties notificationProperties;
    @Mock
    private NotificationClient client;
    @Mock
    private NotificationRequest request;
    @Mock
    private SubmittedClaimsTally submittedClaimsTallyRequest;
    @Mock
    private ClaimantServiceAdaptor claimantServiceAdaptor;
    @Mock
    private CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    @Mock
    private BankDetailsServiceAdaptor bankDetailsServiceAdaptor;
    @Mock
    private EvidenceFactory evidenceFactory;
    @Mock
    private ClaimStatistics claimStatistics;
    @Mock
    private ClaimStats claimStatsRequest;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Claimant mockClaimant;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Circumstances mockCircumstances;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BankDetails mockBankDetails;

    @Mock
    private Evidence evidence;

    @Mock
    private DailyClaimStatsReportService mockDailyClaimStatsReportService;

    @Mock
    private AWSSimpleSystemsManagement mockSsmClient;

    @Mock
    private NotificationAwsSsmProperties mockAwsProperties;

    private NotificationService notificationService;

    @Mock
    private DailyClaimStatsSummaryCsvCreator mockCsvCreator;

    @Before
    public void setup() {
        when(notificationProperties.getSmsTemplateIdEnglish()).thenReturn(SOME_SMS_TEMPLATE_ID_ENGLISH);
        when(notificationProperties.getSmsTemplateIdWelsh()).thenReturn(SOME_SMS_TEMPLATE_ID_WELSH);
        when(notificationProperties.getMailTemplateIdEnglish()).thenReturn(SOME_MAIL_TEMPLATE_ID);
        when(notificationProperties.getMailCountTemplateId()).thenReturn(SOME_MAIL_TEMPLATE_ID);
        when(notificationProperties.getMailCountAddress()).thenReturn(SOME_EMAIL_ADDRESS);
        when(notificationProperties.getMailStatsTemplateId()).thenReturn(STATS_MAIL_TEMPLATE_ID);
        when(notificationProperties.getMailStatsAddress()).thenReturn(STATS_EMAIL_ADDRESS);
        when(notificationProperties.getMailProgressTemplateIdEnglish()).thenReturn(SOME_PROGRESS_MAIL_TEMPLATE_ID_ENGLISH);
        when(notificationProperties.getMailProgressTemplateIdWelsh()).thenReturn(SOME_PROGRESS_MAIL_TEMPLATE_ID_WELSH);
        when(notificationProperties.getSmsProgressTemplateIdEnglish()).thenReturn(SOME_PROGRESS_SMS_TEMPLATE_ID_ENGLISH);
        when(notificationProperties.getSmsProgressTemplateIdWelsh()).thenReturn(SOME_PROGRESS_SMS_TEMPLATE_ID_WELSH);
        when(notificationProperties.getMailSuccessTemplateIdEnglish()).thenReturn(SOME_SUCCESS_MAIL_TEMPLATE_ID_ENGLISH);
        when(notificationProperties.getMailSuccessTemplateIdWelsh()).thenReturn(SOME_SUCCESS_MAIL_TEMPLATE_ID_WELSH);
        when(notificationProperties.getSmsSuccessTemplateIdEnglish()).thenReturn(SOME_SUCCESS_SMS_TEMPLATE_ID_ENGLISH);
        when(notificationProperties.getSmsSuccessTemplateIdWelsh()).thenReturn(SOME_SUCCESS_SMS_TEMPLATE_ID_WELSH);
        when(request.getClaimantId()).thenReturn(CLAIMANT_ID);

        notificationService = new NotificationService(notificationProperties, client, claimantServiceAdaptor,
                circumstancesServiceAdaptor, bankDetailsServiceAdaptor, evidenceFactory,
                mockDailyClaimStatsReportService, mockSsmClient, mockAwsProperties, mockCsvCreator);
    }

    @Test
    public void testThatWeCanSuccessfullySendAnSmsWithEnglishTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(true);
        whenWeSendTheSms();
        thenWeVerifyThatWeveCalledTheSms(true);
    }

    @Test
    public void testThatWeCanSuccessfullySendAnSmsWithWelshTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(false);
        whenWeSendTheSms();
        thenWeVerifyThatWeveCalledTheSms(false);

    }

    @Test
    public void testWeCanSuccessfullySendAProgressSmsWithEnglishTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(true);
        whenWeSendTheProgressSms();
        thenWeVerifyThatWeveCalledTheProgressSms(true);
    }

    @Test
    public void testWeCanSuccessfullySendAProgressSmsWithWelshTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(false);
        whenWeSendTheProgressSms();
        thenWeVerifyThatWeveCalledTheProgressSms(false);
    }

    @Test
    public void testWeCanSuccessfullySendAProgressMailWithEnglishTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(true);
        whenWeSendTheProgressEmail();
        thenWeVerifyThatWeveCalledTheProgressMailEnglish();
    }

    @Test
    public void testWeCanSuccessfullySendAProgressMailWithWelshTemplateId() throws NotificationClientException {
        givenWeGetTheClaimant(false);
        whenWeSendTheProgressEmail();
        thenWeVerifyThatWeveCalledTheProgressMailWelsh();
    }

    @Test
    public void testWeCanSuccessfullySendASuccessSmsWithEnglishTemplateId() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        whenWeSendTheSuccessSms();
        thenWeVerifyThatWeveCalledTheSuccessSms(true);
    }

    @Test
    public void testWeCanSuccessfullySendASuccessSmsWithWelshTemplateId() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(false);
        whenWeSendTheSuccessSms();
        thenWeVerifyThatWeveCalledTheSuccessSms(false);
    }

    @Test
    public void testWeCanSuccessfullySendASuccessMailWithEnglishTemplateId() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        whenWeSendTheSuccessEmail();
        thenWeVerifyThatWeveCalledTheProgressMailEnglish();
    }

    @Test
    public void testWeCanSuccessfullySendASuccessMailWithWelshTemplateId() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(false);
        whenWeSendTheSuccessEmail();
        thenWeVerifyThatWeveCalledTheSuccessMailWelsh();
    }

    @Test(expected = ClaimantByIdNotFoundException.class)
    public void ensureThatWeThrowAnExceptionWhenWeCantFindTheClaimantForAnSms() throws NotificationClientException {
        givenWeCantfindTheClaimant();
        whenWeSendTheSms();
    }

    @Test(expected = NotificationClientException.class)
    public void ensureThatWeThrowAClientExceptionFromANotificationClientSmsCall() throws NotificationClientException {
        givenWeGetTheClaimant(true);
        givenWeThrowANotificationClientExceptionWhenSendingAnSms();
        whenWeSendTheSms();
    }

    @Test
    public void testThatWeCanSuccessfullySendAnEmail() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        givenWeGetTheCircumstances();
        givenWeGetTheBankDetails();
        givenWeHaveEvidence();
        whenWeSendTheMail();
        thenWeVerifyThatWeveCalledTheMail();
    }

    @Test
    public void testThatWeCanSuccessfullySendEmailWithoutBankDetails() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        givenWeGetTheCircumstances();
        givenWeCantFindTheBankDetails();
        givenWeHaveEvidence();
        whenWeSendTheMail();
        thenWeVerifyThatWeveCalledTheMail();
    }

    @Test(expected = ClaimantByIdNotFoundException.class)
    public void ensureThatWeThrowAnExceptionWhenWeCantFindTheClaimantForAnEmail() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeCantfindTheClaimant();
        givenWeGetTheCircumstances();
        givenWeGetTheBankDetails();
        givenWeHaveEvidence();
        whenWeSendTheMail();
    }

    @Test(expected = ClaimantByIdNotFoundException.class)
    public void ensureThatWeThrowAnExceptionWhenWeCantFindTheCircumstancesForAnEmail() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        givenWeCantFindTheCircumstances();
        givenWeGetTheBankDetails();
        givenWeHaveEvidence();
        whenWeSendTheMail();
    }

    @Test(expected = NotificationClientException.class)
    public void ensureThatWeThrowAClientExceptionFromANotificationClientEmail() throws NotificationClientException, ExecutionException, InterruptedException {
        givenWeGetTheClaimant(true);
        givenWeGetTheCircumstances();
        givenWeGetTheBankDetails();
        givenWeHaveEvidence();
        givenWeThrowANotificationClientExceptionWhenSendingAnEmail();
        whenWeSendTheMail();
    }

    @Test
    public void testThatWeCanSuccessfullySendClaimCountMail() throws NotificationClientException {
        givenWeHaveTheSubmittedClaimsTallyRequest();
        whenWeSendTheClaimCountMail();
        thenWeVerifyThatWeveCalledTheClaimCountMail();
    }

    @Test
    public void testThatWeCanSuccessfullySendClaimStatsMail() throws NotificationClientException {
        givenWeHaveTheClaimStatsRequest();
        whenWeSendTheClaimStatsMail();
        thenWeVerifyThatWeveCalledTheClaimStatsMail();
    }

    @Test
    public void testSendDailyClaimStatsSummaryMail() throws NotificationClientException {
        //Arrange
        final ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        final String expectedRecipients = "one@mail.com,two@mail.com";
        final String expectedTemplateId = "templateId";
        final int previousDayCount = 8;
        final String expectedKey = "AKey";
        final List<DailyClaimStatsSummary> summaries = Collections.singletonList(
                new DailyClaimStatsSummary(LocalDate.now(), 1, 1, 1, 1, 1, 1)
        );
        final Parameter parameter = new Parameter();
        parameter.setValue(expectedRecipients);
        final GetParameterResult getParameterResult = new GetParameterResult();
        getParameterResult.setParameter(parameter);

        when(mockDailyClaimStatsReportService.getPreviousDailyClaimStats(eq(previousDayCount)))
                .thenReturn(summaries);
        when(mockAwsProperties.getDailyClaimStatsMailingListKey()).thenReturn(expectedKey);
        when(mockSsmClient.getParameter(any())).thenReturn(getParameterResult);
        when(notificationProperties.getMailDailyClaimStatsSummaryTemplateId()).thenReturn(expectedTemplateId);
        when(mockCsvCreator.createCsv(eq(summaries))).thenReturn("a csv".getBytes(StandardCharsets.UTF_8));
        when(client.sendEmail(eq(expectedTemplateId), emailCaptor.capture(), anyMap(), anyString()))
                .thenReturn(new SendEmailResponse(MAIL_RESPONSE_ONE))
                .thenReturn(new SendEmailResponse(MAIL_RESPONSE_TWO));

        //Act
        final List<SendEmailResponse> actuals = notificationService.sendDailyClaimStatsSummaryMail(previousDayCount);

        //Assert
        assertThat(actuals).hasSize(2);
        assertThat(actuals.get(0).getBody()).contains("RESPONSE_ONE");
        assertThat(actuals.get(1).getBody()).contains("RESPONSE_TWO");
        assertThat(emailCaptor.getAllValues()).isEqualTo(Arrays.asList(expectedRecipients.split(",")));
    }

    /**
     * Tests {@link NotificationService#sendDailyClaimStatsSummaryMail(int)} attempts to continue sending emails
     * even if multiple fail to send.
     */
    @Test
    public void testSendDailyClaimStatsSummaryMailContinuesEmailSendingIfOneError() throws NotificationClientException {
        //Arrange
        final String expectedRecipients = "one@mail.com,two@mail.com,three@mail.com,four@mail.com";
        final String expectedTemplateId = "templateId";
        final int previousDayCount = 8;
        final int expectedResponses = 2;
        final int expectedEmailRecipients = 4;
        final String expectedKey = "AKey";
        final List<DailyClaimStatsSummary> summaries = Collections.singletonList(
                new DailyClaimStatsSummary(LocalDate.now(), 1, 1, 1, 1, 1, 1)
        );
        final Parameter parameter = new Parameter();
        parameter.setValue(expectedRecipients);
        final GetParameterResult getParameterResult = new GetParameterResult();
        getParameterResult.setParameter(parameter);

        when(mockDailyClaimStatsReportService.getPreviousDailyClaimStats(eq(previousDayCount)))
                .thenReturn(summaries);
        when(mockAwsProperties.getDailyClaimStatsMailingListKey()).thenReturn(expectedKey);
        when(mockSsmClient.getParameter(any())).thenReturn(getParameterResult);
        when(notificationProperties.getMailDailyClaimStatsSummaryTemplateId()).thenReturn(expectedTemplateId);
        when(mockCsvCreator.createCsv(eq(summaries))).thenReturn("a csv".getBytes(StandardCharsets.UTF_8));

        when(client.sendEmail(eq(expectedTemplateId), eq("one@mail.com"), anyMap(), anyString()))
                .thenReturn(new SendEmailResponse(MAIL_RESPONSE_ONE));
        when(client.sendEmail(eq(expectedTemplateId), eq("two@mail.com"), anyMap(), anyString()))
                .thenThrow(new NotificationClientException("Could not send email"));
        when(client.sendEmail(eq(expectedTemplateId), eq("three@mail.com"), anyMap(), anyString()))
                .thenThrow(new NotificationClientException("Could not send email"));
        when(client.sendEmail(eq(expectedTemplateId), eq("four@mail.com"), anyMap(), anyString()))
                .thenReturn(new SendEmailResponse(MAIL_RESPONSE_TWO));

        //Act
        final List<SendEmailResponse> actuals = notificationService.sendDailyClaimStatsSummaryMail(previousDayCount);

        //Assert
        assertThat(actuals).hasSize(expectedResponses);
        assertThat(actuals.get(0).getBody()).contains("RESPONSE_ONE");
        assertThat(actuals.get(1).getBody()).contains("RESPONSE_TWO");
        verify(client, times(expectedEmailRecipients)).sendEmail(eq(expectedTemplateId), anyString(), anyMap(), anyString());
    }

    private void givenWeGetTheClaimant(final boolean isEnglishContactPreference) {
        when(mockClaimant.getContactDetails().getNumber()).thenReturn(SOME_NUMBER);
        when(mockClaimant.getName().getFirstName()).thenReturn(SOME_FIRST_NAME);
        when(mockClaimant.getContactDetails().getEmail()).thenReturn(SOME_EMAIL_ADDRESS);
        when(mockClaimant.getDateOfClaim()).thenReturn(LocalDate.now());
        when(claimantServiceAdaptor.getClaimant(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(mockClaimant)));
        if (isEnglishContactPreference) {
            when(mockClaimant.getLanguagePreference()).thenReturn(ENGLISH_CONTACT_PREFERENCE);
        } else {
            when(mockClaimant.getLanguagePreference()).thenReturn(WELSH_CONTACT_PREFERENCE);
        }
    }
    private void givenWeGetTheCircumstances() {
        when(mockCircumstances.getClaimStartDate()).thenReturn(CLAIM_START_DATE);
        when(circumstancesServiceAdaptor.getCircumstancesByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(mockCircumstances)));
    }
    private void givenWeGetTheBankDetails() {
        when(bankDetailsServiceAdaptor.getBankDetailsByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(mockBankDetails)));
    }
    private void givenWeCantfindTheClaimant() {
        when(claimantServiceAdaptor.getClaimant(any())).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
    }
    private void givenWeCantFindTheCircumstances() {
        when(circumstancesServiceAdaptor.getCircumstancesByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
    }
    private void givenWeCantFindTheBankDetails() {
        when(bankDetailsServiceAdaptor.getBankDetailsByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
    }
    private void givenWeHaveEvidence() {
        when(evidenceFactory.create(any(),any())).thenReturn(evidence);
    }
    private void givenWeThrowANotificationClientExceptionWhenSendingAnSms() throws NotificationClientException {
        when(client.sendSms(any(),any(),any(),any())).thenThrow(NotificationClientException.class);
    }
    private void givenWeThrowANotificationClientExceptionWhenSendingAnEmail() throws NotificationClientException {
        when(client.sendEmail(any(),any(),any(),any())).thenThrow(NotificationClientException.class);
    }
    private void givenWeHaveTheSubmittedClaimsTallyRequest() {
        when(submittedClaimsTallyRequest.getTallyDate()).thenReturn(LocalDate.now());
    }
    private void givenWeHaveTheClaimStatsRequest() {
        when(claimStatsRequest.getClaimStatistics()).thenReturn(claimStatistics);
    }

    private void whenWeSendTheSms() throws NotificationClientException {
        notificationService.sendSms(request);
    }
    private void whenWeSendTheMail() throws InterruptedException, ExecutionException, NotificationClientException {
        notificationService.sendMail(request);
    }
    private void whenWeSendTheClaimCountMail() throws NotificationClientException {
        notificationService.sendClaimCountMail(submittedClaimsTallyRequest);
    }
    private void whenWeSendTheClaimStatsMail() throws NotificationClientException {
        notificationService.sendClaimStatsMail(claimStatsRequest);
    }

    private void whenWeSendTheProgressEmail() throws NotificationClientException {
        notificationService.sendProgressMail(request);
    }

    private void whenWeSendTheProgressSms() throws NotificationClientException {
        notificationService.sendProgressSMS(request);
    }

    private void whenWeSendTheSuccessSms() throws NotificationClientException, ExecutionException, InterruptedException {
        notificationService.sendClaimSuccessSms(request);
    }

    private void whenWeSendTheSuccessEmail() throws NotificationClientException, ExecutionException, InterruptedException {
        notificationService.sendClaimSuccessMail(request);
    }


    private void thenWeVerifyThatWeveCalledTheSms(final boolean isEnglishContactPreference) throws NotificationClientException {
        final String currContactPreference =
                isEnglishContactPreference ? SOME_SMS_TEMPLATE_ID_ENGLISH : SOME_SMS_TEMPLATE_ID_WELSH;
        verify(client, times(1)).sendSms(
                eq(currContactPreference), eq(SOME_NUMBER), any(), anyString());
    }
    private void thenWeVerifyThatWeveCalledTheMail() throws NotificationClientException {
        verify(client, times(1)).sendEmail(
                eq(SOME_MAIL_TEMPLATE_ID), eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }
    private void thenWeVerifyThatWeveCalledTheClaimCountMail() throws NotificationClientException {
        verify(client, times(1)).sendEmail(
                eq(SOME_MAIL_TEMPLATE_ID), eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }
    private void thenWeVerifyThatWeveCalledTheClaimStatsMail() throws NotificationClientException {
        verify(client, times(1)).sendEmail(
                eq(STATS_MAIL_TEMPLATE_ID), eq(STATS_EMAIL_ADDRESS), any(), anyString());
    }
    private void thenWeVerifyThatWeveCalledTheProgressSms(final boolean isEnglishContactPreference) throws NotificationClientException {
        final String currContactPreference =
                isEnglishContactPreference ? SOME_PROGRESS_SMS_TEMPLATE_ID_ENGLISH : SOME_PROGRESS_SMS_TEMPLATE_ID_WELSH;
        verify(client, times(1)).sendSms(eq(currContactPreference), eq(SOME_NUMBER), any(), anyString());
    }

    private void thenWeVerifyThatWeveCalledTheSuccessSms(final boolean isEnglishContactPreference) throws NotificationClientException {
        final String currContactPreference =
                isEnglishContactPreference ? SOME_SUCCESS_SMS_TEMPLATE_ID_ENGLISH : SOME_SUCCESS_SMS_TEMPLATE_ID_WELSH;
        verify(client, times(1)).sendSms(eq(currContactPreference), eq(SOME_NUMBER), any(), anyString());
    }
    private void thenWeVerifyThatWeveCalledTheProgressMailEnglish() throws NotificationClientException {
        verify(client, times(1)).sendEmail(
                eq(SOME_PROGRESS_MAIL_TEMPLATE_ID_ENGLISH), eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }

    private void thenWeVerifyThatWeveCalledTheSuccessMailEnglish() throws NotificationClientException {
        verify(client, times(1)).sendEmail(
                eq(SOME_PROGRESS_MAIL_TEMPLATE_ID_ENGLISH), eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }

    private void thenWeVerifyThatWeveCalledTheSuccessMailWelsh() throws NotificationClientException {
        verify(client, times(1)).sendEmail(eq(SOME_SUCCESS_MAIL_TEMPLATE_ID_WELSH),
                eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }

    private void thenWeVerifyThatWeveCalledTheProgressMailWelsh() throws NotificationClientException {
        verify(client, times(1)).sendEmail(eq(SOME_PROGRESS_MAIL_TEMPLATE_ID_WELSH),
                eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }

    private void thenWeVerifyThatWeveCalledTheProgressSms() throws NotificationClientException {
        verify(client, times(1)).sendSms(
                eq(SOME_PROGRESS_MAIL_TEMPLATE_ID_ENGLISH), eq(SOME_EMAIL_ADDRESS), any(), anyString());
    }
}
