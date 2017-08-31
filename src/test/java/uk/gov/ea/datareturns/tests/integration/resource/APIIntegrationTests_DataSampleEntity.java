package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.EntitySubstitution;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.PayloadEntityDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierSet;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Graham Willis
 * Integration test to the submission and dataset services
 * Deals with the basic mechanism - the details of the validations are tested
 * in the resource tests elsewhere.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class APIIntegrationTests_DataSampleEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIIntegrationTests_DataSampleEntity.class);

    @Inject private TestSettings testSettings;
    @Inject private SitePermitService sitePermitService;
    @Inject private DatasetService datasetService;
    @Inject private SubmissionService submissionService;
    @Inject private PayloadEntityDao payloadEntityDao;

    private static final String EMAIL_ADDR = "graham@email.com";
    private final static String SUBMISSION_SUCCESS = "json/landfill-success.json";
    private final static String SUBMISSION_FAILURE = "json/landfill-failure.json";
    private final static String SUBSTITUTIONS = "json/landfill-substitutions.json";

    // The basic structure used for the testing is as follows
    //Map<EaIdId, Map<DatasetIentifier, Map<RecordIdentifier, DataSamplePayload>>>
    private static Map<String, Map<String, Map<String, DataSamplePayload>>> successPayloadMap = new HashMap<>();
    private static Map<String, Map<String, Map<String, DataSamplePayload>>> failurePayloadMap = new HashMap<>();
    private static Map<String, Map<String, Map<String, DataSamplePayload>>> substitutionPayloadMap = new HashMap<>();

    @Before
    public void init() throws IOException {
        successPayloadMap = buildPayloadMap(submissionService.parseJsonArray(readTestFile(SUBMISSION_SUCCESS)));
        failurePayloadMap = buildPayloadMap(submissionService.parseJsonArray(readTestFile(SUBMISSION_FAILURE)));
        substitutionPayloadMap = buildPayloadMap(submissionService.parseJsonArray(readTestFile(SUBSTITUTIONS)));

        Set<String> permits = Stream.concat(
                Stream.concat(successPayloadMap.keySet().stream(), failurePayloadMap.keySet().stream()),
                    substitutionPayloadMap.keySet().stream()).distinct().collect(Collectors.toSet());

        // Remove any old data by removing the permits used in the tests
        permits.stream().forEach(p -> sitePermitService.removePermitSiteAndAliases(p));

        // Add sites and permits back in using the payload
        permitIterator(successPayloadMap, (eaIdId, datasetMap) ->
                datasetIterator(datasetMap, (datasetId, recordMap) -> recordIterator(recordMap, (recordId, payload) -> {
                    if (sitePermitService.getUniqueIdentifierByName(eaIdId) == null) {
                        try {
                            sitePermitService.addNewPermitAndSite(eaIdId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType.LARGE_LANDFILL_USERS, payload.getSiteName());
                        } catch (SitePermitService.SitePermitServiceException e) {
                            LOGGER.error("Error parsing successPayloadMap");
                        }
                    }
                })));

        permitIterator(failurePayloadMap, (eaIdId, datasetMap) ->
                datasetIterator(datasetMap, (datasetId, recordMap) -> recordIterator(recordMap, (recordId, payload) -> {
                    if (sitePermitService.getUniqueIdentifierByName(eaIdId) == null) {
                        try {
                            sitePermitService.addNewPermitAndSite(eaIdId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType.LARGE_LANDFILL_USERS, payload.getSiteName());
                        } catch (SitePermitService.SitePermitServiceException e) {
                            LOGGER.error("Error parsing failurePayloadMap");
                        }
                    }
                })));

        permitIterator(substitutionPayloadMap, (eaIdId, datasetMap) ->
                datasetIterator(datasetMap, (datasetId, recordMap) -> recordIterator(recordMap, (recordId, payload) -> {
                    if (sitePermitService.getUniqueIdentifierByName(eaIdId) == null) {
                        try {
                            sitePermitService.addNewPermitAndSite(eaIdId,
                                    UniqueIdentifierSet.UniqueIdentifierSetType.LARGE_LANDFILL_USERS, payload.getSiteName());
                        } catch (SitePermitService.SitePermitServiceException e) {
                            LOGGER.error("Error parsing substitutionPayloadMap");
                        }
                    }
                })));
    }

    // Test the basic creation and removal of test records
    @Test public void createAndRemoveDatasets() {
        permitIterator(successPayloadMap, (eaIdId, datasetMap) -> {
            datasetIterator(datasetMap, (datasetId, recordMap) -> {
                // Create the datasets
                DatasetEntity datasetEntity = new DatasetEntity();
                datasetEntity.setIdentifier(datasetId);
                datasetEntity.setOriginatorEmail(EMAIL_ADDR);
                datasetService.createDataset(eaIdId, datasetEntity);
                datasetEntity = datasetService.getDataset(eaIdId, datasetId);
                Assert.assertNotNull(datasetEntity);
                Assert.assertEquals(datasetId, datasetEntity.getIdentifier());
                Assert.assertNotNull(datasetEntity.getLastChangedDate());
                Assert.assertNotNull(datasetEntity.getCreateDate());
                Assert.assertEquals(EMAIL_ADDR, datasetEntity.getOriginatorEmail());
                Assert.assertEquals(DatasetEntity.Status.UNSUBMITTED, datasetEntity.getStatus());
            });

            UniqueIdentifier uniqueIdentifier = sitePermitService.getUniqueIdentifierByName(eaIdId);
            Assert.assertNotNull(uniqueIdentifier);
            Assert.assertEquals(uniqueIdentifier.getName(), eaIdId);
            Assert.assertNotNull(uniqueIdentifier.getDatasetChangedDate());
            Assert.assertEquals(uniqueIdentifier.getUniqueIdentifierSet().getUniqueIdentifierSetType(),
                    UniqueIdentifierSet.UniqueIdentifierSetType.LARGE_LANDFILL_USERS);

            Instant datasetChangedDate = uniqueIdentifier.getDatasetChangedDate();

            datasetIterator(datasetMap, (datasetId, recordMap) -> {
                datasetService.removeDataset(eaIdId, datasetId);
                Assert.assertNull(datasetService.getDataset(eaIdId, datasetId));
            });

            Assert.assertNotNull(uniqueIdentifier.getDatasetChangedDate());
            Assert.assertNotEquals(datasetChangedDate, uniqueIdentifier.getDatasetChangedDate());
        });
    }

    @Test public void createAndRemoveRecords() {
        permitIterator(successPayloadMap, (eaIdId, datasetMap) ->
                datasetIterator(datasetMap, (datasetId, recordMap) -> {
            // Create the datasets
            DatasetEntity datasetEntity = new DatasetEntity();
            datasetEntity.setIdentifier(datasetId);
            datasetEntity.setOriginatorEmail(EMAIL_ADDR);
            datasetService.createDataset(eaIdId, datasetEntity);

            // Create the records
            Map<String, RecordEntity> recordEntities = submissionService.createRecords(datasetEntity, recordMap);
            Assert.assertNotNull(recordEntities);
            Assert.assertEquals(recordEntities.size(), recordMap.size());

            recordIterator(recordMap, (recordId, payload) -> {
                RecordEntity recordEntity = recordEntities.get(recordId);
                Assert.assertEquals(recordId, recordEntity.getIdentifier());
                Assert.assertEquals(0, recordEntity.validationErrors.size());
                Assert.assertNotNull(recordEntity.getJson());
                Assert.assertEquals(recordEntity.getRecordStatus(), RecordEntity.RecordStatus.VALID);
                Assert.assertNotNull(recordEntity.getCreateDate());
                Assert.assertNotNull(recordEntity.getLastChangedDate());
            });

            Instant recordChangedDate = datasetEntity.getRecordChangedDate();
            Assert.assertNotNull(recordChangedDate);

            recordIterator(recordMap, (recordId, payload) -> {
                // Delete the records
                submissionService.removeRecord(datasetEntity, recordId);
                Assert.assertFalse(submissionService.recordExists(datasetEntity, recordId));
            });

            Assert.assertNotEquals(recordChangedDate, datasetEntity.getRecordChangedDate());
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            Assert.assertEquals(0, records.size());
        }));
    }

    @Test public void createAndSubmitValidRecords() {
        permitIterator(successPayloadMap, (String eaIdId, Map<String, Map<String, DataSamplePayload>> datasetMap) ->
                datasetIterator(datasetMap, (String datasetId, Map<String, DataSamplePayload> recordMap) -> {
            // Create the datasets
            DatasetEntity datasetEntity = new DatasetEntity();
            datasetEntity.setIdentifier(datasetId);
            datasetEntity.setOriginatorEmail(EMAIL_ADDR);
            datasetService.createDataset(eaIdId, datasetEntity);

            // Create the records
            Map<String, RecordEntity> recordEntities = submissionService.createRecords(datasetEntity, recordMap);

            try {
                submissionService.submit(datasetEntity);
            } catch (ProcessingException e) {
                e.printStackTrace();
            }

            recordIterator(recordMap, (recordId, payload) -> {
                RecordEntity recordEntity = recordEntities.get(recordId);
                Assert.assertEquals(recordEntity.getRecordStatus(), RecordEntity.RecordStatus.VALID);
                DataSampleEntity dataSamplePayload = payloadEntityDao.find(recordEntity.getId(), DataSampleEntity.class);
                Assert.assertNotNull(dataSamplePayload);
            });

            Assert.assertEquals(DatasetEntity.Status.SUBMITTED, datasetEntity.getStatus());
        }));
    }

    @Test public void createAndSubmitInvalidRecords() {
        permitIterator(failurePayloadMap, (String eaIdId, Map<String, Map<String, DataSamplePayload>> datasetMap) ->
                datasetIterator(datasetMap, (String datasetId, Map<String, DataSamplePayload> recordMap) -> {
            // Create the datasets
            DatasetEntity datasetEntity = new DatasetEntity();
            datasetEntity.setIdentifier(datasetId);
            datasetEntity.setOriginatorEmail(EMAIL_ADDR);
            datasetService.createDataset(eaIdId, datasetEntity);

            // Create the records
            submissionService.createRecords(datasetEntity, recordMap);

            try {
                submissionService.submit(datasetEntity);
            } catch (ProcessingException e) {
                Assert.assertEquals(0 ,0);
            }

            Assert.assertEquals(DatasetEntity.Status.UNSUBMITTED, datasetEntity.getStatus());
        }));
    }

    @Test public void testSubstitutions() {
        permitIterator(substitutionPayloadMap, (String eaIdId, Map<String, Map<String, DataSamplePayload>> datasetMap) -> {
            datasetIterator(datasetMap, (String datasetId, Map<String, DataSamplePayload> recordMap) -> {
                // Create the datasets
                DatasetEntity datasetEntity = new DatasetEntity();
                datasetEntity.setIdentifier(datasetId);
                datasetEntity.setOriginatorEmail(EMAIL_ADDR);
                datasetService.createDataset(eaIdId, datasetEntity);

                // Create the records
                submissionService.createRecords(datasetEntity, recordMap);
                try {
                    submissionService.submit(datasetEntity);
                } catch (ProcessingException e) {
                    Assert.assertEquals(0 ,0);
                }

                Assert.assertEquals(DatasetEntity.Status.SUBMITTED, datasetEntity.getStatus());
                Map<String, RecordEntity> recordEntities = submissionService.createRecords(datasetEntity, recordMap);
                Map<RecordEntity, Set<EntitySubstitution>> substitutions = submissionService.evaluateSubstitutes(recordEntities.values());
                Assert.assertNotNull(substitutions);
            });
        });
    }

    private void permitIterator(Map<String, Map<String, Map<String, DataSamplePayload>>> permitMap, BiConsumer<String, Map<String, Map<String, DataSamplePayload>>> function)  {
        permitMap.keySet().stream().forEach(p -> function.accept(p, permitMap.get(p)));
    }

    private void datasetIterator(Map<String, Map<String, DataSamplePayload>> datasetMap, BiConsumer<String, Map<String, DataSamplePayload>> function)  {
        datasetMap.keySet().stream().forEach(p -> function.accept(p, datasetMap.get(p)));
    }

    private void recordIterator(Map<String, DataSamplePayload> recordMap, BiConsumer<String, DataSamplePayload> function)  {
        recordMap.keySet().stream().forEach(p -> function.accept(p, recordMap.get(p)));
    }

    private static Map<String, Map<String, Map<String, DataSamplePayload>>> buildPayloadMap(List<Payload> payloads) {
        Map<String, DataSamplePayload> recordIdentifierMap = new HashMap<>();
        Map<String, Map<String, DataSamplePayload>> datasetIdentifierMap = new HashMap<>();
        Map<String, Map<String, Map<String, DataSamplePayload>>> eaIdIdentifierMap = new HashMap<>();
        payloads.sort(Comparator.comparing(o -> ((DataSamplePayload) o).getEaId()));

        String eaId = null;
        String prevEaId = null;

        // Arbitrarily make the max size of the dataset 2 to give multiple datasets for a given permit
        for (Payload payload : payloads) {
            if (payload instanceof DataSamplePayload) {
                eaId = ((DataSamplePayload) payload).getEaId();
                if (prevEaId == null) {
                    prevEaId = eaId;
                }
                // Max size of the dataset so add the accumulated record map into the dataset
                // and clear the map
                if (recordIdentifierMap.size() == 3) {
                    datasetIdentifierMap.put(UUID.randomUUID().toString(), recordIdentifierMap);
                    recordIdentifierMap = new HashMap<>();
                }
                // If the Id has changed add the accumulated dataset map into the EaId and clear both maps
                if (!eaId.equals(prevEaId)) {
                    if (recordIdentifierMap.size() != 0) {
                        datasetIdentifierMap.put(UUID.randomUUID().toString(), recordIdentifierMap);
                    }
                    eaIdIdentifierMap.put(prevEaId, datasetIdentifierMap);
                    datasetIdentifierMap = new HashMap<>();
                    recordIdentifierMap = new HashMap<>();
                    prevEaId = eaId;
                }
                recordIdentifierMap.put(UUID.randomUUID().toString(), (DataSamplePayload)payload);
            }
        }

        if (recordIdentifierMap.size() != 0) {
            datasetIdentifierMap.put(UUID.randomUUID().toString(), recordIdentifierMap);
        }

        if (datasetIdentifierMap.size() != 0) {
            eaIdIdentifierMap.put(eaId, datasetIdentifierMap);
        }


        return eaIdIdentifierMap;
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
        InputStream inputStream = APIIntegrationTests_DataSampleEntity.class.
                getResourceAsStream(testFile.getAbsolutePath());

        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
