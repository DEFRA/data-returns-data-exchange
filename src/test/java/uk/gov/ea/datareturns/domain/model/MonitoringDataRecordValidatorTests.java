/**
 * 
 */
package uk.gov.ea.datareturns.domain.model;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

	@Test
	public void testBlankReturnType() {
		MonitoringDataRecord record = createValidRecord();
		record.setReturnType("   ");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
		// We'll get 2 violations back - one for the field being blank, the second for the controlled list value check
		for (ConstraintViolation<MonitoringDataRecord> violation : violations) {
//			violation.get
//			System.out.println("Annot: " + violation.getConstraintDescriptor().getAnnotation().);
//			System.out.println("Constraints " + violation.getConstraintDescriptor().getConstraintValidatorClasses());
//			System.out.println(violation.getPropertyPath());
//			System.out.println(violation.getMessageTemplate() + "|" +  violation.getMessage());
		}
		Assert.assertEquals(2, violations.size());
	}
	
	@Test
	public void testInvalidReturnType() {
		MonitoringDataRecord record = createValidRecord();
		record.setReturnType("Invalid Return Type Value");
		Set<ConstraintViolation<MonitoringDataRecord>> violations = validator.validate(record);
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
		record.setMonitoringFrequency("Quarterly in first year.  Then Bi-annual");
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
