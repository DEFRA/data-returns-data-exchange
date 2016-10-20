package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.*;
import uk.gov.ea.datareturns.domain.jpa.service.DependencyValidation;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.RecordConstraintValidator;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import javax.validation.ConstraintValidatorContext;

/**
 * Validate that all entries meet the dependency validation requirements
 * specified in the dependencies.csv file, i.e. that given combination of return type,
 * releases and transfers parameter and unit is allowed
 */
@Component
public class DependencyValidator implements RecordConstraintValidator<DataSample> {

    @Override
    public boolean isValid(DataSample record, ConstraintValidatorContext context) {
        ReturnType returnType = record.getReturnType().getEntity();
        ReleasesAndTransfers releasesAndTransfers = null;
        Parameter parameter = record.getParameter().getEntity();
        Unit unit = record.getUnit().getEntity();

        // We are instantiated through reflection so we need to get the validation engine via the spring application context
        DependencyValidation dependencyValidation = SpringApplicationContextProvider.getApplicationContext().getBean(DependencyValidation.class);

        // Call the dependency validation engine
        Pair<ControlledListsList, DependencyValidation.Result> validation
                = dependencyValidation.validate(returnType, releasesAndTransfers, parameter, unit);

        String message = null;
        DependencyValidation.Result result = validation.getRight();
        ControlledListsList level = validation.getLeft();

        if (result == DependencyValidation.Result.OK) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();

            switch (level) {
                case UNITS:
                    message = "{DR9450-Conflict}";
                    break;
                case PARAMETERS:
                    message = "{DR9430-Conflict}";
                   break;
                case RELEASES_AND_TRANSFERS:
                    message = "{DR9570-Conflict}";
                    break;
                case RETURN_TYPE:
                    message = "{DR9040-Conflict}";
                    break;
            }
        }
        
        context.buildConstraintViolationWithTemplate(message);

        return false;
    }
}
