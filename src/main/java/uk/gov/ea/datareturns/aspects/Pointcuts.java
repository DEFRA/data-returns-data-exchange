package uk.gov.ea.datareturns.aspects;

import com.univocity.parsers.csv.CsvWriter;
import org.aspectj.lang.annotation.Pointcut;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;

import java.io.File;
import java.util.List;

/**
 * Pointcut definitions
 *
 * @author Sam Gardner-Dell
 */
public class Pointcuts {
    /**
     * Pointcut for transform of controlled list entity to preferred value.
     * @param nameOrAlias the name or alias of the entity
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.jpa.dao.masterdata.AliasingEntityDao.getPreferred(*)) && args(nameOrAlias)")
    public void transformControlledListEntityToPreferred(Key nameOrAlias) {
    }

    /**
     * Pointcut for transform of unique identifier list entity to preferred value.
     * @param nameOrAlias the name or alias of the entity
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao.getPreferred(*)) && args(nameOrAlias)")
    public void transformUniqueIdentifierToPreferred(Key nameOrAlias) {
    }

    /**
     * Aggregate pointcut for the transformation of any entity to the preferred value
     *
     * @param nameOrAlias the name or alias of the entity
     */
    @Pointcut("transformControlledListEntityToPreferred(nameOrAlias) || transformUniqueIdentifierToPreferred(nameOrAlias)")
    public void transformToPreferred(Key nameOrAlias) {
    }

    /**
     * Pointcut to intercept each output file as it is written
     *
     * @param records the data returns records to be written
     * @param csvFile a reference to the {@link File} to be written
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor.write(..)) && args(records, csvFile)")
    public void writeOutputFile(List<DataSample> records, File csvFile) {
    }

    /**
     * Pointcut to intercept each record as it is written to an output file.
     *
     * @param writer the {@link CsvWriter} being used
     * @param record the {@link DataSample} being written
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.io.csv.DataSampleBeanWriterProcessor.write(..)) && args(writer, record)")
    public void writeDataSampleRecord(CsvWriter writer, DataSample record) {
    }

    /**
     * Pointcut to intercept validation of the model from an uploaded CSV file
     *
     * @param model the {@link List} of {@link DataSample} records being validated
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.validation.model.validation.DataSampleValidator.validateModel(*)) && args(model)")
    public void modelValidation(List<DataSample> model) {
    }

    /**
     * Pointcut to intercept submission of data to the downstream system
     *
     * @param originatorEmail the email address of the user that uploaded the source file that this output file has been created from
     * @param originatorFilename the name of the file the user uploaded to the datareturns service
     * @param eaId the EA Unique Identifier that the data in the output file pertains to
     * @param returnsCSVFile the CSV file to send to MonitorPro
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.monitorpro.TransportHandler.sendNotifications(..)) && args(originatorEmail, originatorFilename, eaId, returnsCSVFile)")
    public void dataSubmission(String originatorEmail, String originatorFilename, String eaId, File returnsCSVFile) {
    }
}
