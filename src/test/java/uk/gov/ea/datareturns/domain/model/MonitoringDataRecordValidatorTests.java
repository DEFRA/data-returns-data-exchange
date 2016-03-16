/**
 * 
 */
package uk.gov.ea.datareturns.domain.model;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the validation constraints the MonitoringDataRecord class
 * 
 * @author Sam Gardner-Dell
 */
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
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}
	
	@Test
	public void testPermitNumberEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setPermitNumber("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
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
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testMonitoringDateEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		Assert.assertEquals(2, violations.size());
	}
	@Test
	public void testMonitoringDateInvalidFormat() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("2016/03/16");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testMonitoringDateInternationalFormat() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("2016-03-16");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateInternationalFormatWithTime() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("2016-03-16T09:00:00");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKFormat() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("16-03-2016");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	@Test
	public void testMonitoringDateUKWithTime() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringDate("16-03-2016T09:00:00");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
	}
	
	

	/*=================================================================================================================
	 *
	 * MONITORING FREQUENCY
	 * 
	 *=================================================================================================================
	 */
	
	@Test
	public void testMonitoringFrequencyLength() {
		MonitoringDataRecord record = createValidRecord();
		record.setMonitoringFrequency(RandomStringUtils.random(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setMonitoringFrequency(RandomStringUtils.random(31));
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
		record.setMonitoringPoint(RandomStringUtils.random(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setMonitoringPoint(RandomStringUtils.random(31));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
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
		record.setSampleReference(RandomStringUtils.random(255));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setSampleReference(RandomStringUtils.random(256));
		violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
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
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testParameterEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setParameter("");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
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
	public void testValueValidLessThanDecimalNoLeadingZero() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue("<.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		// TODO: NOT SURE IF THIS IS REALLY VALID?????
	}
	@Test
	public void testValueValidGreaterThanDecimalNoLeadingZero() {
		MonitoringDataRecord record = createValidRecord();
		record.setValue(">.1");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		// TODO: NOT SURE IF THIS IS REALLY VALID?????		
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
		Assert.assertEquals(1, violations.size());
	}
	@Test
	public void testUnitEmpty() {
		MonitoringDataRecord record = createValidRecord();
		record.setUnit(" ");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(1, violations.size());
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
		record.setMethStand(RandomStringUtils.random(30));
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		Assert.assertEquals(0, violations.size());
		
		record.setMethStand(RandomStringUtils.random(31));
		violations = validator.validate(record);
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
		MonitoringDataRecord record = new MonitoringDataRecord();
		record.setPermitNumber("AA1234");
		record.setSiteName("Site Name");
		record.setReturnType("EPR/IED Landfill Gas infrastructure monitoring");
		record.setMonitoringDate("2016-03-09T11:18:59");
		record.setMonitoringFrequency("Quarterly");
		record.setMonitoringPoint("Borehole 1");
		record.setSampleReference("Sample Reference");
		record.setSampleBy("Sam Gardner-Dell");
		record.setParameter("1,1,1,2-Tetrachloroethane");
		record.setValue("<0.0006");
		record.setTextValue("Some text value");
		record.setUnit("m3/s");
		record.setReferencePeriod(
				"Bi-annual periodic measurement average value over sample period of between 6 and 8 hours.");
		record.setMethStand("Method or standard");
		record.setComments("Free text comments entered in this field.");
		record.setCic("True");
		record.setCas("100-74-3");
		record.setRdCode("D13");
		return record;
	}
}
