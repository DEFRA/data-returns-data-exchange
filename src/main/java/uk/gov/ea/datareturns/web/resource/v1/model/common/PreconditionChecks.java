package uk.gov.ea.datareturns.web.resource.v1.model.common;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by sam on 03/07/17.
 */
public abstract class PreconditionChecks {

    // Utility class constructor
    private PreconditionChecks() {
    }

    public static Response.ResponseBuilder onPreconditionsPass(final PayloadType payloadEntityType, Preconditions preconditions,
            Supplier<Response.ResponseBuilder> handler) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (payloadEntityType == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Date lastModified = Date.from(payloadEntityType.getLastChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(payloadEntityType.getLastChangedDate()));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

    public static Response.ResponseBuilder onPreconditionsPass(final EntityBase entity, Preconditions
            preconditions, Supplier<Response
            .ResponseBuilder> handler) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (entity == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                rb = preconditions.evaluatePreconditions(entity.getLastModified(), Preconditions.createEtag(entity));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

    // Precondition evaluator for the entity list held at user level
    public static Response.ResponseBuilder onPreconditionsPass(
            final UniqueIdentifier uniqueIdentifier, List<DatasetEntity> datasets,
            Preconditions preconditions, Supplier<Response.ResponseBuilder> handler) {

        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (uniqueIdentifier == null || datasets == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Date lastModified = Date.from(uniqueIdentifier.getDatasetChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(datasets));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

    // Pre-condition evaluator for the ea-id list
    public static Response.ResponseBuilder onPreconditionsPass(final Date lastModified,
                                                               Preconditions preconditions,
                                                               Supplier<Response.ResponseBuilder> handler) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (lastModified == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(lastModified));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

    // Pre-condition evaluator for the ea-id held at the ea-id level
    public static Response.ResponseBuilder onPreconditionsPass(final UniqueIdentifier uniqueIdentifier,
                                                               Preconditions preconditions,
                                                               Supplier<Response.ResponseBuilder> handler) {
        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (uniqueIdentifier == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Date lastModified = Date.from(uniqueIdentifier.getLastChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(uniqueIdentifier));
            }
        }
        if (rb == null) {
            rb = handler.get();
        }
        return rb;
    }

}
