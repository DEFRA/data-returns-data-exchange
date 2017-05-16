package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.MethodOrStandardDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.inject.Inject;

/**
 * DAO for monitoring methods and standards.
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MethodOrStandardDaoImpl extends AbstractEntityDao<MethodOrStandard> implements MethodOrStandardDao {
    @Inject
    public MethodOrStandardDaoImpl() {
        super(MethodOrStandard.class);
    }

    /**
     * For monitoring methods and standards it is safe to remove all whitespace from the keys
     *
     * @param inputValue the key to be relaxed
     * @return the relaxed version of the key
     */
    @Override public String generateMash(String inputValue) {
        return inputValue == null ? null : TextUtils.normalize(inputValue.toUpperCase(), TextUtils.WhitespaceHandling.REMOVE);
    }
}