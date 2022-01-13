package uk.gov.dwp.jsa.notification.service.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.http.api.ClaimStats;
import uk.gov.dwp.jsa.adaptors.http.api.NotificationRequest;
import uk.gov.dwp.jsa.adaptors.http.api.SubmittedClaimsTally;
import uk.gov.dwp.jsa.adaptors.services.ResponseBuilder;
import uk.gov.dwp.jsa.notification.service.config.WithVersionUriComponentsBuilder;
import uk.gov.dwp.jsa.notification.service.services.NotificationService;
import uk.gov.dwp.jsa.security.roles.ContactCentre;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromController;

@RestController
@RequestMapping("/nsjsa/" + WithVersionUriComponentsBuilder.VERSION_SPEL)
public class NotificationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    private final WithVersionUriComponentsBuilder uriBuilder;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(
            final WithVersionUriComponentsBuilder uriBuilder,
            final NotificationService notificationService) {
        this.uriBuilder = uriBuilder;
        this.notificationService = notificationService;
    }

    @PreAuthorize("!hasAnyAuthority('CCM', 'WC', 'SCA')")
    @PostMapping("/notification/mail/claim-confirmation")
    public ResponseEntity<ApiResponse<String>> sendMail(final HttpServletRequest servletRequest,
                                                        @RequestBody final NotificationRequest request)
            throws NotificationClientException, ExecutionException, InterruptedException {
        LOGGER.debug("Sending confirmation email for claimant: {}", request.getClaimantId());
        SendEmailResponse mailResponse = notificationService.sendMail(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/mail/claim-confirmation"),
                mailResponse.getReference().orElse(""),
                HttpStatus.OK
        );
    }

    @PreAuthorize("!hasAnyAuthority('CCM', 'WC', 'SCA')")
    @PostMapping("/notification/mail/mi-claims-count")
    public ResponseEntity<ApiResponse<String>> sendClaimCountMail(final HttpServletRequest servletRequest,
                                                                  @RequestBody final SubmittedClaimsTally request)
            throws NotificationClientException {
        LOGGER.debug("Sending claim count report email");
        SendEmailResponse mailResponse = notificationService.sendClaimCountMail(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/mail/mi-claims-count"),
                mailResponse.getReference().orElse(""),
                HttpStatus.OK
        );
    }

    @PreAuthorize("!hasAnyAuthority('CCM', 'WC', 'SCA')")
    @PostMapping("/notification/sms/claim-confirmation")
    public ResponseEntity<ApiResponse<String>> sendSms(final HttpServletRequest servletRequest,
                                                       @RequestBody final NotificationRequest request)
            throws NotificationClientException {
        LOGGER.debug("Sending confirmation sms for claimant: {}", request.getClaimantId());
        SendSmsResponse smsResponse = notificationService.sendSms(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/sms/claim-confirmation"),
                smsResponse.getReference().orElse(""),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('CCM')")
    @PostMapping("/notification/mail/mi-claim-stats")
    public ResponseEntity<ApiResponse<String>> sendClaimStatsMail(final HttpServletRequest servletRequest,
                                                       @RequestBody final ClaimStats request
    )
            throws NotificationClientException {
        LOGGER.debug("Sending claim stats report email");
        SendEmailResponse mailResponse = notificationService.sendClaimStatsMail(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/mail/mi-claim-stats"),
                mailResponse.getReference().orElse(""),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('SCA')")
    @PostMapping("/notification/mail/claim-progress")
    public ResponseEntity<ApiResponse<String>> sendClaimProgressMail(final HttpServletRequest servletRequest,
                                                                     @RequestBody final NotificationRequest request)
            throws NotificationClientException {
        LOGGER.debug("Sending claim progress email");
        SendEmailResponse emailResponse = notificationService.sendProgressMail(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/mail/claim-progress"),
                emailResponse.getReference().orElse(""),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('SCA')")
    @PostMapping("/notification/sms/claim-progress")
    public ResponseEntity<ApiResponse<String>> sendClaimProgressSms(final HttpServletRequest servletRequest,
                                                                    @RequestBody final NotificationRequest request)
            throws NotificationClientException {
        LOGGER.debug("Sending claim progress sms");
        SendSmsResponse emailResponse = notificationService.sendProgressSMS(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/sms/claim-progress"),
                emailResponse.getReference().orElse(""),
                HttpStatus.OK);
    }

    @ContactCentre
    @PostMapping("/notification/sms/claimant-success")
    public ResponseEntity<ApiResponse<String>> sendClaimSuccessSms(final HttpServletRequest servletRequest,
                                                                    @RequestBody final NotificationRequest request
    ) throws NotificationClientException, ExecutionException, InterruptedException {
        LOGGER.debug("Sending success sms for claimant: {}", request.getClaimantId());
        SendSmsResponse smsResponse = notificationService.sendClaimSuccessSms(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/sms/claimant-success"),
                smsResponse.getReference().orElse(""),
                HttpStatus.OK);
    }

    @ContactCentre
    @PostMapping("/notification/mail/claimant-success")
    public ResponseEntity<ApiResponse<String>> sendClaimSuccessMail(final HttpServletRequest servletRequest,
                                                                    @RequestBody final NotificationRequest request)
            throws NotificationClientException, ExecutionException, InterruptedException {
        LOGGER.debug("Sending success email for claimant: {}", request.getClaimantId());
        SendEmailResponse mailResponse = notificationService.sendClaimSuccessMail(request);
        return buildSuccessfulResponse(
                buildResourceUriFor("/notification/mail/claimant-success"),
                mailResponse.getReference().orElse(""),
                HttpStatus.OK
        );
    }

    private String buildResourceUriFor(final String path) {
        return fromController(uriBuilder, getClass())
                .path(path)
                .build()
                .toUri().getPath();
    }

    private <T> ResponseEntity<ApiResponse<T>> buildSuccessfulResponse(
            final String path,
            final T objectToReturn,
            final HttpStatus status) {
        return new ResponseBuilder<T>()
                .withStatus(status)
                .withSuccessData(URI.create(path), objectToReturn)
                .build();
    }
}
