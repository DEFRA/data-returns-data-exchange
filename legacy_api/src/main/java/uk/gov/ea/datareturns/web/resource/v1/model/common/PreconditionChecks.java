package uk.gov.ea.datareturns.web.resource.v1.model.common;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetCollection;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;

import javax.ws.rs.core.Response;
import java.util.Date;
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

    public static Response.ResponseBuilder onPreconditionsPass(final DatasetCollection collection,
            Preconditions preconditions, Supplier<Response.ResponseBuilder> handler) {

        Response.ResponseBuilder rb = null;
        if (preconditions != null) {
            if (collection == null) {
                rb = preconditions.evaluatePreconditions();
            } else {
                Date lastModified = Date.from(collection.getLastChangedDate());
                rb = preconditions.evaluatePreconditions(lastModified, Preconditions.createEtag(collection));
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
}
