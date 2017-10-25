package uk.gov.ea.datareturns.web.resource.v1.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.web.resource.v1.model.response.ErrorResponse;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * The {@link Preconditions} class provides conditional request handling logic and can be used both as a REST method parameter (marked
 * with the @{@link javax.ws.rs.BeanParam} annotation or as part of a request body data structure when used with a batch request.
 *
 * Precondition evaluation is delegated to the JAX-RS {@link Request} object when used as a REST method parameter and the functionality
 * to evaluate when used as a request body data structure has been derived from this.
 *
 * @author Sam Gardner-Delll
 */
@XmlRootElement(name = "preconditions")
public class Preconditions {
    @Context
    @ApiModelProperty(hidden = true)
    private Request request;

    @HeaderParam("If-Modified-Since")
    @JsonProperty("if_modified_since")
    @ApiModelProperty(
            name = "if_modified_since",
            notes = "Support for RFC7232 conditional requests based on last modification time",
            example = "\"0d210bfb2a0e1f1b4c082a6a0f79de07\""
    )
    private Date ifModifiedSince;

    @HeaderParam("If-Unmodified-Since")
    @JsonProperty("if_unmodified_since")
    @ApiModelProperty(
            name = "if_unmodified_since",
            notes = "Support for RFC7232 conditional requests based on last modification time",
            example = "\"0d210bfb2a0e1f1b4c082a6a0f79de07\""
    )
    private Date ifUnmodifiedSince;

    @HeaderParam("If-Match")
    @JsonProperty("if_match")
    @ApiModelProperty(name = "if_match", notes = "Support for RFC7232 conditional requests based on ETag")
    private String ifMatch;

    @HeaderParam("If-None-Match")
    @JsonProperty("if_none_match")
    @ApiModelProperty(name = "if_none_match", notes = "Support for RFC7232 conditional requests based on ETag")
    private String ifNoneMatch;

    public Preconditions() {

    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Date getIfModifiedSince() {
        return ifModifiedSince;
    }

    public void setIfModifiedSince(Date ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
    }

    public Date getIfUnmodifiedSince() {
        return ifUnmodifiedSince;
    }

    public void setIfUnmodifiedSince(Date ifUnmodifiedSince) {
        this.ifUnmodifiedSince = ifUnmodifiedSince;
    }

    public String getIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(String ifMatch) {
        this.ifMatch = ifMatch;
    }

    public String getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(String ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    /**
     * Evaluate preconditions for a resource that does not yet exist
     *
     * @return a {@link javax.ws.rs.core.Response.ResponseBuilder} with the correct response code if preconditions fail, or null if
     * the request should continue
     */
    public Response.ResponseBuilder evaluatePreconditions() {
        Response.ResponseBuilder rb = null;
        if (request != null) {
            // If the request object is present then this class has been used as a REST method @Context parameter, delegate to Jersey
            rb = request.evaluatePreconditions();
        } else {
            // Preconditions used as part of a batch request xml/json message
            if (StringUtils.isNotEmpty(this.ifMatch) && !"*".equals(this.ifMatch)) {
                rb = Response.status(Response.Status.PRECONDITION_FAILED);
            }
        }
        if (rb != null) {
            rb.entity(ErrorResponse.PRECONDITION_FAILED);
        }
        return rb;
    }

    /**
     * Evaluate preconditions for a resource with the given modification datestamp and ETag
     *
     * @return a {@link javax.ws.rs.core.Response.ResponseBuilder} with the correct response code if preconditions fail, or null if
     * the request should continue
     */
    public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
        Response.ResponseBuilder rb = null;
        if (request != null) {
            // If the request object is present then this class has been used as a REST method @Context parameter, delegate to Jersey
            rb = request.evaluatePreconditions(lastModified, eTag);
        } else {
            // Preconditions used as part of a batch request xml/json message
            if (lastModified == null) {
                throw new IllegalArgumentException("lastModified parameter cannot be null");
            } else if (eTag == null) {
                throw new IllegalArgumentException("eTag parameter cannot be null");
            }

            rb = this.checkIfMatch(eTag);
            if (rb == null) {
                // IfMatch passed, test ifUnmodifiedSince
                rb = this.checkIfUnmodifiedSince(lastModified.getTime());
                if (rb == null) {
                    if (StringUtils.isNotEmpty(this.ifNoneMatch)) {
                        rb = this.checkIfNoneMatch(eTag);
                        if (rb == null) {
                            return null;
                        }
                    }

                    if (this.ifModifiedSince != null) {
                        rb = this.checkIfModifiedSince(lastModified.getTime());
                        if (rb != null) {
                            rb.tag(eTag);
                        }
                    }
                }
            }
        }

        if (rb != null) {
            rb.entity(ErrorResponse.PRECONDITION_FAILED);
        }
        return rb;
    }

    private boolean isGetOrHeadRequest() {
        return false;
    }

    private Response.ResponseBuilder checkIfMatch(EntityTag eTag) {
        Response.ResponseBuilder rb = null;
        if (StringUtils.isNotEmpty(this.ifMatch)) {
            if (eTag.isWeak()) {
                rb = Response.status(Response.Status.PRECONDITION_FAILED);
            } else if (!"*".equals(this.ifMatch) && !eTag.equals(EntityTag.valueOf(this.ifMatch))) {
                rb = Response.status(Response.Status.PRECONDITION_FAILED);
            }
        }
        return rb;
    }

    private Response.ResponseBuilder checkIfNoneMatch(EntityTag eTag) {
        Response.ResponseBuilder rb = null;
        if (StringUtils.isNotEmpty(this.ifNoneMatch)) {
            if (this.isGetOrHeadRequest()) {
                if ("*".equals(this.ifNoneMatch)) {
                    rb = Response.notModified(eTag);
                } else {
                    EntityTag matchTag = EntityTag.valueOf(this.ifNoneMatch);
                    if (matchTag.equals(eTag) || matchTag.equals(new EntityTag(eTag.getValue(), !eTag.isWeak()))) {
                        rb = Response.notModified(eTag);
                    }
                }
            } else if (!eTag.isWeak()) {
                if ("*".equals(this.ifNoneMatch) || EntityTag.valueOf(this.ifNoneMatch).equals(eTag)) {
                    rb = Response.status(Response.Status.PRECONDITION_FAILED);
                }
            }
        }
        return rb;
    }

    private Response.ResponseBuilder checkIfUnmodifiedSince(long lastModified) {
        if (this.ifUnmodifiedSince != null) {
            long ifUnmodifiedSinceTime = this.ifUnmodifiedSince.getTime();
            if (roundDown(lastModified) > ifUnmodifiedSinceTime) {
                return Response.status(Response.Status.PRECONDITION_FAILED);
            }
        }
        return null;
    }

    private Response.ResponseBuilder checkIfModifiedSince(long lastModified) {
        if (this.ifModifiedSince != null) {
            if (isGetOrHeadRequest()) {
                long ifModifiedSinceTime = this.ifModifiedSince.getTime();
                if (roundDown(lastModified) <= ifModifiedSinceTime) {
                    return Response.notModified();
                }
            } else {
                return null;
            }
        }
        return null;
    }

    private static long roundDown(long time) {
        return time - time % 1000L;
    }

    // TODO: Investigate whether serializing objects is an appropriate means to generate an ETag
    public static EntityTag createEtag(Object... objects) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (Object o : objects) {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(o);
                oos.close();
            }
            MessageDigest m = MessageDigest.getInstance("MD5");
            String hexString = Hex.encodeHexString(m.digest(baos.toByteArray()));
            return new EntityTag(hexString);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ProcessingException("Error creating ETag", e);
        }
    }
}