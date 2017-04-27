package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.AbstractUserDataDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Submission;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record_;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SubmissionDao {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SubmissionDao.class);

    @PersistenceContext
    protected EntityManager entityManager;

    public Submission persist(Submission submission) {
        entityManager.persist(submission);
        LOGGER.info("Adding: " + submission.getClass().getSimpleName() + ": " + submission.toString());
        return submission;
    }

    public void merge(Submission submission) {
        entityManager.merge(submission);
        LOGGER.info("Merging: " + submission.getClass().getSimpleName() + ": " + submission.toString());
    }

}
