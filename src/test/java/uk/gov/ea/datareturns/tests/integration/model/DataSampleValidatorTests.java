/**
 *
 */
package uk.gov.ea.datareturns.tests.integration.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.*;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Tests the validation entityfields the DataSample class
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class DataSampleValidatorTests {
    @Inject
    private Validator validator;

    @Test
    public void testValidRecord() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setEaId(new EaId(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        // one violation for the field being blank
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testPermitNumberEmpty() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setEaId(new EaId(""));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        // one violation for the field being blank
        Assert.assertEquals(1, violations.size());
    }


    @Test
    public void testPermitNumberIncorrectForSite() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setEaId(new EaId("70057"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReturnType(new ReturnType("   "));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        // We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testInvalidReturnType() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReturnType(new ReturnType("Invalid Return Type Value"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMonitoringDate(new MonitoringDate(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateEmpty() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMonitoringDate(new MonitoringDate(""));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateInvalidFormat() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void testMonitoringDateInternationalFormat() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateInternationalFormatWithTime() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKFormat() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTime() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeSpaceSeparator() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKFormatWithSlashes() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeWithSlashes() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMonitoringDateFutureDateOnly() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringDateFutureDateAndTime() {
        final DataSampleValidationObject record = createValidNumericRecord();
        final LocalDateTime anHourFromNow = LocalDateTime.now().plusHours(1);
        final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        record.setMonitoringDate(new MonitoringDate(testDate));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReturnPeriod(null);
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReturnPeriodLength() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReturnPeriod(new ReturnPeriod(RandomStringUtils.random(20)));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointEmpty() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(""));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testMonitoringPointLength() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMonitoringPoint(new MonitoringPoint(RandomStringUtils.randomAlphanumeric(50)));
        Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setMonitoringPoint(new MonitoringPoint(RandomStringUtils.randomAlphanumeric(51)));
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setParameter(new Parameter(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterEmpty() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setParameter(new Parameter(""));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterInvalid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setParameter(new Parameter("An invalid parameter value"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testParameterValid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setParameter(new Parameter("1,2,3,4-Tetrachlorobenzene"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueEmpty() {

        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("  "));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testValueInvalid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("<>232323"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueProhibitedWithTextValue() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setTextValue(new TxtValue("true"));
        record.setUnit(new Unit("m/s"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        // Two violations, first for text value used with value, second for unit used with text value
        Assert.assertEquals(2, violations.size());
    }

    @Test
    public void testValueValidLessThanInteger() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("<1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidGreaterThanInteger() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(">1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidLessThanDecimal() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("<0.1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueValidGreaterThanDecimal() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(">0.1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }
    @Test
    public void testValueValidWithSpaces() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(">   -0.1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testValueInvalidLessThanDecimalNoLeadingZero() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("<.1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(">.1"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidLessThanSignOnly() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("<"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidGreaterThanSignOnly() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value(">"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testValueInvalidMinusSignOnly() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setValue(new Value("-"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setTextValue(new TxtValue(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setUnit(new Unit(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitNullForTextValue() {
        final DataSampleValidationObject record = createValidTextRecord();
        record.setUnit(new Unit(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitEmpty() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setUnit(new Unit(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void testUnitValid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setUnit(new Unit("HU"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testUnitProhibitedWithTextValue() {
        final DataSampleValidationObject record = createValidTextRecord();
        record.setUnit(new Unit("HU"));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReferencePeriod(new ReferencePeriod(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testReferencePeriodInvalid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setReferencePeriod(new ReferencePeriod(RandomStringUtils.random(30)));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMethStand(new MethodOrStandard(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testMethStandInvalid() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setMethStand(new MethodOrStandard(RandomStringUtils.random(31)));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
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
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setComments(new Comments(null));
        final Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void testCommentsLength() {
        final DataSampleValidationObject record = createValidNumericRecord();
        record.setComments(new Comments(RandomStringUtils.random(255)));
        Set<ConstraintViolation<DataSampleValidationObject>> violations = this.validator.validate(record);
        Assert.assertEquals(0, violations.size());

        record.setComments(new Comments(RandomStringUtils.random(256)));
        violations = this.validator.validate(record);
        Assert.assertEquals(1, violations.size());
    }


    /**
     * Creates a {@link DataSampleValidationObject} instance with all values setup
     * with a valid entry.
     *
     * @return a new {@link DataSampleValidationObject} which should pass validation
     * DataSampleValidationObject
     */
    private static DataSampleValidationObject createValidNumericRecord() {
        final DataSamplePayload dataSamplePayload = new DataSamplePayload();

        dataSamplePayload.setEaId("42355");
        dataSamplePayload.setSiteName("Biffa - Marchington Landfill Site");
        dataSamplePayload.setReturnType("Landfill leachate monitoring");
        dataSamplePayload.setMonitoringDate("2016-03-09T11:18:59");
        dataSamplePayload.setReturnPeriod("Aug 2016");
        dataSamplePayload.setMonitoringPoint("Borehole 1");
        dataSamplePayload.setParameter("1,1,1,2-Tetrachloroethane");
        dataSamplePayload.setValue("<0.0006");
        dataSamplePayload.setUnit("m3/s");
        dataSamplePayload.setReferencePeriod("95% of all 10-minute averages in any 24 hour period");
        dataSamplePayload.setMethStand("BS EN 12260");
        dataSamplePayload.setComments("Free text comments entered in this field.");

        final DataSampleValidationObject record = new DataSampleValidationObject(dataSamplePayload);

        return record;
    }

    /**
     * Creates a {@link DataSampleValidationObject} instance using Txt_Value with all values setup with a valid data
     *
     * @return a new {@link DataSampleValidationObject} which should pass validation
     */
    private static DataSampleValidationObject createValidTextRecord() {
        final DataSamplePayload dataSamplePayload = new DataSamplePayload();

        dataSamplePayload.setEaId("42355");
        dataSamplePayload.setSiteName("Biffa - Marchington Landfill Site");
        dataSamplePayload.setReturnType("Landfill leachate monitoring");
        dataSamplePayload.setMonitoringDate("2016-03-09T11:18:59");
        dataSamplePayload.setReturnPeriod("Aug 2016");
        dataSamplePayload.setMonitoringPoint("Borehole 1");
        dataSamplePayload.setParameter("1,1,1,2-Tetrachloroethane");
        dataSamplePayload.setTextValue("true");
        dataSamplePayload.setReferencePeriod("95% of all 10-minute averages in any 24 hour period");
        dataSamplePayload.setMethStand("BS EN 12260");
        dataSamplePayload.setComments("Free text comments entered in this field.");

        final DataSampleValidationObject record = new DataSampleValidationObject(dataSamplePayload);

        return record;
    }
}