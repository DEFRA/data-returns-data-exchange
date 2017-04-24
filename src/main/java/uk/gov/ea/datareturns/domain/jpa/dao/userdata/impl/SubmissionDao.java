package uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * @author Graham Willis
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SubmissionDao  {

    /**
     * Let the Dao class know the type of entity in order that type-safe
     * hibernate operations can be performed
     *
     */
    public SubmissionDao() {

    }
}
