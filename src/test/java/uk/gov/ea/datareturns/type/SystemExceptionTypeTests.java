package uk.gov.ea.datareturns.type;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.type.SystemExceptionType.CONVERSION;
import static uk.gov.ea.datareturns.type.SystemExceptionType.DESERIALIZE;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_DELETE;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_READ;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_SAVE;
import static uk.gov.ea.datareturns.type.SystemExceptionType.FILE_UNLOCATABLE;
import static uk.gov.ea.datareturns.type.SystemExceptionType.NOTIFICATION;
import static uk.gov.ea.datareturns.type.SystemExceptionType.SERIALIZATION;
import static uk.gov.ea.datareturns.type.SystemExceptionType.SERVICE;
import static uk.gov.ea.datareturns.type.SystemExceptionType.TRANSFORMER;
import static uk.gov.ea.datareturns.type.SystemExceptionType.UNPROCESSABLE_ENTITY;
import static uk.gov.ea.datareturns.type.SystemExceptionType.VALIDATION;

import org.junit.Test;

/**
 * Tests for System Exception Types.
 * Reasons tested just for completeness but critical codes are kept up to date as the outside world depends on these!
 */
public class SystemExceptionTypeTests
{
	@Test
	public void coverage()
	{
		assertThat(SystemExceptionType.values().length).isGreaterThan(0);
		assertThat(SystemExceptionType.valueOf("FILE_SAVE").getCode()).isEqualTo(601);
	}

	@Test
	public void testCodes()
	{
		assertThat(FILE_SAVE.getCode()).isEqualTo(601);
		assertThat(FILE_UNLOCATABLE.getCode()).isEqualTo(602);
		assertThat(FILE_READ.getCode()).isEqualTo(603);
		assertThat(NOTIFICATION.getCode()).isEqualTo(604);
		assertThat(SERVICE.getCode()).isEqualTo(605);
		assertThat(FILE_DELETE.getCode()).isEqualTo(606);
		assertThat(VALIDATION.getCode()).isEqualTo(607);
		assertThat(TRANSFORMER.getCode()).isEqualTo(608);
		assertThat(SERIALIZATION.getCode()).isEqualTo(609);
		assertThat(DESERIALIZE.getCode()).isEqualTo(610);
		assertThat(CONVERSION.getCode()).isEqualTo(611);

		assertThat(UNPROCESSABLE_ENTITY.getCode()).isEqualTo(422);
	}

	@Test
	public void testReasons()
	{
		assertThat(FILE_SAVE.getReason()).isEqualTo("Cannot Save File");
		assertThat(FILE_UNLOCATABLE.getReason()).isEqualTo("Cannot Locate File");
		assertThat(FILE_READ.getReason()).isEqualTo("Cannot Read File");
		assertThat(NOTIFICATION.getReason()).isEqualTo("Notification failed");
		assertThat(SERVICE.getReason()).isEqualTo("Service failure");
		assertThat(FILE_DELETE.getReason()).isEqualTo("Cannot Delete File");
		assertThat(VALIDATION.getReason()).isEqualTo("Validation failed");
		assertThat(TRANSFORMER.getReason()).isEqualTo("Transformation failed");
		assertThat(SERIALIZATION.getReason()).isEqualTo("Serialize failed");
		assertThat(DESERIALIZE.getReason()).isEqualTo("Deserialize failed");
		assertThat(CONVERSION.getReason()).isEqualTo("Conversion failed");
		assertThat(UNPROCESSABLE_ENTITY.getReason()).isEqualTo("Unprocessable entity");
	}
}
