package uk.gov.ea.datareturns.tests.unit;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldDefinition;

/**
 * Tests the {@link DataReturnsHeadersTests} rules meet the application specification
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsHeadersTests {
    @Test(expected = UnsupportedOperationException.class)
    public void testAllHeadingsUnmodifiable() {
        FieldDefinition.ALL_FIELD_NAMES.add("Test");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMandatoryHeadingsUnmodifiable() {
        FieldDefinition.MANDATORY_FIELDS.add(FieldDefinition.Comments);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMandatoryHeadingNamesUnmodifiable() {
        FieldDefinition.MANDATORY_FIELD_NAMES.add("Test");
    }

    @Test
    public void testAllHeadingsIncludesMandatoryHeadings() {
        Assert.assertTrue(FieldDefinition.ALL_FIELD_NAMES.containsAll(FieldDefinition.MANDATORY_FIELD_NAMES));
    }
}
