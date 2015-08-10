package uk.gov.ea.datareturns.resources;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;

public class CsvValidatorTest
{
	private String location = "/Users/adrianharrison/Documents/Projects/rcdp/data-returns/alpha/uploaded/";

	public CsvValidatorTest()
	{
	}

//	@BeforeClass
//	public static void setUpClass() throws Exception
//	{
//	}
//
//	@AfterClass
//	public static void tearDownClass() throws Exception
//	{
//	}
//
//	@Before
//	public void setUp()
//	{
//	}
//
//	@After
//	public void tearDown()
//	{
//	}
//
//	@Test
//	public void testIsValid()
//	{
//		String uploadedFileLocation = location + "T001_SAMPLE CSV File_1.csv";
//		String uploadedFileSchema = location + "data-schema.csvs";
//
//		Boolean failFast = false;
//		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();
//
//		List<FailMessage> messages = CsvValidator.validate(uploadedFileLocation, uploadedFileSchema, failFast, pathSubstitutions, true);
//
//		assertTrue(messages.size() == 0);
//	}
//
//	@Test
//	public void testIsInValid()
//	{
//		String uploadedFileLocation = location + "T003_SAMPLE CSV File_eaID.csv";
//		String uploadedFileSchema = location + "data-schema.csvs";
//
//		Boolean failFast = false;
//		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();
//
//		List<FailMessage> messages = CsvValidator.validate(uploadedFileLocation, uploadedFileSchema, failFast, pathSubstitutions, true);
//
//		assertTrue(messages.size() == 1);
//	}
//
}
