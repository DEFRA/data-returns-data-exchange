package uk.gov.ea.datareturns;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ea.datareturns.resource.SubmitReturnsResource.APP_STATUS_SUCCESS;
import static uk.gov.ea.datareturns.resource.SubmitReturnsResource.APP_STATUS_SUCCESS_WITH_ERRORS;
import static uk.gov.ea.datareturns.type.ApplicationException.EMPTY_FILE;
import static uk.gov.ea.datareturns.type.ApplicationException.FILE_KEY_MISMATCH;
import static uk.gov.ea.datareturns.type.ApplicationException.INSUFFICIENT_DATA;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_FILE_CONTENTS;
import static uk.gov.ea.datareturns.type.ApplicationException.INVALID_FILE_TYPE;
import static uk.gov.ea.datareturns.type.SystemException.FILE_UNLOCATABLE;
import static uk.gov.ea.datareturns.type.SystemException.NOTIFICATION;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Rule;
import org.junit.Test;

import uk.gov.ea.datareturns.config.TestConfiguration;
import uk.gov.ea.datareturns.domain.UploadFileResult;
import uk.gov.ea.datareturns.resource.SubmitReturnsResource;

import com.google.gson.Gson;

/**
 * @author adrianharrison
 * Test class to test SubmitReturnsResource
 * Notes: .....
 */
