package uk.gov.dwp.jsa.notification.service.services;

import uk.gov.dwp.jsa.adaptors.dto.claim.ClaimStatistics;
import uk.gov.dwp.jsa.adaptors.http.api.ClaimStats;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MailStatsRequest {

    public static final String CASES_RECEIVED_DAY = "caseReceivedDay";
    public static final String HEAD_OF_WORK = "headOfWork";
    public static final String OLDEST_OPEN = "oldestOpenClaim";
    public static final String CASES_CLEARED_IN_DAY = "casesClearedDay";
    public static final String PERCENTAGE_CLOSED_DAY_IN_24HR = "percentageClosedDay24Hr";
    public static final String PERCENTAGE_CLOSED_DAY_IN_48HR = "percentageClosedDay48Hr";
    public static final String PERCENTAGE_CLOSED_WEEK_IN_24HR = "percentageClosedWeek24Hr";
    public static final String PERCENTAGE_CLOSED_WEEK_IN_48HR = "percentageClosedWeek48Hr";
    public static final String CASES_OUTSIDE_24HR_KPI = "caseOutside24hrKpi";
    public static final String CASE_OUTSIDE_48HR_KPI = "caseOutside48hrKpi";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    private Map<String, String> personalisation;

    public MailStatsRequest(final MailStatsRequestBuilder builder) {

        ClaimStatistics claimStatistics = builder.claimStats.getClaimStatistics();
        personalisation = new HashMap<>();
        personalisation.put(CASES_RECEIVED_DAY,
                String.valueOf(claimStatistics.getCasesReceivedInDay()));
        personalisation.put(HEAD_OF_WORK,
                String.valueOf(claimStatistics.getHeadOfWork()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        if (claimStatistics.getOldestClaimOpen() != null) {
            personalisation.put(OLDEST_OPEN,
                claimStatistics.getOldestClaimOpen().format(formatter));
        } else {
            personalisation.put(OLDEST_OPEN, "");
        }
        personalisation.put(CASES_CLEARED_IN_DAY,
                String.valueOf(claimStatistics.getCasesClearedInDay()));
        personalisation.put(PERCENTAGE_CLOSED_DAY_IN_24HR,
                String.format("%.2f", claimStatistics.getPercentageOfClaimsInDayClosedIn24hr()) + "%");
        personalisation.put(PERCENTAGE_CLOSED_DAY_IN_48HR,
                String.format("%.2f", claimStatistics.getPercentageOfClaimsInDayClosedIn48hr()) + "%");
        personalisation.put(PERCENTAGE_CLOSED_WEEK_IN_24HR,
                String.format("%.2f", claimStatistics.getPercentageOfClaimsInWeekClosedIn24hr()) + "%");
        personalisation.put(PERCENTAGE_CLOSED_WEEK_IN_48HR,
                String.format("%.2f", claimStatistics.getPercentageOfClaimsInWeekClosedIn48hr()) + "%");
        personalisation.put(CASES_OUTSIDE_24HR_KPI,
                String.valueOf(claimStatistics.getCasesOutstandingOutside24hrKpi()));
        personalisation.put(CASE_OUTSIDE_48HR_KPI,
                String.valueOf(claimStatistics.getCasesOutstandingOutside48hrKpi()));

    }

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public static class MailStatsRequestBuilder {
        private ClaimStats claimStats;

        public MailStatsRequestBuilder(final ClaimStats claimStats) {
            this.claimStats = claimStats;
        }

        public MailStatsRequest build() {
            return new MailStatsRequest(this);
        }
    }
}
