package uk.gov.dwp.jsa.notification.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.ValidationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.ClaimStatistics;
import uk.gov.dwp.jsa.notification.service.exceptions.DailyClaimStatsNotFoundException;
import uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Responsible for gathering and calculating the claim statistics used in reports.
 */
@Service
public class DailyClaimStatsReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyClaimStatsReportService.class);

    private final Clock clock;
    private final ValidationServiceAdaptor validationServiceAdaptor;

    public DailyClaimStatsReportService(final Clock clock,
                                        final ValidationServiceAdaptor validationServiceAdaptor) {
        this.clock = clock;
        this.validationServiceAdaptor = validationServiceAdaptor;
    }

    /**
     * Gets the daily claim statistics for the previous days. This method is exclusive of the current day and will not
     * include the current day statistics as they will be incomplete.
     *
     * @param previousDayCount the number of previous days to get the claim statistics for.
     *
     * @return claim statistics split by date for previous days in date ascending order
     */
    public List<DailyClaimStatsSummary> getPreviousDailyClaimStats(final int previousDayCount) {
        return getAllClaimStatistics(previousDayCount);
    }

    /**
     * Gets the claim statistics from respective sources and calculates statistics values.
     *
     * @param previousDayCount the number of previous days to get the claim statistics for.
     *
     * @return previous days claim statistics
     */
    private List<DailyClaimStatsSummary> getAllClaimStatistics(final int previousDayCount) {
        final List<DailyClaimStatsSummary> summaries = new ArrayList<>();

        //For each of the previous days excluding the current day, get the claim statistics
        for (int i = 1; i <= previousDayCount; i++) {
            //Resolve date to get
            final LocalDate dateToRetrieve = LocalDate.now(clock).minusDays(i);
            LOGGER.debug("Getting claim statistics for {}", dateToRetrieve);

            //Get the claim statistics for the resolved date
            final ClaimStatistics claimStatistics = validationServiceAdaptor.getClaimStatistics(dateToRetrieve)
                    .orElseThrow(() -> new DailyClaimStatsNotFoundException("Unable to get daily stats for "
                            + dateToRetrieve.format(DateTimeFormatter.ISO_LOCAL_DATE)));

            /*
             * Create an initial summary object with the values from the service but with 0 as the weekly total as
             * is calculated after
             */
            final DailyClaimStatsSummary summary = new DailyClaimStatsSummary(
                    dateToRetrieve,
                    claimStatistics.getCasesReceivedInDay(),
                    claimStatistics.getHeadOfWork(),
                    claimStatistics.getCasesClearedInDay(),
                    claimStatistics.getAssistedDigitalClaimCount(),
                    claimStatistics.getCasesReceivedInDay() + claimStatistics.getAssistedDigitalClaimCount(),
                    0
            );
            summaries.add(summary);
        }
        calculateWeeklyTotals(summaries);
        return summaries;
    }

    /**
     * Calculates the weekly totals for all provided summaries. Weekly totals are calculated where Monday is
     * the beginning of a week.
     *
     * @param summaries summaries to operate on
     */
    private void calculateWeeklyTotals(final List<DailyClaimStatsSummary> summaries) {
        /*
         * Sort the summaries by date ascending order. We need to do this as the weekly totals are an accumulation up
         * to a particular date within a week i.e. Tuesday's total is an accumulation of Monday and Tuesday totals and
         * so on as the week progresses to Sunday.
         */
        summaries.sort(Comparator.comparing(DailyClaimStatsSummary::getDateOfCapture));

        //Split the dates by week start date, where a week start is a Monday
        final Map<LocalDate, List<DailyClaimStatsSummary>> summariesByWeekStartDate = summaries.stream()
                .collect(Collectors.groupingBy(getClaimSummaryWeekStartDate()));

        //Iterate over all dates on a calendar weekly (starting Monday) basis
        summariesByWeekStartDate.forEach((weekStartDate, dailySummaries) -> {
            //Rolling total for weekly count to grow over every iteration
            final AtomicInteger rollingWeeklyTotal = new AtomicInteger(0);

            //Iterate over all the summary/summaries in a calendar week, setting each weekly total value
            dailySummaries.forEach(currentSummary -> {
                rollingWeeklyTotal.set(rollingWeeklyTotal.get() + currentSummary.getTotalClaims());
                currentSummary.setWeeklyTotal(rollingWeeklyTotal.get());
            });
        });
    }

    /**
     * Obtains the week start date of a given date where Monday is the beginning of the week.
     *
     * @return week start date of a claim summary
     */
    private Function<DailyClaimStatsSummary, LocalDate> getClaimSummaryWeekStartDate() {
        /*
         * To calculate the start date of a week:
         * date - day of week (1 indexed from Monday) number of days + 1 day
         */
        return (DailyClaimStatsSummary summary) ->
                summary.getDateOfCapture().minusDays(summary.getDateOfCapture().getDayOfWeek().getValue()).plusDays(1L);
    }
}
