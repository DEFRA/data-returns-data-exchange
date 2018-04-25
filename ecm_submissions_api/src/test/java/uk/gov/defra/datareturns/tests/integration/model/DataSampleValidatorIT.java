package uk.gov.defra.datareturns.tests.integration.model;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.testcommons.framework.DataIntegrationTest;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;

/**
 * Tests the validation entity fields the DataSample class
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@DataIntegrationTest
public class DataSampleValidatorIT {

    private static final RandomStringGenerator GENERATOR = new RandomStringGenerator.Builder()
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.LETTERS)
            .withinRange('0', 'z')
            .build();
    @Inject
    private Validator validator;

    @Test
    public void testValidRecord() {
        final Record record = createValidNumericRecord();
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
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
        final Dataset ds = new Dataset();
        ds.setEaId(null);
        ds.addRecord(createValidNumericRecord());
        final Set<ConstraintViolation<Dataset>> violations = this.validator.validate(ds);
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
        final Record record = createValidNumericRecord();
        record.setReturnType(null);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidReturnType() {
        final Record record = createValidNumericRecord();
        record.setReturnType(Long.MIN_VALUE);
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

    @Test
    public void testMonitoringDateFutureDate() {
        final Record record = createValidNumericRecord();
        final Date date = Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
        record.setMonitoringDate(date);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

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
        record.setReturnPeriod(GENERATOR.generate(20));
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
        record.setMonitoringPoint(GENERATOR.generate(50));
        Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setMonitoringPoint(GENERATOR.generate(51));
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
    public void testParameterInvalid() {
        final Record record = createValidNumericRecord();
        record.setParameter(Long.MIN_VALUE);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterValid() {
        final Record record = createValidNumericRecord();
        record.setParameter(1L);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }
//
//    @Test
//    public void testParameterEmpty() {
//        final Record record = createValidNumericRecord();
//        record.setParameter("");
//        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
//        Assert.assertEquals(1, violations.size());
//    }

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

    @Test
    public void testValueProhibitedWithTextValue() {
        final Record record = createValidNumericRecord();
        record.setTextValue(2L);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        // Two violations, first for text value used with value, second for unit used with text value
        Assert.assertEquals(2, violations.size());
    }

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
    public void testUnitValid() {
        final Record record = createValidNumericRecord();
        record.setUnit(1L);
        final Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitProhibitedWithTextValue() {
        final Record record = createValidTextRecord();
        record.setUnit(1L);
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
        record.setReferencePeriod(Long.MIN_VALUE);
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
        record.setMethodOrStandard(Long.MIN_VALUE);
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
        record.setComments(GENERATOR.generate(255));
        Set<ConstraintViolation<Record>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setComments(GENERATOR.generate(256));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    /**
     * Creates a {@link Record} instance with all values setup
     * with a valid entry.
     *
     * @return a new {@link Record} which should pass validation
     * Record
     */
    private static Record createValidNumericRecord() {
        final Record record = new Record();
        record.setReturnType(10L);
        record.setMonitoringDate(Date.from(Instant.now().minusSeconds(1)));
        record.setReturnPeriod("Aug 2016");
        record.setMonitoringPoint("Borehole 1");
        record.setParameter(1503L);
        record.setNumericValue(BigDecimal.valueOf(0.2323));
        record.setNumericEquality(Record.Equality.LESS_THAN);
        record.setUnit(115L);
        record.setReferencePeriod(10L);
        record.setMethodOrStandard(4L);
        record.setComments("Free text comments entered in this field.");
        return record;
    }

    /**
     * Creates a {@link Record} instance using TXT_VALUE with all values setup with a valid data
     *
     * @return a new {@link Record} which should pass validation
     */
    private static Record createValidTextRecord() {
        final Record record = new Record();
        record.setReturnType(10L);
        record.setMonitoringDate(Date.from(Instant.now().minusSeconds(1)));
        record.setReturnPeriod("Aug 2016");
        record.setMonitoringPoint("Borehole 1");
        record.setParameter(798L);
        record.setTextValue(2L);
        record.setReferencePeriod(10L);
        record.setMethodOrStandard(4L);
        record.setComments("Free text comments entered in this field.");
        return record;
    }
}
