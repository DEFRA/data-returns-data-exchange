package uk.gov.ea.datareturns.domain.result;

/**
 * Created by graham on 21/10/16.
 */
public class DependencyValidationError extends ValidationError {
    private String returnTypeName;
    private String releasesAndTransfersName;
    private String parameterName;
    private String unitName;

    public DependencyValidationError(String returnTypeName, String releasesAndTransfersName, String parameterName, String unitName) {
        this.returnTypeName = returnTypeName;
        this.releasesAndTransfersName = releasesAndTransfersName;
        this.parameterName = parameterName;
        this.unitName = unitName;
    }

    public String getReturnTypeName() {
        return returnTypeName;
    }

    public String getReleasesAndTransfersName() {
        return releasesAndTransfersName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getUnitName() {
        return unitName;
    }

}
