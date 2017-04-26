package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Submission;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SubmissionDao  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SubmissionDao.class);

    @PersistenceContext
    protected EntityManager entityManager;

    public Submission add(Submission submissionType) {
        entityManager.persist(submissionType);
        LOGGER.info("Added: submission: " + submissionType.toString());
        return submissionType;
    }

    public void merge(Submission submissionType) {
        entityManager.merge(submissionType);
        LOGGER.info("Merged: submission: " + submissionType.toString());
    }
}
