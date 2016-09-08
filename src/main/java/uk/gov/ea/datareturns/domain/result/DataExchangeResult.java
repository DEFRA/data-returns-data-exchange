package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Top level response container for all requests to the {@link uk.gov.ea.datareturns.web.resource.DataExchangeResource}
 *
 * @author Sam Gardner-Dell
 */
public class DataExchangeResult {
    @JsonInclude(Include.NON_DEFAULT)
    private int appStatusCode;

    @JsonProperty("uploadResult")
    @JsonInclude(Include.NON_NULL)
    private UploadResult uploadResult;

    @JsonProperty("validationErrors")
    @JsonUnwrapped
    @JsonInclude(Include.NON_NULL)
    private ValidationErrors validationErrors;

    @JsonProperty("parseResult")
    @JsonInclude(Include.NON_NULL)
    private ParseResult parseResult;

    @JsonProperty("completeResult")
    @JsonInclude(Include.NON_NULL)
    private CompleteResult completeResult;

    /**
     * Create a new DataExchangeResult
     */
    public DataExchangeResult() {
        // Set up a default value for the app status code.  The serializer will omit the appStatusCode
        // unless it is set to a different value
        this.appStatusCode = -1;
    }

    /**
     * Create a new DataExchangeResult for the specified {@link UploadResult}
     *
     * @param uploadResult the {@link UploadResult} to associate with
     */
    public DataExchangeResult(final UploadResult uploadResult) {
        this();
        this.uploadResult = uploadResult;
    }

    /**
     * Create a new DataExchangeResult for the specified {@link CompleteResult}
     *
     * @param completeResult the {@link CompleteResult} to associate with
     */
    public DataExchangeResult(final CompleteResult completeResult) {
        this();
        this.completeResult = completeResult;
    }

    /**
     * @return the appStatusCode
     */
    public int getAppStatusCode() {
        return this.appStatusCode;
    }

    /**
     * @param appStatusCode the appStatusCode to set
     */
    public void setAppStatusCode(final int appStatusCode) {
        this.appStatusCode = appStatusCode;
    }

    /**
     * @return the uploadResult
     */
    public UploadResult getUploadResult() {
        return this.uploadResult;
    }

    /**
     * @param uploadResult the uploadResult to set
     */
    public void setUploadResult(final UploadResult uploadResult) {
        this.uploadResult = uploadResult;
    }

    /**
     * @return the validationErrors
     */
    public ValidationErrors getValidationErrors() {
        return this.validationErrors;
    }

    /**
     * @param validationErrors the validationErrors to set
     */
    public void setValidationErrors(final ValidationErrors validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * @return the parseResult
     */
    public ParseResult getParseResult() {
        return this.parseResult;
    }

    /**
     * @param parseResult the parseResult to set
     */
    public void setParseResult(final ParseResult parseResult) {
        this.parseResult = parseResult;
    }

    /**
     * @return the completeResult
     */
    public CompleteResult getCompleteResult() {
        return this.completeResult;
    }

    /**
     * @param completeResult the completeResult to set
     */
    public void setCompleteResult(final CompleteResult completeResult) {
        this.completeResult = completeResult;
    }
}