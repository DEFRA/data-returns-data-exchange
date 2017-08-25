package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.EntitySubstitution;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Graham Willis
 * Integration test to the SubmissionServiceOld
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class APIIntegrationTests_DataSampleEntity {

    @Inject private TestSettings testSettings;
    @Inject private SitePermitService sitePermitService;
    @Inject private DatasetService datasetService;
    @Inject private SubmissionService submissionService;

    private final static String SUBMISSION_SUCCESS = "json/landfill-success.json";
    private final static String SUBMISSION_FAILURE = "json/landfill-failure.json";
    private final static String SUBSTITUTIONS = "json/landfill-substitutions.json";
    private static final String DATASET_ID = "DatasetEntity name";

    private static final String USER_NAME = "Graham Willis";
    private static final String ORIGINATOR_EMAIL = "graham.willis@email.com";
    private static final String[] RECORDS = { "BB001", "BB002", "BB003", "BB004" };

    private static User user;
    private static DatasetEntity dataset;
    private static List<Payload> samples;

    // Remove any old data and set a user and dataset for use in the tests
    @Before public void init() throws IOException {

        for (String record : RECORDS) {
            sitePermitService.removePermitSiteAndAliases(record);
        }

        for (String record : RECORDS) {
            sitePermitService.addNewPermitAndSite(record, "Test site");
            dataset = new DatasetEntity();
            dataset.setIdentifier(DATASET_ID + "_" + record);
            dataset.setOriginatorEmail(ORIGINATOR_EMAIL);
            datasetService.createDataset(record, dataset);
        }

        samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_SUCCESS));
    }

    // Test the basic creation and removal of test records
    @Test public void createTestRemoveRecords() {
        Map<String, Payload> initialPayload = buildPayloadMap(Arrays.asList(RECORDS), null);
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, initialPayload);

        Assert.assertEquals(RECORDS.length, recordEntities.size());
        for (String id : RECORDS) {
            Assert.assertTrue(submissionService.recordExists(dataset, id));
            RecordEntity rec = submissionService.getRecord(dataset, id);
            Assert.assertEquals(RecordEntity.RecordStatus.INVALID, rec.getRecordStatus());
            submissionService.removeRecord(dataset, id);
            Assert.assertFalse(submissionService.recordExists(dataset, id));
        }
    }

    @Test
    public void testThatRecordAdditionDeletionAndModificationUpdateTheDatasetChangeDate() {
        Map<String, Payload> initialPayload = buildPayloadMap(Arrays.asList(RECORDS), null);
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, initialPayload);

        DatasetEntity datasetEntity = datasetService.getDataset(DATASET_ID, user);
        Instant datasetEntityRecordCreateDate = datasetEntity.getRecordChangedDate();
        Assert.assertNotNull(datasetEntityRecordCreateDate);
        Assert.assertEquals(initialPayload.size(), recordEntities.size());

        // Modify a record and therefore update the dataset changed date
        Function<String, Payload> payloadSupplier = new Function<String, Payload>() {
            private int i = 0;

            @Override public Payload apply(String s) {
                return samples.get(i++);
            }
        };
        recordEntities = submissionService
                .createRecords(dataset, buildPayloadMap(Arrays.asList(RECORDS), payloadSupplier));

        datasetEntity = datasetService.getDataset(DATASET_ID, user);
        Instant datasetEntityRecordChangeDate = datasetEntity.getRecordChangedDate();
        Assert.assertNotNull(datasetEntityRecordChangeDate);
        Assert.assertNotEquals(datasetEntityRecordChangeDate, datasetEntityRecordCreateDate);

        // Validate the records - this should NOT set the dataset change date
        datasetEntity = datasetService.getDataset(DATASET_ID, user);
        Instant datasetEntityRecordValidateDate = datasetEntity.getRecordChangedDate();
        Assert.assertEquals(datasetEntityRecordValidateDate, datasetEntityRecordChangeDate);

        // Submit the records - this should set the dataset change date
        // because of the changed status on the dataset
        try {
            submissionService.submit(datasetEntity);
        } catch (ProcessingException e) {
            Assert.fail(e.getMessage());
        }
        datasetEntity = datasetService.getDataset(DATASET_ID, user);
        Instant datasetEntityRecordSubmitDate = datasetEntity.getRecordChangedDate();
        Assert.assertNotEquals(datasetEntityRecordSubmitDate, datasetEntityRecordChangeDate);

        // Delete a record and check that the dataset modification date has changed.
        submissionService.removeRecord(datasetEntity, RECORDS[1]);
        datasetEntity = datasetService.getDataset(DATASET_ID, user);
        Instant datasetEntityRecordDeleteDate = datasetEntity.getRecordChangedDate();
        Assert.assertNotEquals(datasetEntityRecordSubmitDate, datasetEntityRecordDeleteDate);
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewUserRecords() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(Arrays.asList(RECORDS), null));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.INVALID, r.getRecordStatus()));
    }

    // Create a set of new records with no associated data sample
    // and with a user identifier. The records
    // should all have a status of PERSISTED
    @Test public void createNewSystemRecords() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(Arrays.asList(RECORDS), null));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.INVALID, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a system identifier. The records
    // should all have a status of PARSED
    @Test public void createNewSystemRecordsWithSample() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(samples));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a set of new records using the associated data sample
    // and with a user identifier. The records
    // should all have a status of PARSED
    @Test public void createNewUserRecordsWithSample() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(samples));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a set of records and then associate data samples with them
    // as a secondary step
    @Test public void createNewUserRecordsAndAddSample() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(Arrays.asList(RECORDS), null));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.INVALID, r.getRecordStatus()));

        Map<String, Payload> payloadUpdates = new HashMap<>();
        for (String id : RECORDS) {
            payloadUpdates.put(id, samples.get(payloadUpdates.size()));
        }
        recordEntities = submissionService.createRecords(dataset, payloadUpdates);
        recordEntities.values().stream().forEach(r -> {
            Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus());
        });
    }

    // Create and validate a set of valid records
    @Test public void createAndValidateValidRecords() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(samples));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
    }

    // Create a valid set of records and submit them
    @Test public void createValidateAndSubmitRecords() {
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(samples));
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));
        try {
            submissionService.submit(dataset);
        } catch (ProcessingException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(DatasetEntity.Status.SUBMITTED, dataset.getStatus());
    }

    @Test public void createValidateAndDetermineSubstitutions() throws IOException {
        List<Payload> samples = submissionService.parseJsonArray(readTestFile(SUBSTITUTIONS));
        Map<String, Payload> payloadMap = buildPayloadMap(samples);
        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, payloadMap);
        recordEntities.values().stream().forEach(r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));

        int[] expectedSubs = { 4, 4, 4, 0 };
        Map<RecordEntity, Set<EntitySubstitution>> subMap = submissionService.evaluateSubstitutes(recordEntities.values());
        int index = 0;

        for (RecordEntity record : recordEntities.values()) {
            Set<EntitySubstitution> subs  = subMap.get(record);
            if (subs != null) {
                Assert.assertEquals(expectedSubs[index++], subs.size());
            }
        }
    }

    // Create and validate a set of valid and invalid records
    @Test public void createAndValidateValidAndInvalidRecords() throws IOException {
        List<Payload> samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE));

        Map<String, RecordEntity> recordEntities = submissionService.createRecords(dataset, buildPayloadMap(samples));
        Assert.assertEquals(1,
                recordEntities.values().stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID).count());
        Assert.assertEquals(3,
                recordEntities.values().stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.INVALID).count());
    }

    // Create and validate a set of valid and invalid records and submit them
    @Test public void createAndValidateValidAndInvalidAndSubmitRecords() throws IOException {
        List<Payload> samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE));
        Map<String, RecordEntity> recordEntities = submissionService
                .createRecords(dataset, samples.stream().collect(Collectors.toMap(o -> UUID.randomUUID().toString(), o -> o)));

        try {
            submissionService.submit(dataset);
            Assert.fail("Expected an exception to be thrown when attempting to submit a dataset with validation errors");
        } catch (ProcessingException e) {
            // Expected....
        }

        Assert.assertEquals(1,
                recordEntities.values().stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.VALID).count());
        Assert.assertEquals(3,
                recordEntities.values().stream().filter(r -> r.getRecordStatus() == RecordEntity.RecordStatus.INVALID).count());

        Assert.assertEquals(DatasetEntity.Status.UNSUBMITTED, dataset.getStatus());

    }

    // Create and validate a set of valid records, submit and retrieve them by dataset and dataset/identifier
    @Test public void createAndValidateAndSubmitAndRetrieveRecords() throws IOException {
        submissionService.createRecords(dataset, buildPayloadMap(samples));
        try {
            submissionService.submit(dataset);
        } catch (ProcessingException e) {
            Assert.fail(e.getMessage());
        }

        List<AbstractPayloadEntity> payloadEntities = submissionService.getPayloadList(dataset);
        payloadEntities.stream()
                .map(AbstractPayloadEntity::getRecordEntity)
                .forEach(
                        r -> Assert.assertEquals(RecordEntity.RecordStatus.VALID, r.getRecordStatus()));

        RecordEntity r = payloadEntities.get(0).getRecordEntity();
        String id = r.getIdentifier();

        RecordEntity r1 = submissionService.retrieve(dataset, id);
        Assert.assertEquals(r, r1);

        Assert.assertEquals(DatasetEntity.Status.SUBMITTED, dataset.getStatus());
    }

    @Test public void retrieveValidationErrors() throws IOException {
        List<Payload> samples = submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE));
        Map<String, RecordEntity> recordEntities = submissionService
                .createRecords(dataset, samples.stream().collect(Collectors.toMap(o -> UUID.randomUUID().toString(), o -> o)));
        List<Triple<String, String, String>> validationErrors = submissionService.retrieveValidationErrors(dataset);
        Assert.assertEquals(3, validationErrors.size());
    }

    /**
     * Reads the content of the test files and returns as a string
     * @param testFileName
     * @return
     * @throws IOException
     */
    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = APIIntegrationTests_DataSampleEntity.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private Map<String, Payload> buildPayloadMap(Collection<String> ids, Function<String, Payload> payloadSupplier) {
        Map<String, Payload> map = new LinkedHashMap<>();
        for (String id : ids) {
            Payload value = payloadSupplier != null ? payloadSupplier.apply(id) : null;
            map.put(id, value);
        }
        return map;
    }

    private Map<String, Payload> buildPayloadMap(Collection<Payload> payloads) {
        return payloads.stream()
                .collect(Collectors.toMap(o -> UUID.randomUUID().toString(),
                        o -> o,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }
}
