/**
 *
 */
package uk.gov.defra.datareturns.tests.integration.model;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.EcmApi;
import uk.gov.defra.datareturns.data.model.record.Record;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Tests the validation entity fields the DataSample class
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EcmApi.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DataSampleValidatorTests {

    private static final RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.LETTERS)
            .withinRange('0', 'z')
            .build();
    @Inject
    private Validator validator;

    /**
     * Creates a {@link Record} instance with all values setup
     * with a valid entry.
     *
     * @return a new {@link Record} which should pass validation
     * Record
     */
    private static Record createValidNumericRecord() {
        final Record record = new Record();
        record.setReturnType("Landfill leachate monitoring");
        record.setMonitoringDate(new Date());
        record.setReturnPeriod("Aug 2016");
        record.setMonitoringPoint("Borehole 1");
        record.setParameter("1,1,1,2-Tetrachloroethane");
        record.setNumericValue(BigDecimal.valueOf(0.2323));
        record.setNumericEquality(Record.Equality.LESS_THAN);
        record.setUnit("m3/s");
        record.setReferencePeriod("95% of all 10-minute averages in any 24 hour period");
        record.setMethodOrStandard("BS EN 12260");
        record.setComments("Free text comments entered in this field.");
        return record;
    }

	/*=================================================================================================================
     *
	 * PERMIT NUMBER TESTS
	 *
	 *=================================================================================================================
	 */

    /**
     * Creates a {@link Record} instance using Txt_Value with all values setup with a valid data
     *
     * @return a new {@link Record} which should pass validation
     */
    private static Record createValidTextRecord() {
        final Record record = new Record();

        record.setReturnType("Landfill leachate monitoring");
        record.setMonitoringDate(new Date());
        record.setReturnPeriod("Aug 2016");
        record.setMonitoringPoint("Borehole 1");
        record.setParameter("1,1,1,2-Tetrachloroethane");
        record.setTextValue("true");
        record.setReferencePeriod("95% of all 10-minute averages in any 24 hour period");
        record.setMethodOrStandard("BS EN 12260");
        record.setComments("Free text comments entered in this field.");
        return record;
    }

    @Test
    public void testValidRecord() {
        final Record record = createValidNumericRecord();
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

//    @Test
//    public void testPermitNumberNull() {
//        final Record record = createValidNumericRecord();
//        record.setEaId(new EaId(null));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        // one violation for the field being blank
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testPermitNumberEmpty() {
//        final Record record = createValidNumericRecord();
//        record.setEaId(new EaId(""));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        // one violation for the field being blank
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testPermitNumberIncorrectForSite() {
//        final Record record = createValidNumericRecord();
//        record.setEaId(new EaId("70057"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        // one violation for the field being blank
//        Assert.assertEquals(1, violations.size());
//    }

    /*=================================================================================================================
     *
     * RETURN TYPE TESTS
     *
     *=================================================================================================================
     */
    @Test
    public void testBlankReturnType() {
        final Record record = createValidNumericRecord();
        record.setReturnType("   ");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        // We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testInvalidReturnType() {
        final Record record = createValidNumericRecord();
        record.setReturnType("Invalid Return Type Value");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setMonitoringDate(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

//    @Test
//    public void testMonitoringDateEmpty() {
//        final Record record = createValidNumericRecord();
//        record.setMonitoringDate(new MonitoringDate(""));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertFalse(violations.isEmpty());
//    }
//
//    @Test
//    public void testMonitoringDateInvalidFormat() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertFalse(violations.isEmpty());
//    }

//    @Test
//    public void testMonitoringDateInternationalFormat() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateInternationalFormatWithTime() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateUKFormat() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateUKWithTime() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }

//    @Test
//    public void testMonitoringDateUKWithTimeSpaceSeparator() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateUKFormatWithSlashes() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateUKWithTimeWithSlashes() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
//        final Record record = createValidNumericRecord();
//        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }

//    @Test
//    public void testMonitoringDateFutureDateOnly() {
//        final Record record = createValidNumericRecord();
//        final LocalDateTime anHourFromNow = LocalDateTime.now().plusDays(1);
//        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testMonitoringDateFutureDateAndTime() {
//        final Record record = createValidNumericRecord();
//        final LocalDateTime anHourFromNow = LocalDateTime.now().plusHours(1);
//        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
//        record.setMonitoringDate(new MonitoringDate(testDate));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }

	/*=================================================================================================================
     *
	 * RETURN PERIOD
	 *
	 *=================================================================================================================
	 */


    @Test
    public void testReturnPeriodNull() {
        final Record record = createValidNumericRecord();
        record.setReturnPeriod(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReturnPeriodLength() {
        final Record record = createValidNumericRecord();
        record.setReturnPeriod(generator.generate(20));
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setMonitoringPoint(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointEmpty() {
        final Record record = createValidNumericRecord();
        record.setMonitoringPoint("");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointLength() {
        final Record record = createValidNumericRecord();
        record.setMonitoringPoint(generator.generate(50));
        Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setMonitoringPoint(generator.generate(51));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /*=================================================================================================================
     *
     * PARAMETER
     *
     *=================================================================================================================
     */
    @Test
    public void testParameterNull() {
        final Record record = createValidNumericRecord();
        record.setParameter(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterEmpty() {
        final Record record = createValidNumericRecord();
        record.setParameter("");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterInvalid() {
        final Record record = createValidNumericRecord();
        record.setParameter("An invalid parameter value");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterValid() {
        final Record record = createValidNumericRecord();
        record.setParameter("1,2,3,4-Tetrachlorobenzene");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setNumericValue(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

//    @Test
//    public void testValueEmpty() {
//        final Record record = createValidNumericRecord();
//        record.setNumericValue("  ");
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }

//    @Test
//    public void testValueInvalid() {
//        final Record record = createValidNumericRecord();
//        record.setNumericValue(new Value("<>232323"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }

    @Test
    public void testValueProhibitedWithTextValue() {
        final Record record = createValidNumericRecord();
        record.setTextValue("true");
        record.setUnit("m/s");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        // Two violations, first for text value used with value, second for unit used with text value
        Assert.assertEquals(2, violations.size());
    }

//    @Test
//    public void testValueValidLessThanInteger() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value("<1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testValueValidGreaterThanInteger() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value(">1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testValueValidLessThanDecimal() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value("<0.1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testValueValidGreaterThanDecimal() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value(">0.1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }

//    @Test
//    public void testValueValidWithSpaces() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value(">   -0.1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(0, violations.size());
//    }
//
//    @Test
//    public void testValueInvalidLessThanDecimalNoLeadingZero() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value("<.1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value(">.1"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testValueInvalidLessThanSignOnly() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value("<"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testValueInvalidGreaterThanSignOnly() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value(">"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }
//
//    @Test
//    public void testValueInvalidMinusSignOnly() {
//        final Record record = createValidNumericRecord();
//        record.setValue(new Value("-"));
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }

    /*=================================================================================================================
     *
     * TEXT VALUE
     *
     *=================================================================================================================
     */
    @Test
    public void testTextValueNull() {
        final Record record = createValidNumericRecord();
        record.setTextValue(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setUnit(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitNullForTextValue() {
        final Record record = createValidTextRecord();
        record.setUnit(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitEmpty() {
        final Record record = createValidNumericRecord();
        record.setUnit("");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitValid() {
        final Record record = createValidNumericRecord();
        record.setUnit("HU");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitProhibitedWithTextValue() {
        final Record record = createValidTextRecord();
        record.setUnit("HU");
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setReferencePeriod(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReferencePeriodInvalid() {
        final Record record = createValidNumericRecord();
        record.setReferencePeriod(generator.generate(30));
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setMethodOrStandard(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMethStandInvalid() {
        final Record record = createValidNumericRecord();
        record.setMethodOrStandard(generator.generate(31));
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Record record = createValidNumericRecord();
        record.setComments(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testCommentsLength() {
        final Record record = createValidNumericRecord();
        record.setComments(generator.generate(255));
        Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setComments(generator.generate(256));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }
}