package uk.gov.ea.datareturns.aspects;

import com.univocity.parsers.csv.CsvWriter;
import org.aspectj.lang.annotation.Pointcut;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.model.DataSample;

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
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.jpa.dao.AliasingEntityDao.getPreferred(*)) && args(nameOrAlias)")
    public void transformControlledListEntityToPreferred(Key nameOrAlias) {
    }

    /**
     * Pointcut for transform of unique identifier list entity to preferred value.
     * @param nameOrAlias the name or alias of the entity
     */
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.jpa.dao.UniqueIdentifierDao.getPreferred(*)) && args(nameOrAlias)")
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
    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator.validateModel(*)) && args(model)")
    public void modelValidation(List<DataSample> model) {
    }
}
