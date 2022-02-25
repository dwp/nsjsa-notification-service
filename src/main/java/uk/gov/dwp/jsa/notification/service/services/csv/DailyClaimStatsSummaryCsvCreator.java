package uk.gov.dwp.jsa.notification.service.services.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.notification.service.exceptions.DailyClaimStatsNotFoundException;
import uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Creates a CSV based on {@link uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary}.
 */
@Component
public class DailyClaimStatsSummaryCsvCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyClaimStatsSummaryCsvCreator.class);

    private static final String[] HEADERS = new String[] {
            "Date",
            "Online Claims made (24 hour total)",
            "HoW at point of data capture",
            "Claims cleared from HoW (last 24hrs)",
            "Assisted digital",
            "Total Claims",
            "Weekly Total"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(HEADERS).build();

    /**
     * Creates a CSV from the provided summaries. This method makes no assumption about ordering of records and will
     * write them to the CSV in the order provided. If sorting is required it should be done before calling this.
     *
     * @param summaries the summaries to print to the CSV
     *
     * @return byte array containing the CSV data or if an error occurs {@link DailyClaimStatsNotFoundException} is
     * thrown.
     */
    public byte[] createCsv(final List<DailyClaimStatsSummary> summaries) {

        try (
                final StringWriter csvOutput = new StringWriter();
                final CSVPrinter csvPrinter = new CSVPrinter(csvOutput, CSV_FORMAT)) {

            for (final DailyClaimStatsSummary summary : summaries) {
                csvPrinter.printRecord(
                        summary.getDateOfCapture().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        summary.getOnlineClaimsMade(),
                        summary.getHeadOfWork(),
                        summary.getClaimsCleared(),
                        summary.getAssistedDigitalClaimCount(),
                        summary.getTotalClaims(),
                        summary.getWeeklyTotal()
                );
            }
            csvPrinter.flush();
            return csvOutput.toString().getBytes(StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            LOGGER.error("Error creating CSV for daily claim stats", exception);
            throw new DailyClaimStatsNotFoundException("Unable to generate daily claim stats CSV", exception);
        }
    }
}
