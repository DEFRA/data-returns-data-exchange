package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.COLUMN_NAME_NOT_FOUND;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.FILE_KEY_MISMATCH;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.INVALID_CONTENTS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.MULTIPLE_PERMITS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.NO_RETURNS;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.PERMIT_NOT_FOUND;
import static uk.gov.ea.datareturns.type.ApplicationExceptionType.UNSUPPORTED_FILE_TYPE;

import org.junit.Test;

/**
 * Tests for Application Exception Types.
 * Reasons included just for completeness but critical status codes are kept up to date as the outside world depends on these!
 */
public class ApplicationExceptionTypeTests
{
	public static int TYPE_COUNT = 7;

	@Test
	public void coverage()
	{
		assertThat(ApplicationExceptionType.values().length).isGreaterThan(0);
		assertThat(ApplicationExceptionType.valueOf("UNSUPPORTED_FILE_TYPE").getAppStatusCode()).isEqualTo(700);
	}

	@Test
	public void testCountTypes()
	{
		assertThat(ApplicationExceptionType.values().length).isEqualTo(TYPE_COUNT);
	}

	@Test
	public void testAppStatusCodes()
	{
		assertThat(UNSUPPORTED_FILE_TYPE.getAppStatusCode()).isEqualTo(700);
		assertThat(INVALID_CONTENTS.getAppStatusCode()).isEqualTo(701);
		assertThat(NO_RETURNS.getAppStatusCode()).isEqualTo(702);
		assertThat(MULTIPLE_PERMITS.getAppStatusCode()).isEqualTo(703);
		assertThat(PERMIT_NOT_FOUND.getAppStatusCode()).isEqualTo(704);
		assertThat(FILE_KEY_MISMATCH.getAppStatusCode()).isEqualTo(705);
		assertThat(COLUMN_NAME_NOT_FOUND.getAppStatusCode()).isEqualTo(706);
	}

	@Test
	public void testReasons()
	{
		assertThat(UNSUPPORTED_FILE_TYPE.getReason()).isEqualTo("Unsupported File Type");
		assertThat(INVALID_CONTENTS.getReason()).isEqualTo("Invalid contents");
		assertThat(NO_RETURNS.getReason()).isEqualTo("No Returns");
		assertThat(MULTIPLE_PERMITS.getReason()).isEqualTo("Multiple Permits");
		assertThat(PERMIT_NOT_FOUND.getReason()).isEqualTo("Permit not found");
		assertThat(FILE_KEY_MISMATCH.getReason()).isEqualTo("File Key Mismatch");
		assertThat(COLUMN_NAME_NOT_FOUND.getReason()).isEqualTo("Column name not found in schema error message");
	}
}
