package uk.gov.ea.datareturns.web.resource.v1.model.request;

import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Individual request item used in a batch dataset request
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "request")
public class BatchDatasetRequestItem {
    @XmlElement(name = "dataset_id")
    @ApiModelProperty(name = "dataset_id", notes = "The target `dataset_id`")
    private String datasetId;

    @XmlElement(name = "preconditions")
    @ApiModelProperty(name = "preconditions", notes = "Support for RFC7232 conditional requests based on last modification time")
    private Preconditions preconditions;

    @XmlElement(name = "properties")
    @ApiModelProperty(notes = "The details to be associated with the given `dataset_id`", required = true)
    private DatasetProperties properties;

    public BatchDatasetRequestItem() {

    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public Preconditions getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(Preconditions preconditions) {
        this.preconditions = preconditions;
    }

    public DatasetProperties getProperties() {
        return properties;
    }

    public void setProperties(DatasetProperties properties) {
        this.properties = properties;
    }
}