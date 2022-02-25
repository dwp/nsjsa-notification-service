package uk.gov.dwp.jsa.notification.service.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.ContactDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.Name;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.LanguagePreference;
import uk.gov.dwp.jsa.adaptors.http.api.NotificationRequest;
import uk.gov.dwp.jsa.notification.service.Application;
import uk.gov.dwp.jsa.notification.service.config.NotificationAwsSsmProperties;
import uk.gov.dwp.jsa.notification.service.services.SmsRequest;
import uk.gov.dwp.jsa.notification.service.services.evidence.EvidenceFactory;
import uk.gov.dwp.jsa.security.WithMockUser;
import uk.gov.dwp.jsa.security.roles.Role;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Application.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local_test")
public class NotificationTest {

    private static final String MAIL_NOTIFICATION_PATH = "/nsjsa/v1/notification/mail/claim-confirmation";
    private static final String SMS_NOTIFICATION_PATH = "/nsjsa/v1/notification/sms/claim-confirmation";
    private static final String SUCCESS = "success";

    private static final String MAIL_RESPONSE = "{\"content\":{\"body\":\"#Dear person\\r\\n\\r\\nYour online application for Jobseeker...\",\"from_email\":\"new.style.jsa@notifications.service.gov.uk\",\"subject\":\"New style Jobseeker\\u2019s Allowance \\u2013 application received\"},\"id\":\"a8e03f45-3900-4c74-bfec-d2306d2eeb2e\",\"reference\":\"8649139a-7774-44eb-9c0b-326927415755\",\"scheduled_for\":null,\"template\":{\"id\":\"def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/def632cd-387e-4a35-b72a-23ee4a33c2e8\",\"version\":28},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/a8e03f45-3900-4c74-bfec-d2306d2eeb2e\"}";
    private static final String SMS_RESPONSE = "{\"content\":{\"body\":\"Dear person. Following up on your New Style JSA application ...\",\"from_number\":\"GOVUK\"},\"id\":\"e6e93f93-c156-4ce1-9c1b-463e6156e7de\",\"reference\":null,\"scheduled_for\":null,\"template\":{\"id\":\"cfc1713a-ba70-4b22-98e5-bf55762cea9c\",\"uri\":\"https://api.notifications.service.gov.uk/services/458f9f73-84ab-4546-8e77-b15cbea1f8db/templates/cfc1713a-ba70-4b22-98e5-bf55762cea9c\",\"version\":4},\"uri\":\"https://api.notifications.service.gov.uk/v2/notifications/e6e93f93-c156-4ce1-9c1b-463e6156e7de\"}\n";

    private static final String SMS_TEMPLATE_ID = "cfc1713a-ba70-4b22-98e5-bf55762cea9c";
    private static final String MAIL_TEMPLATE_ID = "def632cd-387e-4a35-b72a-23ee4a33c2e8";

    private static final String SOME_FIRST_NAME = "some first name";
    private static final String SOME_LAST_NAME = "some last name";
    private static final String SOME_NUMBER = "123456789";
    private static final String SOME_EMAIL = "some email";

    private static final UUID CLAIMANT_ID = UUID.randomUUID();
    private static final NotificationRequest REQUEST = new NotificationRequest(CLAIMANT_ID);
    private static final Map<String, String> SMS_PERSONALISATION = getSmsPersonalisation();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EvidenceFactory evidenceFactory;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private NotificationClient client;
    @MockBean
    private ClaimantServiceAdaptor claimantServiceAdaptor;
    @MockBean
    private CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    @MockBean
    private BankDetailsServiceAdaptor bankDetailsServiceAdaptor;

    @Autowired
    private NotificationAwsSsmProperties notificationAwsSsmProperties;

    @Before
    public void setup() {
    }

    @Test
    @WithMockUser
    public void testThatICanSuccessfullySendAMail() throws Exception {
        givenWeGetTheClaimant();
        givenWeGetTheCircumstances();
        givenWeGetTheBankDetails();
        givenWeHaveAMailResponse();
        whenISendTheMailThenIExpectTheResponseToBeCorrect();
    }

    @Test
    @WithMockUser(role = Role.CCA)
    public void testThatICanSuccessfullySendASms() throws Exception {
        givenWeGetTheClaimant();
        givenWeHaveASmsResponse();
        whenISendTheSmsThenIExpectTheResponseToBeCorrect();
    }

    private void givenWeGetTheClaimant() {
        when(claimantServiceAdaptor.getClaimant(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(getClaimant())));
    }

    private void givenWeGetTheCircumstances() {
        when(circumstancesServiceAdaptor.getCircumstancesByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(getCircumstances())));
    }

    private void givenWeGetTheBankDetails() {
        when(bankDetailsServiceAdaptor.getBankDetailsByClaimantId(CLAIMANT_ID)).thenReturn(CompletableFuture.completedFuture(Optional.of(getBankDetails())));
    }

    private void givenWeHaveAMailResponse() throws NotificationClientException {
        when(client.sendEmail(eq(MAIL_TEMPLATE_ID), anyString(), any(HashMap.class), anyString()))
                .thenReturn(new SendEmailResponse(MAIL_RESPONSE));
    }

    private void givenWeHaveASmsResponse() throws NotificationClientException {
        when(client.sendSms(eq(SMS_TEMPLATE_ID), eq(SOME_NUMBER), eq(SMS_PERSONALISATION), anyString()))
                .thenReturn(new SendSmsResponse(SMS_RESPONSE));
    }

    private void whenISendTheMailThenIExpectTheResponseToBeCorrect() throws Exception {
        mockMvc.perform(post(MAIL_NOTIFICATION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SUCCESS)))
                .andExpect(content().string(containsString(MAIL_NOTIFICATION_PATH)));
    }

    private void whenISendTheSmsThenIExpectTheResponseToBeCorrect() throws Exception {
        mockMvc.perform(post(SMS_NOTIFICATION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(REQUEST)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(SUCCESS)))
                .andExpect(content().string(containsString(SMS_NOTIFICATION_PATH)));
    }

    private <T> String toJson(T objectToConverted) throws JsonProcessingException {
        return mapper.writeValueAsString(objectToConverted);
    }

    private static Claimant getClaimant() {
        final Claimant claimant = new Claimant();
        claimant.setName(new Name(null, SOME_FIRST_NAME, SOME_LAST_NAME));
        claimant.setContactDetails(new ContactDetails(SOME_NUMBER, SOME_EMAIL, true, true));
        claimant.setDateOfClaim(LocalDate.now());
        LanguagePreference languagePreference = new LanguagePreference(false, false);
        claimant.setLanguagePreference(languagePreference);
        return claimant;
    }

    private static Map<String, String> getSmsPersonalisation() {
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put(SmsRequest.FIRST_NAME, SOME_FIRST_NAME);
        return personalisation;
    }

    private static Circumstances getCircumstances() {
        Circumstances circumstances = new Circumstances();
        ReflectionTestUtils.setField(circumstances, "claimStartDate", LocalDate.now());
        circumstances.setDateOfClaim(LocalDate.now());
        return circumstances;
    }

    private static BankDetails getBankDetails() {
        BankDetails bankDetails = new BankDetails();
        return bankDetails;
    }
}
