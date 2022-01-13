package uk.gov.dwp.jsa.notification.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.RestfulExecutor;
import uk.gov.dwp.jsa.adaptors.dto.claim.BankDetails;
import uk.gov.dwp.jsa.adaptors.dto.claim.Claimant;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.adaptors.dto.claim.LanguagePreference;
import uk.gov.dwp.jsa.adaptors.http.api.ClaimStats;
import uk.gov.dwp.jsa.adaptors.http.api.NotificationRequest;
import uk.gov.dwp.jsa.adaptors.http.api.SubmittedClaimsTally;
import uk.gov.dwp.jsa.notification.service.config.NotificationProperties;
import uk.gov.dwp.jsa.notification.service.exceptions.ClaimantByIdNotFoundException;
import uk.gov.dwp.jsa.notification.service.services.evidence.Evidence;
import uk.gov.dwp.jsa.notification.service.services.evidence.EvidenceFactory;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    private NotificationProperties notificationProperties;
    private NotificationClient client;
    private ClaimantServiceAdaptor claimantServiceAdaptor;
    private CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    private BankDetailsServiceAdaptor bankDetailsServiceAdaptor;
    private EvidenceFactory evidenceFactory;

    @Autowired
    public NotificationService(final NotificationProperties notificationProperties,
                               final NotificationClient client,
                               final ClaimantServiceAdaptor claimantServiceAdaptor,
                               final CircumstancesServiceAdaptor circumstancesServiceAdaptor,
                               final BankDetailsServiceAdaptor bankDetailsServiceAdaptor,
                               final EvidenceFactory evidenceFactory) {
        this.notificationProperties = notificationProperties;
        this.client = client;
        this.claimantServiceAdaptor = claimantServiceAdaptor;
        this.circumstancesServiceAdaptor = circumstancesServiceAdaptor;
        this.bankDetailsServiceAdaptor = bankDetailsServiceAdaptor;
        this.evidenceFactory = evidenceFactory;
    }

    public SendEmailResponse sendMail(final NotificationRequest request)
            throws NotificationClientException, ExecutionException, InterruptedException {
        CompletableFuture<Optional<Claimant>> claimantCall =
            claimantServiceAdaptor.getClaimant(request.getClaimantId())
                .exceptionally(ex -> RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));

        CompletableFuture<Optional<Circumstances>> circumstancesCall =
            circumstancesServiceAdaptor.getCircumstancesByClaimantId(request.getClaimantId())
                .exceptionally(ex -> RestfulExecutor.circumstancesExceptionally(ex,
                        request.getClaimantId().toString()));

        CompletableFuture<Optional<BankDetails>> bankDetailsCall =
            bankDetailsServiceAdaptor.getBankDetailsByClaimantId(request.getClaimantId())
                .exceptionally(ex -> RestfulExecutor.bankDetailsExceptionally(ex, request.getClaimantId().toString()));

        CompletableFuture.allOf(claimantCall, circumstancesCall, bankDetailsCall).join();

        final Optional<Claimant> claimantOptional = claimantCall.get();
        final Optional<Circumstances> circumstancesOptional = circumstancesCall.get();
        if (claimantOptional.isPresent() && circumstancesOptional.isPresent()) {
            Claimant claimant = claimantOptional.get();
            Circumstances circumstances = circumstancesOptional.get();
            BankDetails bankDetails = null;
            Optional<BankDetails> bankDetailsOptional = bankDetailsCall.get();
            if (bankDetailsOptional.isPresent()) {
                bankDetails = bankDetailsOptional.get();
            }

            Evidence evidence = evidenceFactory.create(circumstances, bankDetails);

            String templateId =
                    getEmailTemplateId(claimantOptional.get().getLanguagePreference());

            MailRequest mailRequest = new MailRequest.MailRequestBuilder(
                    templateId,
                    claimant.getContactDetails().getEmail(),
                    claimant.getName().getFirstName(),
                    claimant.getName().getLastName(),
                    claimant.getDateOfClaim(),
                    circumstances.getClaimStartDate(),
                    evidence,
                    UUID.randomUUID()).build();
            return client.sendEmail(
                    mailRequest.getTemplateId(),
                    mailRequest.getEmailAddress(),
                    mailRequest.getPersonalisation(),
                    mailRequest.getReference().toString());
        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendEmailResponse sendClaimCountMail(final SubmittedClaimsTally request) throws NotificationClientException {
        MailCountRequest mailRequest = new MailCountRequest.MailCountRequestBuilder(
                notificationProperties.getMailCountTemplateId(),
                notificationProperties.getMailCountAddress(),
                request,
                UUID.randomUUID()).build();
        return client.sendEmail(
                mailRequest.getTemplateId(),
                mailRequest.getEmailAddress(),
                mailRequest.getPersonalisation(),
                mailRequest.getReference().toString());
    }

    public SendEmailResponse sendProgressMail(final NotificationRequest request) throws NotificationClientException {
        CompletableFuture<Optional<Claimant>> claimantCall =
                claimantServiceAdaptor.getClaimant(request.getClaimantId())
                        .exceptionally(ex ->
                                RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));

        final Optional<Claimant> claimantOptional = claimantCall.join();

        if (claimantOptional.isPresent()) {
            Claimant claimant = claimantOptional.get();
            String templateId = getProgressEmailTemplateId(claimantOptional.get().getLanguagePreference());
            MailClaimProgressRequest mailRequest = new MailClaimProgressRequest.MailClaimProgressRequestBuilder(
                    templateId,
                    claimant.getContactDetails().getEmail(),
                    claimant.getName().getFirstName(),
                    claimant.getName().getLastName(),
                    UUID.randomUUID()).build();
            return client.sendEmail(
                    mailRequest.getTemplateId(),
                    mailRequest.getProgressEmailAddress(),
                    mailRequest.getPersonalisation(),
                    mailRequest.getProgressReference().toString());

        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendSmsResponse sendProgressSMS(final NotificationRequest request)  throws NotificationClientException {
        CompletableFuture<Optional<Claimant>> claimantCall =
                claimantServiceAdaptor.getClaimant(request.getClaimantId())
                        .exceptionally(ex ->
                                RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));

        final Optional<Claimant> claimantOptional = claimantCall.join();

        if (claimantOptional.isPresent()) {
            Claimant claimant = claimantOptional.get();
            String templateId = getProgressSmsTemplateId(claimant.getLanguagePreference());
            SmsRequest smsRequest = new SmsRequest.SmsRequestBuilder(
                    templateId,
                    claimant.getContactDetails().getNumber(),
                    claimant.getName().getFirstName(),
                    UUID.randomUUID()).build();
            return client.sendSms(
                    smsRequest.getTemplateId(),
                    smsRequest.getPhoneNumber(),
                    smsRequest.getPersonalisation(),
                    smsRequest.getReference().toString());
        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendSmsResponse sendSms(final NotificationRequest request) throws NotificationClientException {
        CompletableFuture<Optional<Claimant>> claimantCall =
            claimantServiceAdaptor.getClaimant(request.getClaimantId())
                .exceptionally(ex -> RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));
        final Optional<Claimant> claimant = claimantCall.join();

        if (claimant.isPresent()) {
            String templateId = getSmsTemplateId(claimant.get().getLanguagePreference());
            SmsRequest smsRequest = new SmsRequest.SmsRequestBuilder(
                    templateId,
                    claimant.get().getContactDetails().getNumber(),
                    claimant.get().getName().getFirstName(),
                    UUID.randomUUID()).build();
            return client.sendSms(
                    smsRequest.getTemplateId(),
                    smsRequest.getPhoneNumber(),
                    smsRequest.getPersonalisation(),
                    smsRequest.getReference().toString());
        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendSmsResponse sendClaimSuccessSms(final NotificationRequest request)
            throws NotificationClientException, ExecutionException, InterruptedException {
        CompletableFuture<Optional<Claimant>> claimaintCall =
                claimantServiceAdaptor.getClaimant(request.getClaimantId())
                .exceptionally(ex -> RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));
        final Optional<Claimant> claimant = claimaintCall.join();
        if (claimant.isPresent()) {
            String templateId = getSmsSuccessTemplateId(claimant.get().getLanguagePreference());
            SmsRequest smsRequest = new SmsRequest.SmsRequestBuilder(
                    templateId,
                    claimant.get().getContactDetails().getNumber(),
                    claimant.get().getName().getFirstName(),
                    UUID.randomUUID()).build();
            return client.sendSms(
                    smsRequest.getTemplateId(),
                    smsRequest.getPhoneNumber(),
                    smsRequest.getPersonalisation(),
                    smsRequest.getReference().toString());
        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendEmailResponse sendClaimSuccessMail(final NotificationRequest request)
            throws NotificationClientException, ExecutionException, InterruptedException {
        CompletableFuture<Optional<Claimant>> claimantCall =
                claimantServiceAdaptor.getClaimant(request.getClaimantId())
                        .exceptionally(ex ->
                                RestfulExecutor.claimantExceptionally(ex, request.getClaimantId().toString()));

        final Optional<Claimant> claimantOptional = claimantCall.join();

        if (claimantOptional.isPresent()) {
            Claimant claimant = claimantOptional.get();
            String templateId = getEmailSuccessTemplateId(claimantOptional.get().getLanguagePreference());
            MailClaimSuccessRequest mailRequest = new MailClaimSuccessRequest.MailClaimSuccessRequestBuilder(
                    templateId,
                    claimant.getContactDetails().getEmail(),
                    claimant.getName().getFirstName(),
                    claimant.getName().getLastName(),
                    UUID.randomUUID()).build();
            return client.sendEmail(
                    mailRequest.getTemplateId(),
                    mailRequest.getSuccessEmailAddress(),
                    mailRequest.getPersonalisation(),
                    mailRequest.getSuccessReference().toString());
        }
        throw new ClaimantByIdNotFoundException();
    }

    public SendEmailResponse sendClaimStatsMail(final ClaimStats request) throws NotificationClientException {
        MailStatsRequest mailStatsRequest = new MailStatsRequest.MailStatsRequestBuilder(request).build();
        String templateId = notificationProperties.getMailStatsTemplateId();
        String email = notificationProperties.getMailStatsAddress();
        String reference = UUID.randomUUID().toString();

        return client.sendEmail(templateId, email, mailStatsRequest.getPersonalisation(), reference);
    }

    private String getEmailTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getMailTemplateIdWelsh();
        }
        return notificationProperties.getMailTemplateIdEnglish();
    }

    private String getSmsTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getSmsTemplateIdWelsh();
        }
        return notificationProperties.getSmsTemplateIdEnglish();
    }

    private String getProgressEmailTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getMailProgressTemplateIdWelsh();
        }
        return notificationProperties.getMailProgressTemplateIdEnglish();
    }

    private String getProgressSmsTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getSmsProgressTemplateIdWelsh();
        }
        return notificationProperties.getSmsProgressTemplateIdEnglish();
    }

    private String getSmsSuccessTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getSmsSuccessTemplateIdWelsh();
        }
        return notificationProperties.getSmsSuccessTemplateIdEnglish();
    }

    private String getEmailSuccessTemplateId(final LanguagePreference languagePreference) {
        if (isWelshContactPreference(languagePreference)) {
            return notificationProperties.getMailSuccessTemplateIdWelsh();
        }
        return notificationProperties.getMailSuccessTemplateIdEnglish();
    }

    private boolean isWelshContactPreference(final LanguagePreference languagePreference) {
        return Boolean.TRUE.equals(languagePreference.getWelshContact());
    }
}
