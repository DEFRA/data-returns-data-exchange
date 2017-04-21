package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.Hierarchy;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations.ParameterHierarchy;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.HierarchyValidator;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Graham Willis
 * Validate that all entries meet the dependency validation requirements
 * specified in the dependencies.csv file, i.e. that given combination of return type,
 * releases and transfers parameter and unit is allowed
 */
@Component
public class ParameterHierarchyValidator implements ConstraintValidator<HierarchyValidator, DataSample> {

    @Override
    public boolean isValid(DataSample record, ConstraintValidatorContext context) {
        ReturnType returnTypeEntity = AbstractEntityValue.getEntity(record.getReturnType());
        ReleasesAndTransfers releasesAndTransfersEntity = AbstractEntityValue.getEntity(record.getReleasesAndTransfers());
        Parameter parameterEntity = AbstractEntityValue.getEntity(record.getParameter());
        Unit unitEntity = AbstractEntityValue.getEntity(record.getUnit());

        // We are instantiated through reflection so we need to get the validation engine via the spring application context
        ParameterHierarchy parameterHierarchy = SpringApplicationContextProvider.getApplicationContext().getBean(ParameterHierarchy.class);

        Set<Hierarchy.HierarchyEntity> entities = new HashSet<>();
        entities.add(returnTypeEntity);
        entities.add(releasesAndTransfersEntity);
        entities.add(parameterEntity);
        entities.add(unitEntity);

        // Call the dependency validation engine
        Pair<HierarchyLevel<? extends Hierarchy.HierarchyEntity>, Hierarchy.Result> validation
                = parameterHierarchy.validate(entities);

        String message = null;
        Hierarchy.Result result = validation.getRight();
        ControlledListsList level = validation.getLeft().getControlledList();

        if (result == Hierarchy.Result.OK) {
            return true;
        }

        switch (level) {
            case UNIT:
                message = MessageCodes.DependencyConflict.Unit;
                break;
            case PARAMETER:
                message = MessageCodes.DependencyConflict.Parameter;
                break;
            case RELEASES_AND_TRANSFER:
                message = MessageCodes.DependencyConflict.Rel_Trans;
                break;
            case RETURN_TYPE:
                message = MessageCodes.DependencyConflict.Rtn_Type;
                break;
        }

        if (message != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return false;
    }

    @Override
    public void initialize(HierarchyValidator constraintAnnotation) {

    }

}
