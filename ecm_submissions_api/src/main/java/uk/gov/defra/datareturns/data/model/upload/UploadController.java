package uk.gov.defra.datareturns.data.model.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.defra.datareturns.exceptions.ApplicationExceptionType;
import uk.gov.defra.datareturns.exceptions.CsvValidationException;
import uk.gov.defra.datareturns.service.csv.CsvStructureError;
import uk.gov.defra.datareturns.service.csv.EcmCsvReader;
import uk.gov.defra.datareturns.service.csv.EcmCsvResult;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * The {@link UploadController} adds a custom POST endpoint for the {@link Upload} entity to process multi-part file uploads of DEP compliant CSV
 * files
 *
 * @author Sam Gardner-Dell
 */
@ConditionalOnWebApplication
@RepositoryRestController
@Slf4j
@RequiredArgsConstructor
public class UploadController {
    /**
     * {@link EcmCsvReader} to process the uploaded CSV
     */
    private final EcmCsvReader csvReader;
    /**
     * spring data repository for the {@link Upload} entity
     */
    private final UploadRepository uploadRepository;

    /**
     * Handle a multipart upload of a DEP-compliant CSV file
     *
     * @param file      the {@link MultipartFile} object repesenting the uploaded file
     * @param assembler the SDR {@link PersistentEntityResourceAssembler} used to properly format the response data
     * @return a {@link ResponseEntity} to be served to the client
     * @throws IOException if an error occurs attempting to read the uploaded CSV data.
     */
    @PostMapping(value = "/uploads")
    public ResponseEntity<?> postUpload(@RequestParam("file") final MultipartFile file, final PersistentEntityResourceAssembler assembler)
            throws IOException {
        log.info("Processing upload " + file.getOriginalFilename());
        try {
            if (!file.getOriginalFilename().endsWith(".csv")) {
                throw new CsvValidationException(ApplicationExceptionType.FILE_TYPE_UNSUPPORTED, null,
                        "Unrecognised file extension " + FilenameUtils.getExtension(file.getOriginalFilename()));
            }
            if (file.getSize() == 0) {
                throw new CsvValidationException(ApplicationExceptionType.FILE_EMPTY, null, "No data present in " + file.getOriginalFilename());
            }

            final EcmCsvResult csvResult = csvReader.read(file.getOriginalFilename(), file.getBytes());

            log.info("Finished processing upload " + file.getOriginalFilename());

            if (!csvResult.hasViolations()) {
                final Upload upload = csvResult.getUpload();
                uploadRepository.saveAndFlush(upload);
                return new ResponseEntity<>(assembler.toResource(upload), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(ValidationExceptionMessage.of(ApplicationExceptionType.VALIDATION_ERRORS.getAppStatusCode(),
                        csvResult.getViolations()), HttpStatus.BAD_REQUEST);
            }
        } catch (final CsvValidationException e) {
            return new ResponseEntity<>(ValidationExceptionMessage.of(
                    e.getType().getAppStatusCode(),
                    Collections.singletonList(CsvStructureError.of(e.getType().getReason(), e.getMessage(), e.getLineNo()))
            ), HttpStatus.BAD_REQUEST);
        } finally {
            log.info("Response generated for upload " + file.getOriginalFilename());
        }
    }

    /**
     * Disable PUT/PATCH requests to the upload endpoint
     *
     * @return always returns a 405, "Method Not Allowed" response.
     */
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH}, value = "/uploads/*")
    public ResponseEntity<?> disabledMethods() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Custom validation error response - as per the spring data rest implementation but also extracts global errors.
     *
     * @author Sam Gardner-Dell
     */
    @Value(staticConstructor = "of")
    private static final class ValidationExceptionMessage<T> {
        private final int errorCode;
        @JsonProperty("errors")
        private final Collection<T> errors;
    }
}
