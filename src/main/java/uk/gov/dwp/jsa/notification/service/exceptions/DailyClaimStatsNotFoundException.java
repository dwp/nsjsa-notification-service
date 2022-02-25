package uk.gov.dwp.jsa.notification.service.exceptions;


/**
 * Exception representing when there are no daily claim statistics found.
 */
public class DailyClaimStatsNotFoundException extends RuntimeException {
    public DailyClaimStatsNotFoundException(final String message) {
        super(message);
    }

    public DailyClaimStatsNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
