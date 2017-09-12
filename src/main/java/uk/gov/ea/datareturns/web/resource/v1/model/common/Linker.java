package uk.gov.ea.datareturns.web.resource.v1.model.common;

/**
 * Created by sam on 23/05/17.
 */

import uk.gov.ea.datareturns.web.resource.v1.DatasetResource;
import uk.gov.ea.datareturns.web.resource.v1.DefinitionsResource;
import uk.gov.ea.datareturns.web.resource.v1.EaIdResource;
import uk.gov.ea.datareturns.web.resource.v1.RecordResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.Link;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.PayloadReference;
import uk.gov.ea.datareturns.web.resource.v1.model.dataset.Dataset;
import uk.gov.ea.datareturns.web.resource.v1.model.eaid.EaId;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

public class Linker {

    private UriInfo uriInfo;

    public Linker(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public String eaId(String eaId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(EaIdResource.class);
        ub.path(EaIdResource.class, "getEaId");
        ub.resolveTemplate("ea_id", eaId);
        return ub.build().toASCIIString();
    }

    public String dataset(String eaId, String datasetId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DatasetResource.class);
        ub.path(DatasetResource.class, "getDataset");
        ub.resolveTemplate("ea_id", eaId);
        ub.resolveTemplate("dataset_id", datasetId);
        return ub.build().toASCIIString();
    }

    private String datasetList(String eaIdId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DatasetResource.class);
        ub.resolveTemplate("ea_id", eaIdId);
        return ub.build().toASCIIString();
    }

    public String status(String eaIdId, String datasetId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DatasetResource.class);
        ub.path(DatasetResource.class, "getDatasetStatus");
        ub.resolveTemplate("ea_id", eaIdId);
        ub.resolveTemplate("dataset_id", datasetId);
        return ub.build().toASCIIString();
    }

    public String record(String eaIdId, String datasetId, String recordId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RecordResource.class);
        ub.path(RecordResource.class, "getRecord");
        ub.resolveTemplate("ea_id", eaIdId);
        ub.resolveTemplate("dataset_id", datasetId);
        ub.resolveTemplate("record_id", recordId);
        return ub.build().toASCIIString();
    }

    public String recordsList(String eaIdId, String datasetId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RecordResource.class);
        ub.resolveTemplate("ea_id", eaIdId);
        ub.resolveTemplate("dataset_id", datasetId);
        return ub.build().toASCIIString();
    }

    public String fieldList(String payloadType) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DefinitionsResource.class);
        ub.path(DefinitionsResource.class, "listFields");
        ub.resolveTemplate("payload_type", payloadType);
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

    public String constraintList(String payloadType) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DefinitionsResource.class);
        ub.path(DefinitionsResource.class, "listValidationConstraints");
        ub.resolveTemplate("payload_type", payloadType);
        return ub.build().toASCIIString();
    }

    public String constraint(String payloadType, String contraintId) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(DefinitionsResource.class);
        ub.path(DefinitionsResource.class, "getValidationConstraint");
        ub.resolveTemplate("payload_type", payloadType);
        ub.resolveTemplate("constraint_id", contraintId);
        return ub.build().toASCIIString();
    }

    public void resolve(EaId eaId) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", eaId(eaId.getId())));
        links.add(new Link("datasets", datasetList(eaId.getId())));
        eaId.setLinks(links);
    }

    public void resolve(String eaIdId, Dataset dataset) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", dataset(eaIdId, dataset.getId())));
        links.add(new Link("records", recordsList(eaIdId, dataset.getId())));
        links.add(new Link("status", status(eaIdId, dataset.getId())));
        dataset.setLinks(links);
    }

    public void resolve(String eaIdId, String datasetId, Record record) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", record(eaIdId, datasetId, record.getId())));
        links.add(new Link("dataset", dataset(eaIdId, datasetId)));
        record.setLinks(links);
    }

    public void resolve(PayloadReference reference) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("fields", fieldList(reference.getId())));
        links.add(new Link("constraints", constraintList(reference.getId())));
        reference.setLinks(links);
    }

    public static Linker info(UriInfo uriInfo) {
        Linker l = new Linker(uriInfo);
        return l;
    }
}