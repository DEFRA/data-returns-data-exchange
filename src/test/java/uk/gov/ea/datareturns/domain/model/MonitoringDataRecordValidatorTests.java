/**
 * 
 */
package uk.gov.ea.datareturns.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the validation constraints the MonitoringDataRecord class
 * 
 * @author Sam Gardner-Dell
 */

// TODO: Move this back to being a unit test rather than an integration test and find a way to mock out the controlled list validation
// It looks like you cannot mock a class that is created via reflection as per ControlledListValidator.initialise
// I also tried creating a proxy class inside one of the auditors but that failed too!
// @RunWith(PowerMockRunner.class)
// @PrepareForTest({MonitoringDataRecord.class, ReturnTypeListAuditorProxy.class})
public class MonitoringDataRecordValidatorTests {

	private static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	
	@Test
	public void testValidRecord() {
		MonitoringDataRecord record = createValidRecord();
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setPermitNumber("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		// We'll get 3 violations back - one for the field being blank, one for the pattern check and one for the controlled list value check
		Assert.assertEquals(3, violations.size());
	}
	
	@Test
	public void testPermitNumberEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setPermitNumber("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setReturnType("   ");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}
	
	@Test
	public void testInvalidReturnType() {
		MonitoringDataRecord record = createValidRecord();
		record.setReturnType("Invalid Return Type Value");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}
	@Test
	public void testMonitoringDateEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}
	@Test
	public void testMonitoringDateInvalidFormat() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertFalse(violations.isEmpty());
	}
	@Test
	public void testMonitoringDateInternationalFormat() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateInternationalFormatWithTime() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateInternationalFormatWithTimeSpaceSeparator() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKFormat() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKWithTime() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKWithTimeSpaceSeparator() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKFormatWithSlashes() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKWithTimeWithSlashes() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKWithTimeWithSlashesSpaceSeparator() {
		MonitoringDataRecord record = createValidRecord();
		String testDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	
	@Test
	public void testMonitoringDateFutureDateOnly() {
		MonitoringDataRecord record = createValidRecord();
		LocalDateTime anHourFromNow = LocalDateTime.now().plusDays(1);
		String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	
	
	@Test
	public void testMonitoringDateFutureDateAndTime() {
		MonitoringDataRecord record = createValidRecord();
		LocalDateTime anHourFromNow = LocalDateTime.now().plusHours(1);
		String testDate = anHourFromNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		record.setMonitoringDate(testDate);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	
	
// TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
//	
//	@Test
//	public void testOutDatedMonitoringDateInternationalFormatWithTime() {
//		MonitoringDataRecord record = createValidRecord();
//		LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
//		String testDate = fiveYearsAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
//		record.setMonitoringDate(testDate);
//		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
//		Assert.assertEquals(1, violations.size());
//	}

	/*=================================================================================================================
	 *
	 * MONITORING FREQUENCY
	 * 
	 *=================================================================================================================
	 */
	
	@Test
	public void testMonitoringFrequencyLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPeriod(RandomStringUtils.random(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setMonitoringPeriod(RandomStringUtils.random(31));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testMonitoringPointEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testMonitoringPointLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setMonitoringPoint(RandomStringUtils.randomAlphanumeric(31));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testMonitoringPointSpecialCharacters() {
		MonitoringDataRecord record = createValidRecord();
		String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (char c : invalidCharacters.toCharArray()) {
			record.setMonitoringPoint(String.valueOf(c));
			Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setSampleReference(RandomStringUtils.randomAlphanumeric(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setSampleReference(RandomStringUtils.randomAlphanumeric(256));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testSampleReferenceSpecialCharacters() {
		MonitoringDataRecord record = createValidRecord();
		String invalidCharacters = "!\"£$%^&*()-_=+[]{};:'@#~,<.>/?\\|`¬€";
		for (char c : invalidCharacters.toCharArray()) {
			record.setSampleReference(String.valueOf(c));
			Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setSampleBy(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setSampleBy(RandomStringUtils.random(256));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setParameter(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testParameterEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setParameter("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testParameterInvalid() {
		MonitoringDataRecord record = createValidRecord();
		record.setParameter("An invalid parameter value");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testParameterValid() {
		MonitoringDataRecord record = createValidRecord();
		record.setParameter("1,2,3,4-Tetrachlorobenzene");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setValue(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testValueEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("  ");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testValueInvalid() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<>232323");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testValueValidLessThanInteger() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testValueValidGreaterThanInteger() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue(">1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testValueValidLessThanDecimal() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<0.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testValueValidGreaterThanDecimal() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue(">0.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testValueInvalidLessThanDecimalNoLeadingZero() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testValueInvalidGreaterThanDecimalNoLeadingZero() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue(">.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}	
	@Test
	public void testValueInvalidLessThanSignOnly() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testValueInvalidGreaterThanSignOnly() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue(">");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}	
	@Test
	public void testValueInvalidMinusSignOnly() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("-");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setTextValue(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testTextValueLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setTextValue(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setTextValue(RandomStringUtils.random(256));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}	
	

	/*=================================================================================================================
	 *
	 * UNIT
	 * 
	 *=================================================================================================================
	 */
	@Test
	public void testUnitNull() {
		MonitoringDataRecord record = createValidRecord();
		record.setUnit(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testUnitEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setUnit(" ");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testUnitValid() {
		MonitoringDataRecord record = createValidRecord();
		record.setUnit("Hazen");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setReferencePeriod(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testReferencePeriodLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setReferencePeriod(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setReferencePeriod(RandomStringUtils.random(256));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setMethStand(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMethStandLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setMethStand(RandomStringUtils.random(31));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(2, violations.size());
	}	
	/*=================================================================================================================
	 *
	 * COMMENT
	 * 
	 *=================================================================================================================
	 */
	@Test
	public void testCommentsNull() {
		MonitoringDataRecord record = createValidRecord();
		record.setComments(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testCommentsLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setComments(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setComments(RandomStringUtils.random(256));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setCic(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testCicLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setCic(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setCic(RandomStringUtils.random(256));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setCas(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testCasLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setCas(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setCas(RandomStringUtils.random(256));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = createValidRecord();
		record.setRdCode(null);
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testRdCodeLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setRdCode(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setRdCode(RandomStringUtils.random(256));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}	
	
	
	/**
	 * Creates a {@link MonitoringDataRecord} instance with all values setup
	 * with a valid entry.
	 * 
	 * @return a new {@link MonitoringDataRecord} which should pass validation
	 */
	private static MonitoringDataRecord createValidRecord() {
//		TODO: Find a way to mock the auditor classes
//		ReturnTypeListAuditor rtAuditor = PowerMockito.mock(ReturnTypeListAuditor.class);
//		PowerMockito.doReturn(true).when(rtAuditor).isValid(Mockito.any());
//		PowerMockito.when(rtAuditor.isValid(Mockito.eq())).thenReturn(true);
//		
//		try {
//
//			System.out.println("ReturnTypeListAuditor mocked");
//			PowerMockito.whenNew(ReturnTypeListAuditorProxy.class).withNoArguments().thenThrow(new RuntimeException("WHAT IS GOING ON?"));
//			PowerMockito.whenNew(ReturnTypeListAuditor.class).withAnyArguments().thenReturn(rtAuditor);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}

		MonitoringDataRecord record = new MonitoringDataRecord();
		record.setPermitNumber("AB3002SQ");
		record.setSiteName("Site Name");
		record.setReturnType("EPR/IED Landfill Gas infrastructure monitoring");
		record.setMonitoringDate("2016-03-09T11:18:59");
		record.setMonitoringPeriod("Quarterly");
		record.setMonitoringPoint("Borehole 1");
		record.setSampleReference("Sample Reference");
		record.setSampleBy("Sam Gardner-Dell");
		record.setParameter("1,1,1,2-Tetrachloroethane");
		record.setValue("<0.0006");
		record.setTextValue("Some text value");
		record.setUnit("m3/s");
		record.setReferencePeriod(
				"Bi-annual periodic measurement average value over sample period of between 6 and 8 hours.");
		record.setMethStand("BS ISO 15713");
		record.setComments("Free text comments entered in this field.");
		record.setCic("True");
		record.setCas("100-74-3");
		record.setRdCode("D13");
		return record;
	}
}
