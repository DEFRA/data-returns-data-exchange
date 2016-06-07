package uk.gov.ea.datareturns.tests.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.domain.model.rules.FileType.CSV;
import static uk.gov.ea.datareturns.domain.model.rules.FileType.XML;

import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.FileType;

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
