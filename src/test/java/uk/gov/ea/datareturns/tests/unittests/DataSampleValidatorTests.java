/**
 *
 */
package uk.gov.ea.datareturns.tests.unittests;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.impl.*;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Tests the validation constraints the DataSample class
 *
 * @author Sam Gardner-Dell
 */
@SpringBootTest(classes = App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class DataSampleValidatorTests {
    @Inject
    private Validator validator;

    @Test
    public void testValidRecord() {
        final DataSample record = createValidNumericRecord();
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

	/*=================================================================================================================
     *
	 * PERMIT NUMBER TESTS
	 *
	 *=================================================================================================================
	 */

    @Test
    public void testPermitNumberNull() {
        final DataSample record = createValidNumericRecord();
        record.setEaId(new EaId(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        // one violation for the field being blank
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testPermitNumberEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setEaId(new EaId(""));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        // one violation for the field being blank
        Assert.assertEquals(1, violations.size());
    }


    @Test
    public void testPermitNumberIncorrectForSite() {
        final DataSample record = createValidNumericRecord();
        record.setEaId(new EaId("70057"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        // one violation for the field being blank
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * RETURN TYPE TESTS
     *
     *=================================================================================================================
     */
    @Test
    public void testBlankReturnType() {
        final DataSample record = createValidNumericRecord();
        record.setReturnType(new ReturnType("   "));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        // We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testInvalidReturnType() {
        final DataSample record = createValidNumericRecord();
        record.setReturnType(new ReturnType("Invalid Return Type Value"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * MONITORING DATE TESTS
     *
     *=================================================================================================================
     */
    @Test
    public void testMonitoringDateNull() {
        final DataSample record = createValidNumericRecord();
        record.setMonitoringDate(new MonitoringDate(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setMonitoringDate(new MonitoringDate(""));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateInvalidFormat() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateInternationalFormat() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateInternationalFormatWithTime() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKFormat() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTime() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeSpaceSeparator() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKFormatWithSlashes() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeWithSlashes() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
        final DataSample record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateFutureDateOnly() {
        final DataSample record = createValidNumericRecord();
        final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringDateFutureDateAndTime() {
        final DataSample record = createValidNumericRecord();
        final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    // TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
    //
    //	@Test
    //	public void testOutDatedMonitoringDateInternationalFormatWithTime() {
    //		DataSample record = createValidNumericRecord();
    //		LocalDateTime fiveYearsAgo = LocalDateTime.now(ZoneOffset.UTC).minusYears(5);
    //		String testDate = fiveYearsAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    //		record.setMonitoringDate(new MonitoringDate(testDate));
    //		Set<ConstraintViolation<DataSample>> violations = validator.validate(record);
    //		Assert.assertEquals(1, violations.size());
    //	}

	/*=================================================================================================================
     *
	 * RETURN PERIOD
	 *
	 *=================================================================================================================
	 */

    @Test
    public void testReturnPeriodNull() {
        final DataSample record = createValidNumericRecord();
        record.setReturnPeriod(null);
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReturnPeriodLength() {
        final DataSample record = createValidNumericRecord();
        record.setReturnPeriod(new ReturnPeriod(RandomStringUtils.random(20)));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * MONITORING POINT
     *
     *=================================================================================================================
     */
    @Test
    public void testMonitoringPointNull() {
        final DataSample record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(""));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointLength() {
        final DataSample record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(RandomStringUtils.randomAlphanumeric(30)));
        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setMonitoringPoint(new MonitoringPoint(RandomStringUtils.randomAlphanumeric(31)));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointSpecialCharacters() {
        final DataSample record = createValidNumericRecord();
        final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
        for (final char c : invalidCharacters.toCharArray()) {
            record.setMonitoringPoint(new MonitoringPoint(String.valueOf(c)));
            final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
            Assert.assertEquals(1, violations.size());
        }
    }

    /*=================================================================================================================
     *
     * SAMPLE REFERENCE
     *
     *=================================================================================================================
     */
    //    @Test
    //    public void testSampleReferenceLength() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setSampleReference(new RandomStringUtils.randomAlphanumeric(255));
    //        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //
    //        record.setSampleReference(RandomStringUtils.randomAlphanumeric(256));
    //        violations = this.validator.validate(record);
    //        Assert.assertEquals(1, violations.size());
    //    }
    //
    //    @Test
    //    public void testSampleReferenceSpecialCharacters() {
    //        final DataSample record = createValidNumericRecord();
    //        final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
    //        for (final char c : invalidCharacters.toCharArray()) {
    //            record.setSampleReference(String.valueOf(c));
    //            final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //            Assert.assertEquals(1, violations.size());
    //        }
    //    }

    /*=================================================================================================================
     *
     * SAMPLE BY
     *
     *=================================================================================================================
     */
    //    @Test
    //    public void testSampleByLength() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setSampleBy(RandomStringUtils.random(255));
    //        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //
    //        record.setSampleBy(RandomStringUtils.random(256));
    //        violations = this.validator.validate(record);
    //        Assert.assertEquals(1, violations.size());
    //    }

    /*=================================================================================================================
     *
     * PARAMETER
     *
     *=================================================================================================================
     */
    @Test
    public void testParameterNull() {
        final DataSample record = createValidNumericRecord();
        record.setParameter(new Parameter(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setParameter(new Parameter(""));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterInvalid() {
        final DataSample record = createValidNumericRecord();
        record.setParameter(new Parameter("An invalid parameter value"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterValid() {
        final DataSample record = createValidNumericRecord();
        record.setParameter(new Parameter("1,2,3,4-Tetrachlorobenzene"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    /*=================================================================================================================
     *
     * VALUE
     *
     *=================================================================================================================
     */
    @Test
    public void testValueNull() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("  "));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalid() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("<>232323"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueProhibitedWithTextValue() {
        final DataSample record = createValidNumericRecord();
        record.setTextValue(new TxtValue("true"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        // Two violations, first for text value used with value, second for unit used with text value
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testValueValidLessThanInteger() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("<1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidGreaterThanInteger() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(">1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidLessThanDecimal() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("<0.1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidGreaterThanDecimal() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(">0.1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueInvalidLessThanDecimalNoLeadingZero() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("<.1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(">.1"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidLessThanSignOnly() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("<"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidGreaterThanSignOnly() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(">"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidMinusSignOnly() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value("-"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * TEXT VALUE
     *
     *=================================================================================================================
     */
    @Test
    public void testTextValueNull() {
        final DataSample record = createValidNumericRecord();
        record.setTextValue(new TxtValue(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    /*=================================================================================================================
     *
     * UNIT
     *
     *=================================================================================================================
     */
    @Test
    public void testUnitNullForNumericValue() {
        final DataSample record = createValidNumericRecord();
        record.setUnit(new Unit(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitNullForTextValue() {
        final DataSample record = createValidTextRecord();
        record.setUnit(new Unit(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitEmpty() {
        final DataSample record = createValidNumericRecord();
        record.setUnit(new Unit(" "));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitValid() {
        final DataSample record = createValidNumericRecord();
        record.setUnit(new Unit("HU"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitProhibitedWithTextValue() {
        final DataSample record = createValidTextRecord();
        record.setUnit(new Unit("HU"));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * REFERENCE PERIOD
     *
     *=================================================================================================================
     */
    @Test
    public void testReferencePeriodNull() {
        final DataSample record = createValidNumericRecord();
        record.setReferencePeriod(new ReferencePeriod(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReferencePeriodInvalid() {
        final DataSample record = createValidNumericRecord();
        record.setReferencePeriod(new ReferencePeriod(RandomStringUtils.random(30)));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * METHOD OR STANDARD
     *
     *=================================================================================================================
     */
    @Test
    public void testMethStandNull() {
        final DataSample record = createValidNumericRecord();
        record.setMethStand(new MethodOrStandard(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMethStandInvalid() {
        final DataSample record = createValidNumericRecord();
        record.setMethStand(new MethodOrStandard(RandomStringUtils.random(31)));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * COMMENT
     *
     *=================================================================================================================
     */
    @Test
    public void testCommentsNull() {
        final DataSample record = createValidNumericRecord();
        record.setComments(new Comments(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testCommentsLength() {
        final DataSample record = createValidNumericRecord();
        record.setComments(new Comments(RandomStringUtils.random(255)));
        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setComments(new Comments(RandomStringUtils.random(256)));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * COMMERCIAL IN CONFIDENCE
     *
     *=================================================================================================================
     */
    @Test
    public void testCicNull() {
        final DataSample record = createValidNumericRecord();
        record.setCic(new Cic(null));
        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testCicLength() {
        final DataSample record = createValidNumericRecord();
        record.setCic(new Cic(RandomStringUtils.random(255)));
        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setCic(new Cic(RandomStringUtils.random(256)));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * CHEMICAL ABSTRACTS SERVICE
     *
     *=================================================================================================================
     */
    //    @Test
    //    public void testCasNull() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setCas(null);
    //        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //    }
    //
    //    @Test
    //    public void testCasLength() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setCas(RandomStringUtils.random(255));
    //        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //
    //        record.setCas(RandomStringUtils.random(256));
    //        violations = this.validator.validate(record);
    //        Assert.assertEquals(1, violations.size());
    //    }

    /*=================================================================================================================
     *
     * RECOVERY/DISPOSAL CODE
     *
     *=================================================================================================================
     */
    //    @Test
    //    public void testRdCodeNull() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setRdCode(null);
    //        final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //    }
    //
    //    @Test
    //    public void testRdCodeLength() {
    //        final DataSample record = createValidNumericRecord();
    //        record.setRdCode(RandomStringUtils.random(255));
    //        Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
    //        Assert.assertEquals(0, violations.size());
    //
    //        record.setRdCode(RandomStringUtils.random(256));
    //        violations = this.validator.validate(record);
    //        Assert.assertEquals(1, violations.size());
    //    }

    /**
     * Creates a {@link DataSample} instance with all values setup
     * with a valid entry.
     *
     * @return a new {@link DataSample} which should pass validation
     */
    private static DataSample createValidNumericRecord() {
        final DataSample record = new DataSample();
        record.setEaId(new EaId("EP3136GK"));
        record.setSiteName(new SiteName("Rainham Landfill"));
        record.setReturnType(new ReturnType("Landfill leachate monitoring"));
        record.setMonitoringDate(new MonitoringDate("2016-03-09T11:18:59"));
        record.setReturnPeriod(new ReturnPeriod("Aug 2016"));
        record.setMonitoringPoint(new MonitoringPoint("Borehole 1"));
        //        record.setSampleReference("Sample Reference");
        //        record.setSampleBy("Sam Gardner-Dell");
        record.setParameter(new Parameter("1,1,1,2-Tetrachloroethane"));
        record.setValue(new Value("<0.0006"));
        record.setUnit(new Unit("m3/s"));
        record.setReferencePeriod(new ReferencePeriod("95% of all 10-minute averages in any 24-hour period"));
        record.setMethStand(new MethodOrStandard("BS EN 12260"));
        record.setComments(new Comments("Free text comments entered in this field."));
        record.setCic(new Cic("True"));
        //        record.setCas("100-74-3");
        //        record.setRdCode("D13");
        return record;
    }

    /**
     * Creates a {@link DataSample} instance using Txt_Value with all values setup with a valid data
     *
     * @return a new {@link DataSample} which should pass validation
     */
    private static DataSample createValidTextRecord() {
        final DataSample record = createValidNumericRecord();
        record.setValue(new Value(null));
        record.setTextValue(new TxtValue("true"));
        record.setUnit(new Unit(null));
        return record;
    }
}