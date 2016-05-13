package uk.gov.ea.datareturns.unittests;

import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.util.TestUtils;

/**
 * Tests the {@link DataReturnsHeadersTests} rules meet the application specification
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsHeadersTests {

	@Test
	public void testUtilityClassDefinition() throws ReflectiveOperationException {
		TestUtils.assertUtilityClassWellDefined(DataReturnsHeaders.class);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAllHeadingsUnmodifiable() {
		DataReturnsHeaders.getAllHeadings().add("Test");
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testMandatoryHeadingsUnmodifiable() {
		DataReturnsHeaders.getMandatoryHeadings().add("Test");
	}
	
	
	@Test
	public void testAllHeadingsIncludesMandatoryHeadings() {
		DataReturnsHeaders.getAllHeadings().containsAll(DataReturnsHeaders.getMandatoryHeadings());
	}
}
