package uk.gov.ea.datareturns.web.resource.v1.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.DatasetProperties;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Individual request item used in a batch dataset request
 *
 * @author Sam Gardner-Dell
 */
@XmlRootElement(name = "request")
public class BatchDatasetRequestItem {
    @JsonProperty("dataset_id")
    @ApiModelProperty(name = "dataset_id", notes = "The target `dataset_id`")
    private String datasetId;

    @JsonProperty("preconditions")
    @ApiModelProperty(name = "preconditions", notes = "Support for RFC7232 conditional requests based on last modification time")
    private Preconditions preconditions;

    @JsonProperty("properties")
    @ApiModelProperty(notes = "The details to be associated with the given `dataset_id`")
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