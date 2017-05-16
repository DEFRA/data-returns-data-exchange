package uk.gov.ea.datareturns.tests.unit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.domain.validation.rules.FileType.CSV;
import static uk.gov.ea.datareturns.domain.validation.rules.FileType.XML;

/**
 * Tests for File types.
 * Reasons included just for completeness.
 */
public class FileTypeTests {
    @Test
    public void testFileTypes() {
        assertThat(CSV.getExtension()).isEqualTo("csv");
        assertThat(XML.getExtension()).isEqualTo("xml");
    }
}
