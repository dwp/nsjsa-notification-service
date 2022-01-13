package uk.gov.dwp.jsa.notification.service.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.services.ResponseBuilder;
import uk.gov.service.notify.NotificationClientException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(ClaimantByIdNotFoundException.class)
    public final @ResponseBody
    ResponseEntity<ApiResponse<String>> handleClaimantByIdNotFoundException(
            final Exception ex,
            final WebRequest request
    ) {
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.NOT_FOUND)
                .withApiError(
                        ClaimantByIdNotFoundException.CODE,
                        ClaimantByIdNotFoundException.MESSAGE
                ).build();
    }

    @ExceptionHandler(NotificationClientException.class)
    public final @ResponseBody
    ResponseEntity<ApiResponse<String>> handleNotificationClientException(
            final NotificationClientException ex,
            final WebRequest request
    ) {
        LOGGER.error("Error communicating with the GDS Notify service.", ex);
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.BAD_GATEWAY)
                .withApiError(
                        String.valueOf(ex.getHttpResult()),
                        ex.getMessage()
                ).build();
    }
}
