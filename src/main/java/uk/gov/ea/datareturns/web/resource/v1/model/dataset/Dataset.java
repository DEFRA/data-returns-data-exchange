package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.EntityBase;

import java.io.Serializable;

/**
 * Dataset entity
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(parent = EntityBase.class)
public class Dataset extends EntityBase implements Serializable {
    @ApiModelProperty(name = "datasetProperties")
    private DatasetProperties properties;

    public Dataset() {
        super();
    }

    public DatasetProperties getProperties() {
        return properties;
    }

    public void setProperties(DatasetProperties properties) {
        this.properties = properties;
    }
}