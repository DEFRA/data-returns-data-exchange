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
	public void coverage() {
		assertThat(FileType.values().length).isGreaterThan(0);
		assertThat(FileType.valueOf("CSV").getFileType()).isEqualTo("csv");
	}

	@Test
	public void testFileTypes() {
		assertThat(CSV.getFileType()).isEqualTo("csv");
		assertThat(XML.getFileType()).isEqualTo("xml");
	}

	@Test
	public void testDescriptions() {
		assertThat(CSV.getReason()).isEqualTo("Comma Separated Values");
		assertThat(XML.getReason()).isEqualTo("Extensible Markup Language");
	}
}
