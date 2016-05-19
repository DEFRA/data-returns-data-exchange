package uk.gov.ea.datareturns.unittests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.BooleanValue;
import uk.gov.ea.datareturns.util.TestUtils;

public class BooleanValueTests {

	@Test
	public void testBooleanValueUtilityClassDefinition() throws ReflectiveOperationException {
		TestUtils.assertUtilityClassWellDefined(BooleanValue.class);
	}

	@Test
	public void testTextValueBooleans() {
		final Map<String, Boolean> booleanTests = new HashMap<>();
		booleanTests.put("true", Boolean.TRUE);
		booleanTests.put("yes", Boolean.TRUE);
		booleanTests.put("1", Boolean.TRUE);
		booleanTests.put("tRuE", Boolean.TRUE);
		booleanTests.put("True", Boolean.TRUE);
		booleanTests.put("false", Boolean.FALSE);
		booleanTests.put("no", Boolean.FALSE);
		booleanTests.put("0", Boolean.FALSE);
		booleanTests.put("fAlSe", Boolean.FALSE);
		booleanTests.put("somethingelse", null);
		booleanTests.put(null, null);

		for (final Map.Entry<String, Boolean> entry : booleanTests.entrySet()) {
			Assert.assertTrue(BooleanValue.from(entry.getKey()) == entry.getValue());
		}
	}
}
