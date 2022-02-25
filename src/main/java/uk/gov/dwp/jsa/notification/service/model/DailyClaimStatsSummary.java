package uk.gov.dwp.jsa.notification.service.model;

import java.time.LocalDate;
import java.util.Objects;


/**
 * Represents the daily statistics of all claims in the application.
 */
public class DailyClaimStatsSummary {
    private final LocalDate dateOfCapture;
    private final int onlineClaimsMade;
    private final int headOfWork;
    private final int claimsCleared;
    private final int assistedDigitalClaimCount;
    private final int totalClaims;
    private int weeklyTotal;

    public DailyClaimStatsSummary(final LocalDate dateOfCapture, final int onlineClaimsMade, final int headOfWork,
                                  final int claimsCleared, final int assistedDigitalClaimCount,
                                  final int totalClaims, final int weeklyTotal) {
        this.dateOfCapture = dateOfCapture;
        this.onlineClaimsMade = onlineClaimsMade;
        this.headOfWork = headOfWork;
        this.claimsCleared = claimsCleared;
        this.assistedDigitalClaimCount = assistedDigitalClaimCount;
        this.totalClaims = totalClaims;
        this.weeklyTotal = weeklyTotal;
    }

    public LocalDate getDateOfCapture() {
        return dateOfCapture;
    }

    public int getOnlineClaimsMade() {
        return onlineClaimsMade;
    }

    public int getHeadOfWork() {
        return headOfWork;
    }

    public int getClaimsCleared() {
        return claimsCleared;
    }

    public int getAssistedDigitalClaimCount() {
        return assistedDigitalClaimCount;
    }

    public int getTotalClaims() {
        return totalClaims;
    }

    public int getWeeklyTotal() {
        return weeklyTotal;
    }

    public void setWeeklyTotal(final int weeklyTotal) {
        this.weeklyTotal = weeklyTotal;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DailyClaimStatsSummary summary = (DailyClaimStatsSummary) other;
        return onlineClaimsMade == summary.onlineClaimsMade && headOfWork == summary.headOfWork
                && claimsCleared == summary.claimsCleared
                && assistedDigitalClaimCount == summary.assistedDigitalClaimCount
                && totalClaims == summary.totalClaims && weeklyTotal == summary.weeklyTotal
                && Objects.equals(dateOfCapture, summary.dateOfCapture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateOfCapture, onlineClaimsMade, headOfWork, claimsCleared, assistedDigitalClaimCount,
                totalClaims, weeklyTotal);
    }

    @Override
    public String toString() {
        return "DailyClaimStatsSummary{"
                + "dateOfCapture=" + dateOfCapture
                + ", onlineClaimsMade=" + onlineClaimsMade
                + ", headOfWork=" + headOfWork
                + ", claimsCleared=" + claimsCleared
                + ", assistedDigitalClaimCount=" + assistedDigitalClaimCount
                + ", totalClaims=" + totalClaims
                + ", weeklyTotal=" + weeklyTotal
                + '}';
    }
}
