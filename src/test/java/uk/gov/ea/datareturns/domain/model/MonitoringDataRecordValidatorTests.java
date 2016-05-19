/**
 *
 */
package uk.gov.ea.datareturns.domain.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ea.datareturns.App;

/**
 * Tests the validation constraints the MonitoringDataRecord class
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class MonitoringDataRecordValidatorTests {

	@Inject
	private Validator validator;

	@Test
	public void testValidRecord() {
		final MonitoringDataRecord record = createValidRecord();
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setEaId(new EaId(null));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		// We'll get 2 violations back - one for the field being blank and one for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testPermitNumberEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setEaId(new EaId(""));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setReturnType("   ");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testInvalidReturnType() {
		final MonitoringDataRecord record = createValidRecord();
		record.setReturnType("Invalid Return Type Value");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate(ReturnsDate.from(""));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateInvalidFormat() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}

	@Test
	public void testMonitoringDateInternationalFormat() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateInternationalFormatWithTime() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKFormat() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTime() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeSpaceSeparator() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKFormatWithSlashes() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeWithSlashes() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
		final MonitoringDataRecord record = createValidRecord();
		final String testDate = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringDateFutureDateOnly() {
		final MonitoringDataRecord record = createValidRecord();
		final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
		final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringDateFutureDateAndTime() {
		final MonitoringDataRecord record = createValidRecord();
		final LocalDateTime anHourFromNow = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
		final String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(ReturnsDate.from(testDate));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
	//
	//	@Test
	//	public void testOutDatedMonitoringDateInternationalFormatWithTime() {
	//		MonitoringDataRecord record = createValidRecord();
	//		LocalDateTime fiveYearsAgo = LocalDateTime.now(ZoneOffset.UTC).minusYears(5);
	//		String testDate = fiveYearsAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
	//		record.setMonitoringDate(ReturnsDate.from(testDate));
	//		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
	//		Assert.assertEquals(1, violations.size());
	//	}

	/*=================================================================================================================
	 *
	 * MONITORING PERIOD
	 *
	 *=================================================================================================================
	 */

	@Test
	public void testMonitoringPeriodNull() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPeriod(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMonitoringPeriodLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPeriod(RandomStringUtils.random(20));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint("");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(31));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testMonitoringPointSpecialCharacters() {
		final MonitoringDataRecord record = createValidRecord();
		final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (final char c : invalidCharacters.toCharArray()) {
			record.setMonitoringPoint(String.valueOf(c));
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setSampleReference(RandomStringUtils.randomAlphanumeric(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setSampleReference(RandomStringUtils.randomAlphanumeric(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testSampleReferenceSpecialCharacters() {
		final MonitoringDataRecord record = createValidRecord();
		final String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (final char c : invalidCharacters.toCharArray()) {
			record.setSampleReference(String.valueOf(c));
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setSampleBy(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setParameter(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testParameterEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setParameter("");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testParameterInvalid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setParameter("An invalid parameter value");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testParameterValid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setParameter("1,2,3,4-Tetrachlorobenzene");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setValue(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("  ");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testValueInvalid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("<>232323");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueValidLessThanInteger() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("<1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidGreaterThanInteger() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue(">1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidLessThanDecimal() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("<0.1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueValidGreaterThanDecimal() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue(">0.1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testValueInvalidLessThanDecimalNoLeadingZero() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("<.1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue(">.1");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidLessThanSignOnly() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("<");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidGreaterThanSignOnly() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue(">");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testValueInvalidMinusSignOnly() {
		final MonitoringDataRecord record = createValidRecord();
		record.setValue("-");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setTextValue(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testTextValueInvalid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setTextValue(RandomStringUtils.random(30));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	@Test
	public void testTextValueBooleans() {
		final MonitoringDataRecord record = createValidRecord();
		final String[] allowedBooleans = { "true", "false", "yes", "no", "1", "0", "True", "False", "tRuE", "yEs", "fAlSe" };

		for (final String bool : allowedBooleans) {
			record.setTextValue(bool);
			final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setUnit(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testUnitEmpty() {
		final MonitoringDataRecord record = createValidRecord();
		record.setUnit(" ");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}

	@Test
	public void testUnitValid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setUnit("Hazen");
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setReferencePeriod(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testReferencePeriodInvalid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setReferencePeriod(RandomStringUtils.random(30));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setMethStand(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testMethStandInvalid() {
		final MonitoringDataRecord record = createValidRecord();
		record.setMethStand(RandomStringUtils.random(31));
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setComments(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCommentsLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setComments(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setCic(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCicLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setCic(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setCas(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testCasLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setCas(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
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
		final MonitoringDataRecord record = createValidRecord();
		record.setRdCode(null);
		final Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}

	@Test
	public void testRdCodeLength() {
		final MonitoringDataRecord record = createValidRecord();
		record.setRdCode(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = this.validator.validate(record);
		Assert.assertEquals(0, violations.size());

		record.setRdCode(RandomStringUtils.random(256));
		violations = this.validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}

	/**
	 * Creates a {@link MonitoringDataRecord} instance with all values setup
	 * with a valid entry.
	 *
	 * @return a new {@link MonitoringDataRecord} which should pass validation
	 */
	private static MonitoringDataRecord createValidRecord() {
		final MonitoringDataRecord record = new MonitoringDataRecord();
		record.setEaId(new EaId("DP3431PC"));
		record.setSiteName("Site Name");
		record.setReturnType("EPR/IED Landfill Gas infrastructure monitoring");
		record.setMonitoringDate(ReturnsDate.from("2016-03-09T11:18:59"));
		record.setMonitoringPeriod("During all downwind monitoring");
		record.setMonitoringPoint("Borehole 1");
		record.setSampleReference("Sample Reference");
		record.setSampleBy("Sam Gardner-Dell");
		record.setParameter("1,1,1,2-Tetrachloroethane");
		record.setValue("<0.0006");
		record.setTextValue("Extreme weather");
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