public class SubmitReturnsResourceTest
{
//	public final static MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");
//
//	public final static String TEST_FILES_PATH = "/testfiles";
//
//	public final static String FILE_CSV_SUCCESS = "success.csv";
//	public final static String FILE_CSV_FAILURES = "failures.csv";
//	public final static String FILE_CSV_EMPTY = "empty.csv";
//	public final static String FILE_CSV_INSUFFICIENT_DATA = "header-row-only.csv";
//	public final static String FILE_NON_CSV = "invalid-type.txt";
//	public final static String FILE_NON_TEXT = "binary.csv";
//
//	@Rule
//	public final ResourceTestRule resources = ResourceTestRule.builder().addResource(new SubmitReturnsResource(new TestConfiguration()))
//			.addResource(new MultiPartFeature()).build();
//
//	// E2E START
//	// TODO can't go any further yet as notification(s) will fail
////	@Test
////	public void testEndToEnd()
////	{
////		Response resp = performMultiPartUpload(FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
////		UploadFileResult result = resp.readEntity(UploadFileResult.class);
////
////		resp = resources.client().target("/submit-returns/validate").queryParam("fileKey", result.getFileKey())
////				.queryParam("returnType", result.getReturnType()).request().get(Response.class);
////		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
////
////	}
//	// E2E END
//
//	// UPLOAD STEP START
//	@Test
//	public void testEAIdentifierData()
//	{
//		final Response resp = performMultiPartUpload(FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
//		assertThat(result.getFileKey()).isNotEmpty();
//		assertThat(result.getEaId()).isNotEmpty();
//		assertThat(result.getSiteName()).isNotEmpty();
//		assertThat(result.getReturnType()).isNotEmpty();
//	}
//
//	@Test
//	public void testInvalidFileType()
//	{
//		final Response resp = performMultiPartUpload(FILE_NON_CSV, MediaType.TEXT_PLAIN_TYPE);
//		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_FILE_TYPE.getAppStatusCode());
//	}
//
//	@Test
//	public void testEmptyFileFailure()
//	{
//		final Response resp = performMultiPartUpload(FILE_CSV_EMPTY, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(EMPTY_FILE.getAppStatusCode());
//	}
//
//	@Test
//	public void testInsufficientData()
//	{
//		final Response resp = performMultiPartUpload(FILE_CSV_INSUFFICIENT_DATA, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(INSUFFICIENT_DATA.getAppStatusCode());
//	}
//
//	@Test
//	public void testInvalidContents()
//	{
//		final Response resp = performMultiPartUpload(FILE_NON_TEXT, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(INVALID_FILE_CONTENTS.getAppStatusCode());
//	}
//	// UPLOAD STEP END
//
//	// VALIDATE STEP START
//	@Test
//	public void testFileKeyMismatch()
//	{
//		final Response resp = resources.client().target("/submit-returns/validate").queryParam("fileKey", UUID.randomUUID().toString()).request()
//				.get(Response.class);
//		assertThat(resp.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
//
//		final UploadFileResult result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(FILE_KEY_MISMATCH.getAppStatusCode());
//	}
//
//	@Test
//	public void testMissingSchemaFile()
//	{
//		Response resp = performMultiPartUpload(FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		UploadFileResult result = resp.readEntity(UploadFileResult.class);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
//
//		resp = resources.client().target("/submit-returns/validate").queryParam("fileKey", result.getFileKey())
//				.queryParam("returnType", "no_such_return_type").request().get(Response.class);
//		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
//
//		final UploadFileResult result2 = getResultFromResponse(resp);
//		assertThat(result2.getAppStatusCode()).isEqualTo(FILE_UNLOCATABLE.getCode());
//	}
//
//	@Test
//	public void testWithValidationErrors()
//	{
//		Response resp = performMultiPartUpload(FILE_CSV_FAILURES, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		UploadFileResult result = resp.readEntity(UploadFileResult.class);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
//
//		resp = resources.client().target("/submit-returns/validate").queryParam("fileKey", result.getFileKey())
//				.queryParam("returnType", result.getReturnType()).request().get(Response.class);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		result = getResultFromResponse(resp);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS_WITH_ERRORS);
//		assertThat(result.getErrors().size()).isNotEqualTo(0);
//	}
//	// VALIDATE STEP END
//
//	// COMPLETE STEP START
//	@Test
//	public void testUserNotificationFailure()
//	{
//		Response resp = performMultiPartUpload(FILE_CSV_SUCCESS, MEDIA_TYPE_CSV);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		UploadFileResult result = resp.readEntity(UploadFileResult.class);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
//
//		resp = resources.client().target("/submit-returns/validate").queryParam("fileKey", result.getFileKey())
//				.queryParam("returnType", result.getReturnType()).request().get(Response.class);
//		assertThat(resp.getStatus()).isEqualTo(OK.getStatusCode());
//
//		result = resp.readEntity(UploadFileResult.class);
//		assertThat(result.getAppStatusCode()).isEqualTo(APP_STATUS_SUCCESS);
//
//		final FormDataMultiPart form = new FormDataMultiPart();
//		final FormDataBodyPart fdp1 = new FormDataBodyPart("fileKey", result.getFileKey());
//		form.bodyPart(fdp1);
//		final FormDataBodyPart fdp2 = new FormDataBodyPart("emailcc", "abc@abc.com");
//		form.bodyPart(fdp2);
//
//		resp = resources.client().target("/submit-returns/complete").request()
//				.post(Entity.entity(form, form.getMediaType()), Response.class);
//		assertThat(resp.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
//
//		result = resp.readEntity(UploadFileResult.class);
//		assertThat(result.getAppStatusCode()).isEqualTo(NOTIFICATION.getCode());
//	}
//	// COMPLETE STEP START
//
//	private Response performMultiPartUpload(String testFileName, MediaType mediaType)
//	{
//		final FormDataMultiPart form = new FormDataMultiPart();
//		final InputStream data = this.getClass().getResourceAsStream(makeFullPath(TEST_FILES_PATH, testFileName));
//
//		final StreamDataBodyPart fdp1 = new StreamDataBodyPart("fileUpload", data, testFileName, mediaType);
//
//		form.bodyPart(fdp1);
//
//		final Response resp = resources.client().register(MultiPartFeature.class).target("/submit-returns/upload").request()
//				.post(Entity.entity(form, form.getMediaType()), Response.class);
//
//		return resp;
//	}
//
//	private UploadFileResult getResultFromResponse(Response resp)
//	{
//		final Gson gson = new Gson();
//
//		return gson.fromJson(resp.readEntity(String.class), UploadFileResult.class);
//	}
//
//	private String makeFullPath(String path, String fileName)
//	{
//		return path + File.separator + fileName;
//	}
}
