package uk.gov.ea.datareturns.tests.integration.resource;

import com.univocity.parsers.common.TextParsingException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.io.csv.CSVColumnReader;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.storage.StorageException;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;
import uk.gov.ea.datareturns.domain.storage.StorageProvider.StoredFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Processor Integration Tests
 *
 * The purpose of tests in this class is to check that for a given input file the output is transformed as expected.
 *
 * For example, we accept boolean values true, false, yes, no, 1, 0 (or any case variation of this) but should always output
 * boolean values using the standard true/false notation
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class ProcessorIntegrationTests {
    public final static String IO_TESTS_FOLDER = "/testfiles/iotests/";
    public final static String BOOLEAN_TESTS = "testTextValueSubstitution.csv";
    public final static String RETURN_PERIOD_TESTS = "testReturnPeriodSubstitution.csv";

    public final static String RTN_TYPE_SUB = "testReturnTypeSubstitution.csv";
    public final static String REF_PERIOD_SUB = "testRefPeriodSubstitution.csv";
    public final static String QUALIFIER_SUB = "testQualifierSubstitution.csv";
    public final static String METH_STAND_SUB = "testMethStandSubstitution.csv";
    public final static String PARAMETER_SUB = "testParameterSubstitution.csv";
    public final static String UNITS_SUB = "testUnitsSubstitution.csv";
    public final static String TEXT_SUB = "testTextValueSubstitution.csv";

    @Inject
    private ApplicationContext context;

    @Inject
    private StorageProvider storage;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private QualifierDao qualifierDao;

    @Inject
    private ReferencePeriodDao referencePeriodDao;

    @Inject
    private MethodOrStandardDao methodOrStandardDao;

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private UnitDao unitDao;

    @Inject
    private TextValueDao textValueDao;

    /**
     * Tests return period values are converted as necessary.
     */
    @Test
    public void testReturnPeriodSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(RETURN_PERIOD_TESTS));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final String[] expected = {
                "Qtr 4 2014",
                "Water year 2016",
                "Week 52 2014",
                "Week 03 2013",
                "Water year 2015",
                "Jan 2015",
                "2016",
                "2016/17"
        };
        verifyColumnContainsExactly(outputFiles.iterator().next(), FieldDefinition.Rtn_Period.getName(), expected);
    }

    @Test
    public void testTextValueValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(TEXT_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<TextValue> textValues = textValueDao.list();
        final List<String> textValuesNames = textValues.stream().map(TextValue::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Value.getName(), textValuesNames);
    }

    @Test
    public void testReturnTypeValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(RTN_TYPE_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<ReturnType> returnTypes = returnTypeDao.list();
        final List<String> returnTypeNames = returnTypes.stream().map(ReturnType::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Rtn_Type.getName(), returnTypeNames);
    }

    @Test
    public void testQualifierValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(QUALIFIER_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<Qualifier> qualifiers = qualifierDao.list();
        final List<String> qualifierNames = qualifiers.stream().map(Qualifier::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Qualifier.getName(), qualifierNames);
    }

    @Test
    public void testRefPeriodValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(REF_PERIOD_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<ReferencePeriod> referencePeriod = referencePeriodDao.list();
        final List<String> referencePeriodNames = referencePeriod.stream().map(ReferencePeriod::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Ref_Period.getName(), referencePeriodNames);
    }

    @Test
    public void testMethodOrStandardValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(METH_STAND_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<MethodOrStandard> methodOrStandard = methodOrStandardDao.list();
        final List<String> methodOrStandardNames = methodOrStandard.stream().map(MethodOrStandard::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Meth_Stand.getName(), methodOrStandardNames);
    }

    @Test
    public void testParameterValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(PARAMETER_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<Parameter> parameter = parameterDao.list();
        final List<String> parameterNames = parameter.stream().map(Parameter::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Parameter.getName(), parameterNames);
    }

    @Test
    public void testUnitValueSubstitution() {
        final Collection<File> outputFiles = getOutputFiles(getTestFileStream(UNITS_SUB));
        Assertions.assertThat(outputFiles.size()).isEqualTo(1);
        final List<Unit> unit = unitDao.list();
        final List<String> unitNames = unit.stream().map(Unit::getName).collect(Collectors.toList());
        verifyColumnDataContainedIn(outputFiles.iterator().next(), FieldDefinition.Unit.getName(), unitNames);
    }

    /**
     * Retrieve the set of output files which are created by the processors for the given {@link InputStream}
     *
     * @param inputStream the {@link InputStream} containing DEP compliant
     * @return
     */
    private Collection<File> getOutputFiles(final InputStream inputStream) {
        final FileUploadProcessor processor = this.context.getBean(FileUploadProcessor.class);

        processor.setClientFilename("ProcessorIntegrationTests.csv");
        processor.setInputStream(inputStream);

        DataExchangeResult result = null;
        try {
            result = processor.process();
        } catch (final ProcessingException e) {
            Assertions.fail("Processor exception thrown", e);
        }
        Assertions.assertThat(result.getAppStatusCode()).isLessThanOrEqualTo(0);
        Assertions.assertThat(result.getParseResult()).isNotNull();
        Assertions.assertThat(result.getParseResult().getMappings()).isNotEmpty();
        Assertions.assertThat(result.getUploadResult().getFileKey()).isNotEmpty();

        final String fileKey = result.getUploadResult().getFileKey();
        try {
            final StoredFile storedFile = this.storage.retrieveTemporaryData(fileKey);
            final File workingFolder = org.assertj.core.util.Files.temporaryFolder();
            final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(workingFolder, storedFile.getFile());
            return zipModel.getOutputFiles();
        } catch (StorageException | IOException e) {
            throw new AssertionError("Unable to retrieve stored file.", e);
        }
    }

    /**
     * For a given output CSV file, check that the values in the specified column match those that are expected
     *
     * @param csvFile the CSV file to parse
     * @param columnName the column header of the data to be checked
     * @param expectedValues the expected values to be found in the column (in document order)
     */
    private static void verifyColumnContainsExactly(final File csvFile, final String columnName, final String[] expectedValues) {
        try {
            final List<String> columnData = CSVColumnReader.readColumn(csvFile, columnName);
            Assertions.assertThat(expectedValues.length).isEqualTo(columnData.size());

            // Ensure that the expected values and column data match exactly (including order)
            Assertions.assertThat(expectedValues).containsExactlyElementsOf(columnData);
        } catch (final TextParsingException e) {
            throw new AssertionError("Unable to parse output CSV file.", e);
        }
    }

    /**
     * For a given output CSV file, check that the values in the specified column are contained by the list expected
     *
     * @param csvFile the CSV file to parse
     * @param columnName the column header of the data to be checked
     * @param allowedValues the expected values
     */
    private static void verifyColumnDataContainedIn(final File csvFile, final String columnName, final List<String> allowedValues) {
        try {
            final List<String> columnData = CSVColumnReader.readColumn(csvFile, columnName);
            Assertions.assertThat(allowedValues).containsAll(columnData);
        } catch (final TextParsingException e) {
            throw new AssertionError("Unable to parse output CSV file.", e);
        }
    }

    /**
     * Return an InputStream for a given test file
     *
     * @param testFile the name of the test file to resolve
     * @return an {@link InputStream} from the test file
     */
    private static InputStream getTestFileStream(final String testFile) {
        return ProcessorIntegrationTests.class.getResourceAsStream(IO_TESTS_FOLDER + testFile);
    }
}