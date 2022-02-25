package uk.gov.dwp.jsa.notification.service.services;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.ValidationServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.dto.claim.ClaimStatistics;
import uk.gov.dwp.jsa.notification.service.exceptions.DailyClaimStatsNotFoundException;
import uk.gov.dwp.jsa.notification.service.model.DailyClaimStatsSummary;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Mock testing for {@link DailyClaimStatsReportService}
 */
@RunWith(MockitoJUnitRunner.class)
public class DailyClaimStatsReportServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2021-12-01T14:00:00Z"), ZoneId.systemDefault());
    private final LocalDate DAY_ONE = LocalDate.now(CLOCK).minusDays(4L);
    private final LocalDate DAY_TWO = LocalDate.now(CLOCK).minusDays(3L);
    private final LocalDate DAY_THREE = LocalDate.now(CLOCK).minusDays(2L);
    private final LocalDate DAY_FOUR = LocalDate.now(CLOCK).minusDays(1L);
    private ValidationServiceAdaptor mockValidationServiceAdaptor;
    private DailyClaimStatsReportService testSubject;
    private DailyClaimStatsSummary dayOneSummary;
    private DailyClaimStatsSummary dayTwoSummary;
    private DailyClaimStatsSummary dayThreeSummary;
    private DailyClaimStatsSummary dayFourSummary;

    @Before
    public void beforeEachTest() {
        mockValidationServiceAdaptor = mock(ValidationServiceAdaptor.class);
        testSubject = new DailyClaimStatsReportService(CLOCK, mockValidationServiceAdaptor);
    }

    @Test(expected = DailyClaimStatsNotFoundException.class)
    public void testGetClaimStatsThrowsDailyStatsNotFoundException() {
        //Arrange
        when(mockValidationServiceAdaptor.getClaimStatistics(any())).thenReturn(Optional.empty());

        //Act and Assert
        testSubject.getPreviousDailyClaimStats(8);
    }

    @Test
    public void testGetClaimStatsCalculatesCorrectWeeklyValues() {
        //Arrange
        final int expectedPrevDayCount = 4;
        setupValidStatisticsData();

        //Act
        final List<DailyClaimStatsSummary> actual = testSubject.getPreviousDailyClaimStats(expectedPrevDayCount);

        //Assert
        assertThat(actual).hasSize(expectedPrevDayCount);
        assertThat(actual).isEqualTo(Arrays.asList(dayOneSummary, dayTwoSummary, dayThreeSummary, dayFourSummary));
    }

    private void setupValidStatisticsData() {
        final ClaimStatistics claimStatsDayOne = new ClaimStatistics();
        claimStatsDayOne.setCasesReceivedInDay(5);
        claimStatsDayOne.setHeadOfWork(10);
        claimStatsDayOne.setCasesClearedInDay(15);
        claimStatsDayOne.setAssistedDigitalClaimCount(20);

        final ClaimStatistics claimStatsDayTwo = new ClaimStatistics();
        claimStatsDayTwo.setCasesReceivedInDay(25);
        claimStatsDayTwo.setHeadOfWork(30);
        claimStatsDayTwo.setCasesClearedInDay(35);
        claimStatsDayTwo.setAssistedDigitalClaimCount(40);

        final ClaimStatistics claimStatsDayThree = new ClaimStatistics();
        claimStatsDayThree.setCasesReceivedInDay(45);
        claimStatsDayThree.setHeadOfWork(50);
        claimStatsDayThree.setCasesClearedInDay(55);
        claimStatsDayThree.setAssistedDigitalClaimCount(60);

        final ClaimStatistics claimStatsDayFour = new ClaimStatistics();
        claimStatsDayFour.setCasesReceivedInDay(65);
        claimStatsDayFour.setHeadOfWork(70);
        claimStatsDayFour.setCasesClearedInDay(75);
        claimStatsDayFour.setAssistedDigitalClaimCount(80);

        dayOneSummary = new DailyClaimStatsSummary(DAY_ONE,
                claimStatsDayOne.getCasesReceivedInDay(), claimStatsDayOne.getHeadOfWork(),
                claimStatsDayOne.getCasesClearedInDay(), claimStatsDayOne.getAssistedDigitalClaimCount(),
                claimStatsDayOne.getCasesReceivedInDay() + claimStatsDayOne.getAssistedDigitalClaimCount(),
                claimStatsDayOne.getCasesReceivedInDay() + claimStatsDayOne.getAssistedDigitalClaimCount());

        dayTwoSummary = new DailyClaimStatsSummary(DAY_TWO,
                claimStatsDayTwo.getCasesReceivedInDay(), claimStatsDayTwo.getHeadOfWork(),
                claimStatsDayTwo.getCasesClearedInDay(), claimStatsDayTwo.getAssistedDigitalClaimCount(),
                claimStatsDayTwo.getCasesReceivedInDay() + claimStatsDayTwo.getAssistedDigitalClaimCount(),
                dayOneSummary.getWeeklyTotal() + claimStatsDayTwo.getCasesReceivedInDay() + claimStatsDayTwo.getAssistedDigitalClaimCount());

        dayThreeSummary = new DailyClaimStatsSummary(DAY_THREE,
                claimStatsDayThree.getCasesReceivedInDay(), claimStatsDayThree.getHeadOfWork(),
                claimStatsDayThree.getCasesClearedInDay(), claimStatsDayThree.getAssistedDigitalClaimCount(),
                claimStatsDayThree.getCasesReceivedInDay() + claimStatsDayThree.getAssistedDigitalClaimCount(),
                claimStatsDayThree.getCasesReceivedInDay() + claimStatsDayThree.getAssistedDigitalClaimCount());

        dayFourSummary = new DailyClaimStatsSummary(DAY_FOUR,
                claimStatsDayFour.getCasesReceivedInDay(), claimStatsDayFour.getHeadOfWork(),
                claimStatsDayFour.getCasesClearedInDay(), claimStatsDayFour.getAssistedDigitalClaimCount(),
                claimStatsDayFour.getCasesReceivedInDay() + claimStatsDayFour.getAssistedDigitalClaimCount(),
                dayThreeSummary.getWeeklyTotal() + claimStatsDayFour.getCasesReceivedInDay() + claimStatsDayFour.getAssistedDigitalClaimCount());

        when(mockValidationServiceAdaptor.getClaimStatistics(DAY_FOUR)).thenReturn(Optional.of(claimStatsDayFour));
        when(mockValidationServiceAdaptor.getClaimStatistics(DAY_THREE)).thenReturn(Optional.of(claimStatsDayThree));
        when(mockValidationServiceAdaptor.getClaimStatistics(DAY_TWO)).thenReturn(Optional.of(claimStatsDayTwo));
        when(mockValidationServiceAdaptor.getClaimStatistics(DAY_ONE)).thenReturn(Optional.of(claimStatsDayOne));
    }
}
