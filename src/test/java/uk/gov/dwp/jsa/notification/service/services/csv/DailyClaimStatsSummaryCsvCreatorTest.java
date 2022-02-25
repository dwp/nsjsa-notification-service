package uk.gov.dwp.jsa.notification.service.services.csv;


import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Testing for {@link DailyClaimStatsSummaryCsvCreator}
 */
public class DailyClaimStatsSummaryCsvCreatorTest {
    private final Resource expectedCsv = new ClassPathResource("valid-daily-claim-stats-summary.csv");

    private final DailyClaimStatsSummaryCsvCreator testSubject = new DailyClaimStatsSummaryCsvCreator();

    @Test
    public void testCreateCsvCreatesCorrectCsv() throws IOException {
        //Arrange
        final List<DailyClaimStatsSummary> summaries = Arrays.asList(
                new DailyClaimStatsSummary(LocalDate.of(2021, 12, 2), 5, 10, 15, 20, 25, 30),
                new DailyClaimStatsSummary(LocalDate.of(2021, 12, 1), 35, 40, 45, 50, 55, 60)
        );

        //Act
        final byte[] actual = testSubject.createCsv(summaries);

        //Assert
        assertThat(actual).isEqualTo(IOUtils.toByteArray(expectedCsv.getInputStream()));
    }
}
