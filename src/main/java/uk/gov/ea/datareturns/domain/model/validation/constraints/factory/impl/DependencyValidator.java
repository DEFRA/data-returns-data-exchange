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
        uk.gov.ea.datareturns.domain.model.fields.impl.ReturnType returnType = record.getReturnType();
        //uk.gov.ea.datareturns.domain.model.fields.impl.ReleasesAndTransfers releasesAndTransfers = null;
        uk.gov.ea.datareturns.domain.model.fields.impl.Parameter parameter = record.getParameter();
        uk.gov.ea.datareturns.domain.model.fields.impl.Unit unit = record.getUnit();

        ReturnType returnTypeEntity = (returnType != null) ? returnType.getEntity() : null;
        ReleasesAndTransfers releasesAndTransfersEntity = null;
        Parameter parameterEntity = (parameter != null) ? parameter.getEntity() : null;
        Unit unitEntity = (unit != null) ? unit.getEntity() : null;

        // We are instantiated through reflection so we need to get the validation engine via the spring application context
        DependencyValidation dependencyValidation = SpringApplicationContextProvider.getApplicationContext().getBean(DependencyValidation.class);

        // Call the dependency validation engine
        Pair<ControlledListsList, DependencyValidation.Result> validation
                = dependencyValidation.validate(returnTypeEntity, releasesAndTransfersEntity, parameterEntity, unitEntity);

        String message = null;
        DependencyValidation.Result result = validation.getRight();
        ControlledListsList level = validation.getLeft();

        if (result == DependencyValidation.Result.OK) {
            return true;
        } else {
            // We will disregard 'missing' because this
            // is detected by the straight forward controlled lists check
            // (Except in the case of releases)
            if (result == DependencyValidation.Result.EXPECTED && level != ControlledListsList.RELEASES_AND_TRANSFERS) {
                return true;
            } else {
                switch (level) {
                    case UNITS:
                        message = "{DR9450-Combination}";
                        break;
                    case PARAMETERS:
                        message = "{DR9430-Combination}";
                        break;
                    case RELEASES_AND_TRANSFERS:
                        message = "{DR9570-Combination}";
                        break;
                    case RETURN_TYPE:
                        message = "{DR9410-Combination}";
                        break;
                }
            }
        }

        if (message != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(level.getfieldDefinition().getName())
                    .addConstraintViolation();
        }

        return false;
    }
}
