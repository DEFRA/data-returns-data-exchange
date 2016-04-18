package uk.gov.ea.datareturns.unittests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.ea.datareturns.domain.model.rules.DateFormat;

/**
 * Tests the {@link DateFormat} rules meet the application specification
 * 
 * @author Sam Gardner-Dell
 */
public class DateFormatTests {
	@Test
	public void testISODate() {
		LocalDate now = LocalDate.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testUKDashedDate() {
		LocalDate now = LocalDate.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}
	@Test
	public void testUKSlashedDate() {
		LocalDate now = LocalDate.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate parsed = DateFormat.parseDate(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testISODateTimeTSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testISODateTimeSpaceSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKSlashedDateTimeTSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testUKSlashedDateTimeSpaceSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}

	@Test
	public void testUKDashedDateTimeTSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testUKDashedDateTimeSpaceSeparator() {
		LocalDateTime now = LocalDateTime.now();
		String testDate = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNotNull(parsed);
	}
	
	@Test
	public void testAmericanDateFails() {
		String testDate = "12-31-2016 00:00:00";
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		Assert.assertNull(parsed);
	}
	
	@Test
	public void testToStandardDateFormat() {
		String testDate = "31/01/2016";
		LocalDate parsed = DateFormat.parseDate(testDate);
		String standardFormat = DateFormat.toStandardFormat(parsed);
		Assert.assertEquals(standardFormat, "2016-01-31");
	}

	@Test
	public void testToStandardDateFormatNull() {
		LocalDate value = null;
		Assert.assertNull(DateFormat.toStandardFormat(value));
	}
	
	@Test
	public void testToStandardDateTimeFormat() {
		String testDate = "31/01/2016T09:01:02";
		LocalDateTime parsed = DateFormat.parseDateTime(testDate);
		String standardFormat = DateFormat.toStandardFormat(parsed);
		Assert.assertEquals(standardFormat, "2016-01-31T09:01:02");
	}

	@Test
	public void testToStandardDateTimeFormatNull() {
		LocalDateTime value = null;
		Assert.assertNull(DateFormat.toStandardFormat(value));
	}
}
