package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.FileType.CSV;
import static uk.gov.ea.datareturns.type.FileType.XML;

import org.junit.Test;

/**
 * Tests for File types.
 * Reasons included just for completeness.
 */
public class FileTypeTests
{
	@Test
	public void coverage()
	{
		assertThat(FileType.values().length).isGreaterThan(0);
		assertThat(FileType.valueOf("CSV").getFileType()).isEqualTo("csv");
	}

	@Test
	public void testFileTypes()
	{
		assertThat(CSV.getFileType()).isEqualTo("csv");
		assertThat(XML.getFileType()).isEqualTo("xml");
	}

	@Test
	public void testDescriptions()
	{
		assertThat(CSV.getReason()).isEqualTo("Comma Separated Values");
		assertThat(XML.getReason()).isEqualTo("Extensible Markup Language");
	}
}
