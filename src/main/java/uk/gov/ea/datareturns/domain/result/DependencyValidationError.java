package uk.gov.ea.datareturns.domain.result;

import java.util.Optional;

/**
 * Created by graham on 21/10/16.
 */
public class DependencyValidationError extends ValidationError {
    private Optional<String> returnTypeName;
    private Optional<String> releasesAndTransfersName;
    private Optional<String> parameterName;
    private Optional<String> unitName;

    public DependencyValidationError(Optional<String> returnTypeName, Optional<String> releasesAndTransfersName, Optional<String> parameterName, Optional<String> unitName) {
        this.returnTypeName = returnTypeName;
        this.releasesAndTransfersName = releasesAndTransfersName;
        this.parameterName = parameterName;
        this.unitName = unitName;
    }

    public String getReturnTypeName() {
        return returnTypeName.orElse(null);
    }

    public String getReleasesAndTransfersName() {
        return releasesAndTransfersName.orElse(null);
    }

    public String getParameterName() {
        return parameterName.orElse(null);
    }

    public String getUnitName() {
        return unitName.orElse(null);
    }

}
