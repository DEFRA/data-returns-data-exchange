package uk.gov.defra.datareturns.data.model.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.dataset.DatasetRepository;
import uk.gov.defra.datareturns.data.model.record.RecordRepository;
import uk.gov.defra.datareturns.exceptions.ValidationException;
import uk.gov.defra.datareturns.service.csv.ECMCSVReader;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ConditionalOnWebApplication
@RepositoryRestController
@Slf4j
@RequiredArgsConstructor
public class UploadController {
    private final ECMCSVReader csvReader;
    private final UploadRepository uploadRepository;
    private final DatasetRepository datasetRepository;
    private final RecordRepository recordRepository;
    private final Validator validator;

//    private final EntityLinks entityLinks;

    @PostMapping(value = "/uploads")
    public ResponseEntity<?> postUpload(@RequestParam("file") final MultipartFile file,
                                        final PersistentEntityResourceAssembler assembler) throws IOException {
        try {
            final List<Dataset> datasets = csvReader.read(file.getBytes());

            final Upload upload = new Upload();
            upload.setFilename(file.getOriginalFilename());
            upload.setDatasets(datasets);
            uploadRepository.saveAndFlush(upload);

            final Set<ConstraintViolation<Dataset>> constraintViolations = new LinkedHashSet<>();
            for (final Dataset dataset : datasets) {
                constraintViolations.addAll(validator.validate(dataset));

                if (constraintViolations.isEmpty()) {
                    dataset.setUpload(upload);
                    datasetRepository.save(dataset);
                    recordRepository.save(dataset.getRecords());
                }
            }

            if (constraintViolations.isEmpty()) {
                return new ResponseEntity<>(assembler.toResource(upload), HttpStatus.OK);
            } else {
                log.error("Constraint violations found");
                return new ResponseEntity<>(assembler.toResource(constraintViolations), HttpStatus.BAD_REQUEST);
            }
        } catch (final ValidationException e) {
            log.warn("Error processing file", e);
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/uploads/{upload_id}")
    public ResponseEntity<?> putUpload(@RequestParam("file") final MultipartFile file) throws IOException {
        log.info("PUT Received: {}", IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8));

        final Upload upload = new Upload();
        upload.setFilename(file.getOriginalFilename());
        uploadRepository.saveAndFlush(upload);
        return new ResponseEntity<>(upload, HttpStatus.OK);
    }
}
