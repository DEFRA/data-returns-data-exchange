package uk.gov.defra.datareturns.tests.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Slf4j
public class TestListener implements TestExecutionListener {
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        log.info("Before Test Class: " + testContext.getTestClass());
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        log.info("Prepare Test Instance: " + testContext.getTestInstance());
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        log.info("Before Test Method: " + testContext.getTestClass() + "." + testContext.getTestMethod());

    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        log.info("After Test Method: " + testContext.getTestClass() + "." + testContext.getTestMethod());

    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        log.info("After Test Class: " + testContext.getTestClass());
    }
}
