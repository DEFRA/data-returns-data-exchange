package uk.gov.ea.datareturns.aspects;

import org.aspectj.lang.annotation.Pointcut;
import uk.gov.ea.datareturns.domain.jpa.dao.Key;
import uk.gov.ea.datareturns.domain.model.DataSample;

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
     * @param nameOrAlias the name or alias of the entityh
     */
    @Pointcut("transformControlledListEntityToPreferred(nameOrAlias) || transformUniqueIdentifierToPreferred(nameOrAlias)")
    public void transformToPreferred(Key nameOrAlias) {
    }

    /**
     * Pointcut to intercept validation of the model from an uploaded CSV file
     *
     * @param model the {@link List} of {@link DataSample} records being validated
     */    @Pointcut("execution(public * uk.gov.ea.datareturns.domain.model.validation.DataSampleValidator.validateModel(*)) && args(model)")
    public void modelValidation(List<DataSample> model) {
    }
}
