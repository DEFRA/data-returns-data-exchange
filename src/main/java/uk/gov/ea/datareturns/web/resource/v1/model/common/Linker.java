package uk.gov.ea.datareturns.web.resource.v1.model.common;

/**
 * Created by sam on 23/05/17.
 */

import uk.gov.ea.datareturns.web.resource.v1.DatasetResource;
import uk.gov.ea.datareturns.web.resource.v1.DefinitionsResource;
import uk.gov.ea.datareturns.web.resource.v1.RecordResource;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class Linker {

    private UriInfo uriInfo;

    public Linker(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public String dataset(String datasetId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DatasetResource.class);
        ub.path(DatasetResource.class, "getDataset");
        ub.resolveTemplate("dataset_id", datasetId);
        return ub.build().toASCIIString();
    }

    public String record(String datasetId, String recordId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RecordResource.class);
        ub.path(RecordResource.class, "getRecord");
        ub.resolveTemplate("dataset_id", datasetId);
        ub.resolveTemplate("record_id", recordId);
        return ub.build().toASCIIString();
    }

    public String field(String payloadType, String fieldId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DefinitionsResource.class);
        ub.path(DefinitionsResource.class, "getFieldDefinition");
        ub.resolveTemplate("payload_type", payloadType);
        ub.resolveTemplate("field_id", fieldId);
        return ub.build().toASCIIString();
    }

    public String constraint(String payloadType, String contraintId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DefinitionsResource.class);
        ub.path(DefinitionsResource.class, "getValidationContraint");
        ub.resolveTemplate("payload_type", payloadType);
        ub.resolveTemplate("constraint_id", contraintId);
        return ub.build().toASCIIString();
    }

    public static Linker info(UriInfo uriInfo) {
        Linker l = new Linker(uriInfo);
        return l;
    }
}
