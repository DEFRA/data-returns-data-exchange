package uk.gov.ea.datareturns.tests.integration.api.v1;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierSet;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

@Component
public class TestPermitData {

    @Inject
    private SitePermitService sitePermitService;

    private static SitePermitService service;

    public static void setService(SitePermitService service) {
        TestPermitData.service = service;
    }

    @PostConstruct
    private void init() {
        TestPermitData.setService(sitePermitService);
    }

    public static class TestData {
        public String testSiteName;
        public String uniqueId;
        public String[] aliases;

        protected TestData(String uniqueId, String testSiteName, String[] aliases) {
            this.testSiteName = testSiteName;
            this.uniqueId = uniqueId;
            this.aliases = aliases;
        }

        protected TestData(String testSiteName, String uniqueId) {
            this(testSiteName, uniqueId, null);
        }
    }

    protected final static TestPermitData.TestData[] TEST_DATA = new TestPermitData.TestData[] {
            new TestPermitData.TestData(UUID.randomUUID().toString().substring(0, 18), UUID.randomUUID().toString()),
            new TestPermitData.TestData(UUID.randomUUID().toString().substring(0, 18), UUID.randomUUID().toString()),
            new TestPermitData.TestData(UUID.randomUUID().toString().substring(0, 18), UUID.randomUUID().toString(),
                    new String[] {UUID.randomUUID().toString().substring(0, 18),
                            UUID.randomUUID().toString().substring(0, 18),
                            UUID.randomUUID().toString().substring(0, 18)})
    };

    // Remove any old data and set a user and dataset for use in the tests
    public static void createTestData() throws IOException, SitePermitService.SitePermitServiceException {
        for (TestPermitData.TestData p : TEST_DATA) {
            service.removePermitSiteAndAliases(p.uniqueId);
            service.addNewPermitAndSite(p.uniqueId,
                    UniqueIdentifierSet.UniqueIdentifierSetType.LARGE_LANDFILL_USERS,
                    p.testSiteName);
        }
    }

    // Remove any old data and set a user and dataset for use in the tests
    public static void destroyTestData() throws IOException, SitePermitService.SitePermitServiceException {
        for (TestPermitData.TestData p : TEST_DATA) {
            service.removePermitSiteAndAliases(p.uniqueId);
        }
    }

    public static TestPermitData.TestData[] getTestData() {
        return TEST_DATA;
   }
}
