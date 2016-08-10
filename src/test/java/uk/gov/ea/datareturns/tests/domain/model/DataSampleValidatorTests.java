/**
 *
 */
package uk.gov.ea.datareturns.tests.domain.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.model.ReturnsDate;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class DataSampleValidatorTests {

	@Inject
	private Validator validator;

	@Test
	public void testValidRecord() {
		final DataSample record = createValidRecord();
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
		final DataSample record = createValidRecord();
		record.setEaId(new EaId(null));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		// We'll get 2 violations back - one for the field being blank and one for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testPermitNumberEmpty() {
		final DataSample record = createValidRecord();
		record.setEaId(new EaId(""));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		// We'll get 3 violations back - one for the field being blank, one for the pattern check and one for the controlled list value check
		Assert.assertEquals(3, violations.size());
	}

	/*=================================================================================================================
	 *
	 * RETURN TYPE TESTS
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testBlankReturnType() {
		final DataSample record = createValidRecord();
		record.setReturnType("   ");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testInvalidReturnType() {
		final DataSample record = createValidRecord();
		record.setReturnType("Invalid Return Type Value");
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
		final DataSample record = createValidRecord();
		record.setMonitoringDate(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateEmpty() {
		final DataSample record = createValidRecord();
		record.setMonitoringDate(ReturnsDate.from(""));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateInvalidFormat() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateInternationalFormat() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateInternationalFormatWithTime() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKFormat() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTime() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeSpaceSeparator() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKFormatWithSlashes() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeWithSlashes() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
		final DataSample record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateFutureDateOnly() {
		final DataSample record = createValidRecord();
		final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
		final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringDateFutureDateAndTime() {
		final DataSample record = createValidRecord();
		final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
		final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
	//
	//	@Test
	//	public void testOutDatedMonitoringDateInternationalFormatWithTime() {
	//		DataSample record = createValidRecord();
	//		LocalDateTime fiveYearsAgo = LocalDateTime.now(ZoneOffset.UTC).minusYears(5);
	//		String testDate = fiveYearsAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
	//		record.setMonitoringDate(ReturnsDate.from(testDate));
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
		final DataSample record = createValidRecord();
		record.setReturnPeriod(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testReturnPeriodLength() {
		final DataSample record = createValidRecord();
		record.setReturnPeriod(RandomStringUtils.random(20));
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
		final DataSample record = createValidRecord();
		record.setMonitoringPoint(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointEmpty() {
		final DataSample record = createValidRecord();
		record.setMonitoringPoint("");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointLength() {
		final DataSample record = createValidRecord();
		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(30));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(31));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointSpecialCharacters() {
		final DataSample record = createValidRecord();
		final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (final char c : invalidCharacters.toCharArray()) {
			record.setMonitoringPoint(String.valueOf(c));
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
	@Test
	public void testSampleReferenceLength() {
		final DataSample record = createValidRecord();
		record.setSampleReference(RandomStringUtils.randomAlphanumeric(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setSampleReference(RandomStringUtils.randomAlphanumeric(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testSampleReferenceSpecialCharacters() {
		final DataSample record = createValidRecord();
		final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (final char c : invalidCharacters.toCharArray()) {
			record.setSampleReference(String.valueOf(c));
			final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
			Assert.assertEquals(1, violations.size());
		}
	}

	/*=================================================================================================================
	 *
	 * SAMPLE BY
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testSampleByLength() {
		final DataSample record = createValidRecord();
		record.setSampleBy(RandomStringUtils.random(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setSampleBy(RandomStringUtils.random(256));
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
		final DataSample record = createValidRecord();
		record.setParameter(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testParameterEmpty() {
		final DataSample record = createValidRecord();
		record.setParameter("");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testParameterInvalid() {
		final DataSample record = createValidRecord();
		record.setParameter("An invalid parameter value");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testParameterValid() {
		final DataSample record = createValidRecord();
		record.setParameter("1,2,3,4-Tetrachlorobenzene");
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
		final DataSample record = createValidRecord();
		record.setValue(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueEmpty() {
		final DataSample record = createValidRecord();
		record.setValue("  ");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testValueInvalid() {
		final DataSample record = createValidRecord();
		record.setValue("<>232323");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueValidLessThanInteger() {
		final DataSample record = createValidRecord();
		record.setValue("<1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidGreaterThanInteger() {
		final DataSample record = createValidRecord();
		record.setValue(">1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidLessThanDecimal() {
		final DataSample record = createValidRecord();
		record.setValue("<0.1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidGreaterThanDecimal() {
		final DataSample record = createValidRecord();
		record.setValue(">0.1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueInvalidLessThanDecimalNoLeadingZero() {
		final DataSample record = createValidRecord();
		record.setValue("<.1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
		final DataSample record = createValidRecord();
		record.setValue(">.1");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidLessThanSignOnly() {
		final DataSample record = createValidRecord();
		record.setValue("<");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidGreaterThanSignOnly() {
		final DataSample record = createValidRecord();
		record.setValue(">");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidMinusSignOnly() {
		final DataSample record = createValidRecord();
		record.setValue("-");
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
		final DataSample record = createValidRecord();
		record.setTextValue(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testTextValueInvalid() {
		final DataSample record = createValidRecord();
		record.setTextValue(RandomStringUtils.random(30));
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testTextValueBooleans() {
		final DataSample record = createValidRecord();
		final String[] allowedBooleans = { "true", "false", "yes", "no", "1", "0", "True", "False", "tRuE", "yEs", "fAlSe" };

		for (final String bool : allowedBooleans) {
			record.setTextValue(bool);
			final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
			Assert.assertTrue("Boolean value " + bool + " failed validation ", violations.isEmpty());
		}
	}

	/*=================================================================================================================
	 *
	 * UNIT
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testUnitNull() {
		final DataSample record = createValidRecord();
		record.setUnit(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testUnitEmpty() {
		final DataSample record = createValidRecord();
		record.setUnit(" ");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testUnitValid() {
		final DataSample record = createValidRecord();
		record.setUnit("Hazen");
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	/*=================================================================================================================
	 *
	 * REFERENCE PERIOD
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testReferencePeriodNull() {
		final DataSample record = createValidRecord();
		record.setReferencePeriod(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testReferencePeriodInvalid() {
		final DataSample record = createValidRecord();
		record.setReferencePeriod(RandomStringUtils.random(30));
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
		final DataSample record = createValidRecord();
		record.setMethStand(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMethStandInvalid() {
		final DataSample record = createValidRecord();
		record.setMethStand(RandomStringUtils.random(31));
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
		final DataSample record = createValidRecord();
		record.setComments(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCommentsLength() {
		final DataSample record = createValidRecord();
		record.setComments(RandomStringUtils.random(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setComments(RandomStringUtils.random(256));
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
		final DataSample record = createValidRecord();
		record.setCic(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCicLength() {
		final DataSample record = createValidRecord();
		record.setCic(RandomStringUtils.random(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setCic(RandomStringUtils.random(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	/*=================================================================================================================
	 *
	 * CHEMICAL ABSTRACTS SERVICE
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testCasNull() {
		final DataSample record = createValidRecord();
		record.setCas(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCasLength() {
		final DataSample record = createValidRecord();
		record.setCas(RandomStringUtils.random(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setCas(RandomStringUtils.random(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	/*=================================================================================================================
	 *
	 * RECOVERY/DISPOSAL CODE
	 *
	 *=================================================================================================================
	 */
	@Test
	public void testRdCodeNull() {
		final DataSample record = createValidRecord();
		record.setRdCode(null);
		final Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testRdCodeLength() {
		final DataSample record = createValidRecord();
		record.setRdCode(RandomStringUtils.random(255));
		Set<ConstraintViolation<DataSample>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setRdCode(RandomStringUtils.random(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	/**
	 * Creates a {@link DataSample} instance with all values setup
	 * with a valid entry.
	 *
	 * @return a new {@link DataSample} which should pass validation
	 */
	private static DataSample createValidRecord() {
		final DataSample record = new DataSample();
		record.setEaId(new EaId("DP3431PC"));
		record.setSiteName("Site Name");
		record.setReturnType("Landfill Annual monitoring report");
		record.setMonitoringDate(ReturnsDate.from("2016-03-09T11:18:59"));
		record.setReturnPeriod("Aug 2016");
		record.setMonitoringPoint("Borehole 1");
		record.setSampleReference("Sample Reference");
		record.setSampleBy("Sam Gardner-Dell");
		record.setParameter("1,1,1,2-Tetrachloroethane");
		record.setValue("<0.0006");
		record.setTextValue("true");
		record.setUnit("m3/s");
		record.setReferencePeriod("Biannual periodic measurement average value over sample period of between 6 and 8 hours");
		record.setMethStand("BS ISO 15713");
		record.setComments("Free text comments entered in this field.");
		record.setCic("True");
		record.setCas("100-74-3");
		record.setRdCode("D13");
		return record;
	}
}