package uk.gov.ea.datareturns.unittests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.DateFormat;
import uk.gov.ea.datareturns.util.TestUtils;

/**
 * Tests the {@link DateFormat} rules meet the application specification
 *
 * @author Sam Gardner-Dell
 */
public class DateFormatTests {

	@Test
	public void testBooleanValueUtilityClassDefinition() throws ReflectiveOperationException {
		TestUtils.assertUtilityClassWellDefined(DateFormat.class);
	}

	@Test
	public void testISODate() {
		final LocalDate now = LocalDate.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		final LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKDashedDate() {
		final LocalDate now = LocalDate.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		final LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKSlashedDate() {
		final LocalDate now = LocalDate.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		final LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testISODateTimeTSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testISODateTimeSpaceSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKSlashedDateTimeTSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKSlashedDateTimeSpaceSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKDashedDateTimeTSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKDashedDateTimeSpaceSeparator() {
		final LocalDateTime now = LocalDateTime.now();
		final String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testAmericanDateFails() {
		final String testDate = "12-31-2016 00:00:00";
		final LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNull(parsed);
	}
}
