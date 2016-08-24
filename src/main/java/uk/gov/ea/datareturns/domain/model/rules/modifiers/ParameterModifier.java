package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Graham Willis: 23/08/16
 * Perform the standard name modification on the qualifier
 */
@Component
public class ParameterModifier implements EntityModifier {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ParameterModifier.class);

    @Inject
    ParameterDao parameterDao;

    @Override
    public Object doModify(Object input) {
        LOGGER.debug("Received parameter name: " + Objects.toString(input, ""));
        String modifiedParameterName = parameterDao.getStandardizedName(input.toString());
        LOGGER.debug("Modified parameter name: " + modifiedParameterName);
        return modifiedParameterName;
    }
}
